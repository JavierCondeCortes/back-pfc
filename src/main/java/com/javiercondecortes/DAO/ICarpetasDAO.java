package com.javiercondecortes.DAO;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.javiercondecortes.modelo.carpetas;

@Repository
public interface ICarpetasDAO extends JpaRepository<carpetas, Long> {

    // Devuelve solo las carpetas del usuario logueado
    List<carpetas> findByUsuarioId(Long usuarioId);

    // Permite buscar una carpeta específica asegurando que el dueño es el correcto
    carpetas findByIdAndUsuarioId(Long carpetaId, Long usuarioId);
}