package com.javiercondecortes.DAO;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.javiercondecortes.modelo.vehiculos;

@Repository
public interface IVehiculosDAO extends JpaRepository<vehiculos, Long> {
	
    List<vehiculos> findByUsuarioId(Long usuarioId);

    Optional<vehiculos> findByMatriculaAndUsuarioId(String matricula, Long usuarioId);

    Optional<vehiculos> findByMatricula(String matricula);
    
    boolean existsByIdAndUsuarioId(Long id, Long usuarioId);

}