package com.javiercondecortes.controlador;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import com.javiercondecortes.modelo.mantenimientos;
import com.javiercondecortes.modelo.usuarios;
import com.javiercondecortes.modelo.vehiculos;
import com.javiercondecortes.servicio.IMantenimientosService;
import com.javiercondecortes.servicio.IUsuariosService;
import com.javiercondecortes.servicio.IVehiculosService;

@RestController
@RequestMapping("/api/mantenimientos")
@CrossOrigin(origins = "*")
public class MantenimientosController {

    @Autowired
    private IMantenimientosService service;
    @Autowired
    private IUsuariosService usuarioService;
    @Autowired
    private IVehiculosService vehiculoService;

    private usuarios getUsuarioAutenticado() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return usuarioService.findByEmail(auth.getName());
    }

    // 1. LISTAR POR VEHÍCULO (CON SEGURIDAD)
    @PostMapping("/vehiculo")
    public ResponseEntity<?> listarPorVehiculo(@RequestBody Map<String, Long> payload) {
        try {
            usuarios user = getUsuarioAutenticado();
            Long idCoche = payload.get("vehiculoId");

            vehiculos coche = vehiculoService.findById(idCoche); 

            if (coche == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Vehículo no encontrado.");
            }

            // Validamos propiedad
            if (!coche.getUsuario().getId().equals(user.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No tienes permiso para ver estos mantenimientos.");
            }

            List<mantenimientos> lista = service.findByVehiculoId(idCoche);
            return ResponseEntity.ok(lista);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }

    // 2. GUARDAR (NUEVO)
    @PostMapping("/guardar")
    public ResponseEntity<String> guardar(@RequestBody mantenimientos mant) {
        try {
            usuarios user = getUsuarioAutenticado();
            
            // 1. Validar que el coche existe y pertenece al usuario
            if (mant.getVehiculo() == null || mant.getVehiculo().getId() == null) {
                return ResponseEntity.badRequest().body("Error: ID de vehículo no proporcionado.");
            }
            
            vehiculos coche = vehiculoService.findById(mant.getVehiculo().getId());

            if (coche == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("El vehículo no existe.");
            }

            if (!coche.getUsuario().getId().equals(user.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No puedes añadir mantenimientos a un coche ajeno.");
            }

            // 2. IMPORTANTE: Asignar el usuario del token al mantenimiento
            // Sin esta línea, la DB dará error de "usuario_id cannot be null"
            mant.setUsuario(user);

            service.saveMantenimiento(mant);
            return ResponseEntity.ok("Mantenimiento guardado correctamente.");
        } catch (Exception e) {
            e.printStackTrace(); // Mira la consola de Eclipse para ver el error real
            return ResponseEntity.internalServerError().body("Error al guardar: " + e.getMessage());
        }
    }

    // 3. EDITAR (BLOQUEANDO CAMBIO DE VEHÍCULO)
    @PostMapping("/editar")
    public ResponseEntity<String> editar(@RequestBody mantenimientos mantEditado) {
        try {
            usuarios usuarioLogueado = getUsuarioAutenticado();
            
            // 1. Buscamos por el ID (Asegúrate de tener el método getId() en el modelo)
            mantenimientos mantDb = service.findById(mantEditado.getId());

            if (mantDb == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No existe el registro.");
            }

            // 2. Validación de seguridad
            if (!mantDb.getUsuario().getId().equals(usuarioLogueado.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No tienes permiso.");
            }

            // 3. ACTUALIZACIÓN SELECTIVA (Nombres corregidos según tu modelo)
            mantDb.setComponente_cambiado(mantEditado.getComponente_cambiado());
            mantDb.setDescripcion(mantEditado.getDescripcion());
            mantDb.setKm_cambiado(mantEditado.getKm_cambiado());
            mantDb.setCosto(mantEditado.getCosto());
            mantDb.setFecha_cambio(mantEditado.getFecha_cambio());

            // El vehículo y el usuario NO se tocan para mantener la integridad
            service.saveMantenimiento(mantDb);
            
            return ResponseEntity.ok("Mantenimiento actualizado correctamente.");

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }

    // 4. ELIMINAR (USANDO OBJETO DE BD PARA VALIDAR)
    @PostMapping("/eliminar")
    public ResponseEntity<String> eliminar(@RequestBody Map<String, Object> payload) {
        try {
            // 1. Extraemos el ID del JSON
            Object idObj = payload.get("id");
            if (idObj == null) {
                return ResponseEntity.badRequest().body("Error: No se proporcionó el ID.");
            }
            Long idMant = Long.valueOf(idObj.toString());

            // 2. Buscamos el mantenimiento REAL en la base de datos
            // No te fíes de lo que venga en el JSON, busca siempre en la DB
            mantenimientos mantDb = service.findById(idMant);

            if (mantDb == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("El mantenimiento no existe.");
            }

            // 3. Obtenemos quién es el usuario que hace la petición (Token)
            usuarios usuarioLogueado = getUsuarioAutenticado();

            // 4. LÓGICA DE PROPIEDAD:
            // Comparamos el ID del dueño del vehículo asociado al mantenimiento 
            // con el ID del usuario del Token.
            Long idDuenoCoche = mantDb.getVehiculo().getUsuario().getId();
            Long idUserToken = usuarioLogueado.getId();

            if (idDuenoCoche.equals(idUserToken) || usuarioLogueado.getRol().equals("ADMIN")) {
                // Si es el dueño o es ADMIN, procedemos al borrado
                service.deleteMantenimiento(idMant);
                return ResponseEntity.ok("Mantenimiento eliminado correctamente.");
            } else {
                // Si intenta borrar algo que no es suyo
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                                     .body("No tienes permiso: este mantenimiento pertenece a otro usuario.");
            }

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error al eliminar: " + e.getMessage());
        }
    }
    
 // 5. HISTORIAL GLOBAL (Todos los mantenimientos del usuario)
    @PostMapping("/historial")
    public ResponseEntity<?> listarMisMantenimientos() {
        try {
            // Obtenemos el usuario desde el Token
            usuarios user = getUsuarioAutenticado();

            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario no encontrado.");
            }

            // El service debe buscar por el ID del usuario
            List<mantenimientos> lista = service.findByUsuarioId(user.getId());

            if (lista.isEmpty()) {
                return ResponseEntity.noContent().build();
            }

            return ResponseEntity.ok(lista);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error al recuperar el historial: " + e.getMessage());
        }
    }
}