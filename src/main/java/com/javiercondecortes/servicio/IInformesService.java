package com.javiercondecortes.servicio;

import java.util.List;
import com.javiercondecortes.modelo.informes;

public interface IInformesService {
    
    // Buscar por ID de vehículo asociado
    List<informes> findByVehiculoId(Long vehiculoId);
    
    // Buscar por ID de usuario (Historial general)
    List<informes> findByUsuarioId(Long usuarioId);
    
    // Buscar informes dentro de una carpeta específica
    List<informes> findByCarpetaId(Long carpetaId);
    
    // Guardar informe (se encarga de crear o actualizar)
    void saveInformes(informes informe);
    
    // Eliminar por ID (más seguro que pasar el objeto entero)
    void deleteInformes(Long id);

    informes findById(Long id);

}