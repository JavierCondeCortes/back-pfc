package com.javiercondecortes.DAO;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.javiercondecortes.modelo.marcas;

@Repository
public interface IMarcasDAO extends JpaRepository<marcas, Long> {
    // Busca una marca por su nombre exacto
    Optional<marcas> findByNombre(String nombre);
}