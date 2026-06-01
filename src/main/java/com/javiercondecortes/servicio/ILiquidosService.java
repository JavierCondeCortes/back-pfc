package com.javiercondecortes.servicio;

import java.util.List;
import com.javiercondecortes.modelo.liquidos;

public interface ILiquidosService {

    /**
     * Muestra todos los líquidos asociados a un vehículo.
     * Usamos Long vehiculoId para facilitar el filtrado desde el Controlador.
     */
    List<liquidos> findByVehiculoId(Long vehiculoId);
    
    /**
     * Busca un tipo de líquido específico (ej: "Aceite") en un vehículo concreto.
     */
    liquidos findByTipoAndVehiculoId(String tipo, Long vehiculoId);
    
    /**
     * SEGURIDAD: Listar todos los líquidos de un usuario.
     * Útil para el Dashboard general del TFC.
     */
    List<liquidos> findByUsuarioId(Long usuarioId);
    
    /**
     * Guarda o actualiza la información de un líquido.
     */
    void saveLiquidos(liquidos liquido);
    
    /**
     * Elimina un registro de líquido por su ID.
     * Es más seguro que pasar el objeto entero.
     */
    void deleteLiquidos(Long id);

	liquidos findById(Long id);
}