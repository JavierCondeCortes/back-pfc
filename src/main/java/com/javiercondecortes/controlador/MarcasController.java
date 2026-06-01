package com.javiercondecortes.controlador;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.javiercondecortes.modelo.marcas;
import com.javiercondecortes.servicio.IMarcasService;

@RestController
@RequestMapping("/api/marcas")
@CrossOrigin(origins = "*")
public class MarcasController {

    @Autowired
    private IMarcasService service;

    /**
     * Listar todas las marcas disponibles.
     * POST /api/marcas/todas
     */
    @PostMapping("/todas")
    public List<marcas> listarTodas() {
        return service.findAll();
    }

    /**
     * Buscar marca por nombre.
     * POST /api/marcas/buscar -> { "nombre": "BMW" }
     */
    @PostMapping("/buscar")
    public ResponseEntity<marcas> buscar(@RequestBody Map<String, String> payload) {
        marcas marca = service.findByNombre(payload.get("nombre"));
        return (marca != null) ? ResponseEntity.ok(marca) : ResponseEntity.notFound().build();
    }

    /**
     * Registrar una marca nueva.
     * POST /api/marcas/guardar
     */
    @PostMapping("/guardar")
    public ResponseEntity<String> guardar(@RequestBody marcas marca) {
        service.saveMarcas(marca);
        return ResponseEntity.ok("Marca guardada correctamente.");
    }

    /**
     * Eliminar marca por ID.
     * POST /api/marcas/eliminar -> { "id": 1 }
     */
    @PostMapping("/eliminar")
    public ResponseEntity<String> eliminar(@RequestBody Map<String, Long> payload) {
        try {
            service.deleteMarcas(payload.get("id"));
            return ResponseEntity.ok("Marca eliminada con éxito.");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error al eliminar la marca.");
        }
    }
}