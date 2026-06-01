package com.javiercondecortes.controlador;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import com.javiercondecortes.modelo.carpetas;
import com.javiercondecortes.modelo.usuarios;
import com.javiercondecortes.servicio.ICarpetasService;
import com.javiercondecortes.servicio.IUsuariosService;

@RestController
@RequestMapping("/api/carpetas")
@CrossOrigin(origins = "*")
public class CarpetasController {

    @Autowired
    private ICarpetasService service;
    @Autowired
    private IUsuariosService usuarioService;

    private usuarios getUsuarioAutenticado() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return usuarioService.findByEmail(auth.getName());
    }

    // 1. MOSTRAR CONTENIDO (MIS CARPETAS)
    // No necesita Body, sacamos el ID del token por seguridad
    @PostMapping("/mis-carpetas")
    public ResponseEntity<?> listar() {
        try {
            usuarios user = getUsuarioAutenticado();
            List<carpetas> lista = service.listarPorUsuario(user.getId());
            return ResponseEntity.ok(lista);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error al obtener carpetas.");
        }
    }

    // 2. CREAR / GUARDAR
    @PostMapping("/guardar")
    public ResponseEntity<String> guardar(@RequestBody carpetas carpeta) {
        try {
            usuarios user = getUsuarioAutenticado();
            
            // Asignamos el usuario del token para que nadie cree carpetas a nombre de otro
            carpeta.setUsuario(user);
            
            service.guardar(carpeta);
            return ResponseEntity.ok("Carpeta creada con éxito.");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error al crear la carpeta.");
        }
    }

    // 3. EDITAR (SOLO NOMBRE)
    @PostMapping("/editar")
    public ResponseEntity<String> editar(@RequestBody carpetas carpetaEditada) {
        try {
            usuarios user = getUsuarioAutenticado();
            carpetas carpetaDb = service.findById(carpetaEditada.getId());

            if (carpetaDb == null) return ResponseEntity.status(404).body("Carpeta no encontrada.");

            // Validar propiedad
            if (!carpetaDb.getUsuario().getId().equals(user.getId())) {
                return ResponseEntity.status(403).body("No tienes permiso sobre esta carpeta.");
            }

            // Solo actualizamos el nombre para no romper las relaciones con informes aquí
            carpetaDb.setNombre(carpetaEditada.getNombre());

            service.guardar(carpetaDb);
            return ResponseEntity.ok("Carpeta actualizada.");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error al editar.");
        }
    }

    // 4. ELIMINAR
    @PostMapping("/eliminar")
    public ResponseEntity<String> eliminar(@RequestBody Map<String, Object> payload) {
        try {
            Object idRaw = payload.get("id");
            if (idRaw == null) return ResponseEntity.badRequest().body("ID no proporcionado.");

            Long idCarpeta = Long.valueOf(idRaw.toString());
            usuarios user = getUsuarioAutenticado();
            carpetas carpetaDb = service.findById(idCarpeta);

            if (carpetaDb == null) return ResponseEntity.status(404).body("La carpeta no existe.");

            // Seguridad: Dueño o ADMIN
            if (carpetaDb.getUsuario().getId().equals(user.getId()) || user.getRol().equals("ADMIN")) {
                service.eliminar(idCarpeta);
                return ResponseEntity.ok("Carpeta eliminada.");
            }

            return ResponseEntity.status(403).body("No tienes permiso.");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error al eliminar.");
        }
    }
    
    /**
     * Obtener el contenido (informes) de una carpeta específica.
     * POST /api/carpetas/contenido -> Body JSON: { "id": 10 }
     */
    @PostMapping("/contenido")
    public ResponseEntity<?> verContenido(@RequestBody Map<String, Object> payload) {
        try {
            // 1. Extraer y validar el ID del JSON
            Object idRaw = payload.get("id");
            if (idRaw == null) return ResponseEntity.badRequest().body("ID de carpeta no proporcionado.");
            
            Long idCarpeta = Long.valueOf(idRaw.toString());
            
            // 2. Obtener el usuario del Token JWT
            usuarios user = getUsuarioAutenticado();
            
            // 3. Buscar la carpeta en la DB
            carpetas carpeta = service.findById(idCarpeta);

            if (carpeta == null) {
                return ResponseEntity.status(404).body("La carpeta no existe.");
            }

            // 4. SEGURIDAD: Validar que la carpeta pertenece al usuario que la pide
            if (!carpeta.getUsuario().getId().equals(user.getId())) {
                return ResponseEntity.status(403).body("No tienes permiso para ver el contenido de esta carpeta.");
            }

            // 5. Devolver la carpeta
            // Gracias al @ManyToMany y al Set<informes>, Jackson enviará la lista de informes automáticamente
            return ResponseEntity.ok(carpeta);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error al obtener el contenido: " + e.getMessage());
        }
    }
    
    @PostMapping("/vincular")
    public ResponseEntity<String> vincular(@RequestBody Map<String, Long> payload) {
        Long carpetaId = payload.get("carpetaId");
        Long informeId = payload.get("informeId");
        usuarios user = getUsuarioAutenticado();

        if (service.vincularInforme(carpetaId, informeId, user.getId())) {
            return ResponseEntity.ok("Informe añadido a la carpeta");
        }
        return ResponseEntity.status(403).body("No se pudo vincular el informe");
    }

    @PostMapping("/desvincular")
    public ResponseEntity<String> desvincular(@RequestBody Map<String, Long> payload) {
        Long carpetaId = payload.get("carpetaId");
        Long informeId = payload.get("informeId");
        usuarios user = getUsuarioAutenticado();

        if (service.desvincularInforme(carpetaId, informeId, user.getId())) {
            return ResponseEntity.ok("Informe quitado de la carpeta");
        }
        return ResponseEntity.status(403).body("No se pudo desvincular el informe");
    }
}