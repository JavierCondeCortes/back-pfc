package com.javiercondecortes.servicio;

import java.util.List;
import com.javiercondecortes.modelo.mantenimientos;

public interface IMantenimientosService {

    // Búsqueda de mantenimientos asociados a un coche por su ID
    List<mantenimientos> findByVehiculoId(Long vehiculoId);
    
    // Búsqueda de mantenimientos asociados a un usuario por su ID
    List<mantenimientos> findByUsuarioId(Long usuarioId);
    
    // Guardar o actualizar un registro
    void saveMantenimiento(mantenimientos mantenimiento);
    
    // Eliminar registro por su ID (más seguro que pasar el objeto entero)
    void deleteMantenimiento(Long id);

	mantenimientos findById(Long id);
}