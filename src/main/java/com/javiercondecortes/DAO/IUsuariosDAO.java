package com.javiercondecortes.DAO;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.javiercondecortes.modelo.usuarios;


@Repository
public interface IUsuariosDAO extends JpaRepository<usuarios, Long> {
    // Principal para JWT y Login
    Optional<usuarios> findByEmail(String email);
    
    // Para validaciones de perfil
    Optional<usuarios> findByNombre(String nombre);
}