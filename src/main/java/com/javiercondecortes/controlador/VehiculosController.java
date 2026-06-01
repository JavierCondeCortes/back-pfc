package com.javiercondecortes.controlador;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.javiercondecortes.modelo.vehiculos;
import com.javiercondecortes.modelo.usuarios;
import com.javiercondecortes.servicio.IVehiculosService;
import com.javiercondecortes.servicio.IUsuariosService;

@RestController
@RequestMapping("/api/vehiculos")
@CrossOrigin(origins = "*")
public class VehiculosController {

    @Autowired
    private IVehiculosService service;

    @Autowired
    private IUsuariosService usuarioService;

    /**
     * Obtiene el usuario autenticado desde el contexto de seguridad (Token JWT).
     */
    private usuarios getUsuarioAutenticado() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        return usuarioService.findByEmail(email);
    }

    /**
     * Listar MIS vehículos (el ID se extrae del token).
     * POST /api/vehiculos/mis-vehiculos -> Ya no necesita Body con ID.
     */
    @PostMapping("/mis-vehiculos")
    public ResponseEntity<List<vehiculos>> listarPorUsuario() {
        usuarios user = getUsuarioAutenticado();
        List<vehiculos> lista = service.findByUsuarioId(user.getId());
        return ResponseEntity.ok(lista);
    }

    /**
     * Detalle de un vehículo del usuario.
     * POST /api/vehiculos/detalle -> Body JSON: { "matricula": "1234ABC" }
     */
    @PostMapping("/detalle")
    public ResponseEntity<vehiculos> buscarPorMatricula(@RequestBody Map<String, String> payload) {
        usuarios user = getUsuarioAutenticado();
        String matricula = payload.get("matricula");
        
        vehiculos vehiculo = service.findByMatriculaUsuario(matricula, user.getId());
        
        if (vehiculo == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(vehiculo);
    }

    /**
     * Guardar vehículo: Se asigna automáticamente el dueño mediante el Token.
     * POST /api/vehiculos/guardar -> Body JSON: { "marca": "Seat", ... } (Sin usuario)
     */
    @PostMapping("/guardar")
    public ResponseEntity<String> guardar(@RequestBody vehiculos vehiculo) {
        try {
            usuarios user = getUsuarioAutenticado();
            vehiculo.setUsuario(user); // Vinculación automática
            
            service.saveVehiculos(vehiculo);
            return ResponseEntity.ok("Vehículo registrado correctamente.");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error al procesar: " + e.getMessage());
        }
    }

    @PostMapping("/editar")
    public ResponseEntity<String> editar(@RequestBody vehiculos vehiculoEditado) {
        try {
            usuarios usuarioLogueado = getUsuarioAutenticado();
            
            // 1. Buscar el vehículo original
            vehiculos vehiculoDb = service.findById(vehiculoEditado.getId());

            if (vehiculoDb == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Vehículo no encontrado.");
            }

            // 2. SEGURIDAD: ¿Es el dueño?
            if (!vehiculoDb.getUsuario().getId().equals(usuarioLogueado.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No tienes permiso para editar este vehículo.");
            }

            // 3. ACTUALIZACIÓN SELECTIVA
            vehiculoDb.setModelo(vehiculoEditado.getModelo());
            vehiculoDb.setKm_recorridos(vehiculoEditado.getKm_recorridos());
            vehiculoDb.setPegatina(vehiculoEditado.getPegatina());
            vehiculoDb.setTipo_combustible(vehiculoEditado.getTipo_combustible());
            vehiculoDb.setUltima_fecha_itv(vehiculoEditado.getUltima_fecha_itv());
            // NO editamos 'matricula' ni 'usuario' para mantener la integridad

            service.saveVehiculos(vehiculoDb);
            return ResponseEntity.ok("Vehículo actualizado correctamente.");

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }
    
    /**
     * Eliminar vehículo.
     * POST /api/vehiculos/eliminar -> Body JSON: { "id": 10 }
     */
    @PostMapping("/eliminar")
    public ResponseEntity<String> eliminar(@RequestBody Map<String, Long> payload) {
        try {
            usuarios usuarioAutenticado = getUsuarioAutenticado();
            Long idVehiculo = payload.get("id");

            if (usuarioAutenticado == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario no autenticado.");
            }

            if (!service.existsByIdAndUsuarioId(idVehiculo, usuarioAutenticado.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No tienes permiso para eliminar este vehículo.");
            }

            service.deleteVehiculos(idVehiculo);
            return ResponseEntity.ok("Vehículo eliminado con éxito.");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }

}