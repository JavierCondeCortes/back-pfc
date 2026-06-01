package com.javiercondecortes.DAO;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.javiercondecortes.modelo.liquidos;

@Repository
public interface ILiquidosDAO extends JpaRepository<liquidos, Long> {

    // Buscar todos los líquidos de un vehículo (filtrado por ID del objeto vehiculo)
    List<liquidos> findByVehiculoId(Long vehiculoId);

    // SEGURIDAD: Buscar líquidos validando que pertenecen al usuario logueado
    List<liquidos> findByUsuarioId(Long usuarioId);

    /**
     * Busca un líquido específico (ej: Aceite) para un coche concreto.
     * Usamos el nombre del campo exacto en tu modelo: "tipo"
     */
    Optional<liquidos> findByTipoAndVehiculoId(String tipo, Long vehiculoId);

    /**
     * MÉTODO DE SEGURIDAD MÁXIMA: 
     * Busca un líquido por su ID pero asegura que el dueño es el usuario que hace la petición.
     */
    Optional<liquidos> findByIdAndUsuarioId(Long id, Long usuarioId);
    
    void deleteByVehiculoId(Long vehiculoId);
}