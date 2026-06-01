	package com.javiercondecortes.servicio;

import com.javiercondecortes.modelo.usuarios;

public interface IUsuariosService {
    
    /**
     * Guarda un usuario encriptando su contraseña automáticamente.
     * Se usa tanto para registro como para actualización.
     */
    void saveUsuarios(usuarios usuario);
    
    /**
     * Elimina un usuario de la base de datos.
     */
    void deleteUsuarios(Long id);
    
    /**
     * Busca un usuario por su nombre. 
     * Útil para perfiles públicos o validaciones de nombres de usuario.
     */
    usuarios findByNombre(String nombre);

    /**
     * CRÍTICO PARA SEGURIDAD: Busca por email.
     * Este es el método que usará JWT para identificar al usuario logueado.
     */
    usuarios findByEmail(String email);

    /**
     * Opcional: Verifica si un email ya existe antes de registrar.
     */
    boolean existeEmail(String email);
}