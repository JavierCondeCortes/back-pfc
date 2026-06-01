package com.javiercondecortes.controlador;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.javiercondecortes.modelo.liquidos;
import com.javiercondecortes.modelo.usuarios;
import com.javiercondecortes.modelo.vehiculos;
import com.javiercondecortes.servicio.ILiquidosService;
import com.javiercondecortes.servicio.IUsuariosService;
import com.javiercondecortes.servicio.IVehiculosService;

@RestController
@RequestMapping("/api/liquidos")
@CrossOrigin(origins = "*")
public class LiquidosController {

    @Autowired
    private ILiquidosService service;
    @Autowired
    private IUsuariosService usuarioService;
    @Autowired
    private IVehiculosService vehiculoService;

    private usuarios getUsuarioAutenticado() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return usuarioService.findByEmail(auth.getName());
    }

    // 1. LISTAR POR VEHÍCULO (SEGURIDAD DE PROPIEDAD)
    @PostMapping("/por-vehiculo")
    public ResponseEntity<?> listarPorCoche(@RequestBody Map<String, Long> payload) {
        try {
            usuarios user = getUsuarioAutenticado();
            Long vehiculoId = payload.get("vehiculoId");

            vehiculos coche = vehiculoService.findById(vehiculoId);
            if (coche == null) return ResponseEntity.status(404).body("Vehículo no encontrado.");

            if (!coche.getUsuario().getId().equals(user.getId())) {
                return ResponseEntity.status(403).body("No tienes permiso para ver estos datos.");
            }

            return ResponseEntity.ok(service.findByVehiculoId(vehiculoId));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }

    // 2. HISTORIAL GLOBAL (Igual que Informes y Mantenimientos)
    @PostMapping("/historial")
    public ResponseEntity<?> verHistorial() {
        try {
            usuarios user = getUsuarioAutenticado();
            // Asegúrate de tener findByUsuarioId en tu ILiquidosService
            List<liquidos> lista = service.findByUsuarioId(user.getId());
            return ResponseEntity.ok(lista);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error al obtener historial.");
        }
    }

    // 3. GUARDAR (ASIGNACIÓN AUTOMÁTICA DE USUARIO)
    @PostMapping("/guardar")
    public ResponseEntity<String> guardar(@RequestBody liquidos liquido) {
        try {
            usuarios user = getUsuarioAutenticado();
            vehiculos coche = vehiculoService.findById(liquido.getVehiculo().getId());

            if (coche == null) return ResponseEntity.status(404).body("Vehículo no existe.");

            if (!coche.getUsuario().getId().equals(user.getId())) {
                return ResponseEntity.status(403).body("No puedes añadir registros a un coche ajeno.");
            }

            liquido.setUsuario(user);
            service.saveLiquidos(liquido);
            return ResponseEntity.ok("Líquido guardado con éxito.");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error al guardar.");
        }
    }

    // 4. EDITAR (PROTECCIÓN DE INTEGRIDAD)
    @PostMapping("/editar")
    public ResponseEntity<String> editar(@RequestBody liquidos liquidoEditado) {
        try {
            usuarios user = getUsuarioAutenticado();
            liquidos liquidoDb = service.findById(liquidoEditado.getId());

            if (liquidoDb == null) return ResponseEntity.status(404).body("Registro no encontrado.");

            // Validar que el líquido pertenece al usuario del token
            if (!liquidoDb.getUsuario().getId().equals(user.getId())) {
                return ResponseEntity.status(403).body("No tienes permiso para editar este registro.");
            }

            // Actualización selectiva (No permitimos cambiar vehiculo_id ni usuario_id)
            liquidoDb.setNombre(liquidoEditado.getNombre());
            liquidoDb.setTipo(liquidoEditado.getTipo());
            liquidoDb.setKm_para_cambio(liquidoEditado.getKm_para_cambio());
            liquidoDb.setTiempo_para_cambio(liquidoEditado.getTiempo_para_cambio());

            service.saveLiquidos(liquidoDb);
            return ResponseEntity.ok("Registro actualizado correctamente.");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error al editar.");
        }
    }

    // 5. ELIMINAR (MISMA LÓGICA DE MANTENIMIENTOS)
    @PostMapping("/eliminar")
    public ResponseEntity<String> eliminar(@RequestBody Map<String, Object> payload) {
        try {
            Object idRaw = payload.get("id");
            if (idRaw == null) return ResponseEntity.badRequest().body("ID no proporcionado.");

            Long idLiquido = Long.valueOf(idRaw.toString());
            usuarios user = getUsuarioAutenticado();
            liquidos liquidoDb = service.findById(idLiquido);

            if (liquidoDb == null) return ResponseEntity.status(404).body("El registro no existe.");

            // Permitir si es dueño o ADMIN
            if (liquidoDb.getUsuario().getId().equals(user.getId()) || user.getRol().equals("ADMIN")) {
                service.deleteLiquidos(idLiquido);
                return ResponseEntity.ok("Registro eliminado.");
            }

            return ResponseEntity.status(403).body("No tienes permiso para eliminar este líquido.");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error al eliminar.");
        }
    }
}