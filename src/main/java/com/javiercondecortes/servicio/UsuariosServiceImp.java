package com.javiercondecortes.servicio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.javiercondecortes.DAO.IUsuariosDAO;
import com.javiercondecortes.modelo.usuarios;

@Service
public class UsuariosServiceImp implements IUsuariosService {

    @Autowired
    private IUsuariosDAO dao;

    // Instanciamos el encriptador de Spring Security
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @Override
    @Transactional
    public void saveUsuarios(usuarios usuario) {
        // SEGURIDAD: Solo encriptamos si la contraseña no está ya encriptada
        // Esto evita "re-encriptar" un hash si actualizamos otros datos del usuario
        if (usuario.getPassword() != null && !usuario.getPassword().startsWith("$2a$")) {
            String encodedPassword = encoder.encode(usuario.getPassword());
            usuario.setPassword(encodedPassword);
        }
        dao.save(usuario);
    }

    @Override
    @Transactional
    public void deleteUsuarios(Long id) {
        dao.deleteById(id); 
    }

    @Override
    @Transactional(readOnly = true)
    public usuarios findByNombre(String nombre) {
        // .orElse(null) evita errores de puntero nulo en el controlador
        return dao.findByNombre(nombre).orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public usuarios findByEmail(String email) {
        // Este es el método que usará tu JwtUtil para validar el usuario
        return dao.findByEmail(email).orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existeEmail(String email) {
        return dao.findByEmail(email).isPresent();
    }
}