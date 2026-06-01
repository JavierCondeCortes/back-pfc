package com.javiercondecortes.controlador;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import com.javiercondecortes.modelo.informes;
import com.javiercondecortes.modelo.usuarios;
import com.javiercondecortes.modelo.vehiculos;
import com.javiercondecortes.servicio.IInformesService;
import com.javiercondecortes.servicio.IUsuariosService;
import com.javiercondecortes.servicio.IVehiculosService;

@RestController
@RequestMapping("/api/informes")
public class InformesController {
	
	@Autowired
	private IVehiculosService vehiculoService; // Asegúrate de usar el nombre de la Interfaz
	
	@Autowired
    private IInformesService service;

    @Autowired
    private IUsuariosService usuarioService; // Necesario para buscar al usuario del token

    /**
     * Extrae el email del Token JWT y busca al usuario en la DB
     */
    private usuarios getUsuarioAutenticado() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        return usuarioService.findByEmail(email);
    }

    @PostMapping("/guardar")
    public ResponseEntity<String> guardar(@RequestBody informes informe) {
        try {
            // 1. Usuario del Token
            usuarios usuarioLogueado = getUsuarioAutenticado();
            
            // 2. Buscar el vehículo que viene en el JSON para comprobar quién es el dueño
            // Nota: Necesitas inyectar IVehiculosService en este controlador
            vehiculos vehiculoDb = vehiculoService.findById(informe.getVehiculo().getId());

            if (vehiculoDb == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("El vehículo no existe.");
            }

            // 3. VALIDACIÓN DE SEGURIDAD:
            // ¿El dueño del vehículo es el mismo que está intentando subir el informe?
            if (!vehiculoDb.getUsuario().getId().equals(usuarioLogueado.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                                     .body("Error: No puedes añadir informes a un vehículo que no te pertenece.");
            }

            // 4. Si pasa la validación, asignamos usuario y guardamos
            informe.setUsuario(usuarioLogueado);
            service.saveInformes(informe);
            
            return ResponseEntity.ok("Informe guardado correctamente y vinculado a tu vehículo.");

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error al procesar: " + e.getMessage());
        }
    }
    // POST /api/informes/historial -> { "usuarioId": 5 }
    @PostMapping("/historial")
    public ResponseEntity<?> listarMisInformes() {
        try {
            // 1. Obtenemos el usuario del contexto de seguridad
            usuarios user = getUsuarioAutenticado();

            // 2. Verificación de seguridad básica (por si el token es válido pero el usuario fue borrado)
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario no encontrado.");
            }

            // 3. Obtenemos la lista
            List<informes> lista = service.findByUsuarioId(user.getId());

            // 4. Si la lista está vacía, devolvemos un 204 No Content (opcional) o la lista vacía
            if (lista.isEmpty()) {
                return ResponseEntity.noContent().build();
            }

            return ResponseEntity.ok(lista);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error al recuperar el historial: " + e.getMessage());
        }
    }

    // POST /api/informes/vehiculo -> { "vehiculoId": 12 }
    @PostMapping("/vehiculo")
    public ResponseEntity<?> listarPorVehiculo(@RequestBody Map<String, Long> payload) {
        try {
            // 1. Obtener el usuario autenticado desde el Token
            usuarios usuarioLogueado = getUsuarioAutenticado();
            Long vehiculoId = payload.get("vehiculoId");

            // 2. Buscar el vehículo en la DB para verificar la propiedad
            // (Inyecta IVehiculosService si no lo has hecho ya)
            vehiculos vehiculoDb = vehiculoService.findById(vehiculoId);

            if (vehiculoDb == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("El vehículo no existe.");
            }

            // 3. SEGURIDAD: ¿El dueño del vehículo es el usuario del Token?
            if (!vehiculoDb.getUsuario().getId().equals(usuarioLogueado.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                                     .body("Error: No tienes permiso para ver los informes de este vehículo.");
            }

            // 4. Si todo es correcto, devolvemos la lista de informes
            List<informes> lista = service.findByVehiculoId(vehiculoId);
            return ResponseEntity.ok(lista);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error al procesar la solicitud.");
        }
    }

    // POST /api/informes/eliminar -> { "id": 45 }
    @PostMapping("/eliminar")
    public ResponseEntity<String> eliminar(@RequestBody Map<String, Long> payload) {
        try {
            usuarios usuarioLogueado = getUsuarioAutenticado();
            Long idInforme = payload.get("id");

            // 1. Buscamos el informe en la base de datos (aquí Hibernate carga el vehículo y el usuario)
            informes informeDb = service.findById(idInforme);

            if (informeDb == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("El informe no existe.");
            }

            // 2. SEGURIDAD: Comparamos el dueño del informe de la DB con el usuario del Token
            // IMPORTANTE: No uses informeDb.getVehiculo() aquí si solo quieres validar al dueño
            if (!informeDb.getUsuario().getId().equals(usuarioLogueado.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                                     .body("No tienes permiso para borrar este informe.");
            }

            // 3. Borramos
            service.deleteInformes(idInforme);
            return ResponseEntity.ok("Informe eliminado correctamente.");

        } catch (Exception e) {
            // Esto te ayudará a ver en la consola de Eclipse/IntelliJ qué línea falla exactamente
            e.printStackTrace(); 
            return ResponseEntity.internalServerError().body("Error al procesar: " + e.getMessage());
        }
    }
    
    @PostMapping("/editar")
    public ResponseEntity<String> editar(@RequestBody informes informeEditado) {
        try {
            usuarios usuarioLogueado = getUsuarioAutenticado();
            
            // 1. Recuperamos el informe original de la base de datos
            informes informeDb = service.findById(informeEditado.getId());

            if (informeDb == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("El informe no existe.");
            }

            // 2. SEGURIDAD: ¿El informe pertenece al usuario del Token?
            if (!informeDb.getUsuario().getId().equals(usuarioLogueado.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                                     .body("Error: No tienes permiso para editar este informe.");
            }

            // 3. ACTUALIZACIÓN SELECTIVA:
            // Solo modificamos campos informativos. 
            // NO tocamos ni 'usuario' ni 'vehiculo' del objeto informeDb.
            informeDb.setNombre(informeEditado.getNombre());
            informeDb.setCosto(informeEditado.getCosto());
            informeDb.setRuta_doc(informeEditado.getRuta_doc());
            informeDb.setFecha_informe(informeEditado.getFecha_informe());

            // 4. Guardamos el objeto original con los campos cambiados
            service.saveInformes(informeDb);
            
            return ResponseEntity.ok("Informe actualizado (el vehículo asignado se mantiene inalterable).");

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error al editar: " + e.getMessage());
        }
    }
}