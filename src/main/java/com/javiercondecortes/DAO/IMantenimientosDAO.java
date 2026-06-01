package com.javiercondecortes.DAO;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.javiercondecortes.modelo.mantenimientos;

@Repository
public interface IMantenimientosDAO extends JpaRepository<mantenimientos, Long> {

    // Filtrado seguro por ID de vehículo
    List<mantenimientos> findByVehiculoId(Long vehiculoId);

    // Filtrado seguro por ID de usuario (Crítico para privacidad)
    List<mantenimientos> findByUsuarioId(Long usuarioId);
}