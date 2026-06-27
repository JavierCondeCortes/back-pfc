package com.javiercondecortes.DAO;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.javiercondecortes.modelo.informes;

@Repository
public interface IInformesDAO extends JpaRepository<informes, Long> {

    // Buscar por ID de usuario (Seguridad JWT)
    List<informes> findByUsuarioId(Long usuarioId);

    // Buscar por ID de vehículo (Filtro por coche)
    List<informes> findByVehiculoId(Long vehiculoId);

    // Buscar por ID de carpeta (Relación ManyToMany)
    List<informes> findByCarpetasId(Long carpetaId);

    // Validación cruzada de seguridad
    Optional<informes> findByIdAndUsuarioId(Long id, Long usuarioId);
    
    @Modifying
    @Query(
        value = "DELETE FROM informes_carpetas WHERE informe_id IN (SELECT id FROM informes WHERE vehiculo_id = :vehiculoId)",
        nativeQuery = true
    )
    void deleteCarpetasLinksByVehiculoId(@Param("vehiculoId") Long vehiculoId);

    void deleteByVehiculoId(Long vehiculoId);
}
