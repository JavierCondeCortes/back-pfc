package com.javiercondecortes.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.javiercondecortes.DAO.IUsuariosDAO;

@Configuration
public class ApplicationConfig {

    private final IUsuariosDAO usuarioDao;

    public ApplicationConfig(IUsuariosDAO usuarioDao) {
        this.usuarioDao = usuarioDao;
    }

    /**
     * Define cómo recuperar el usuario de la base de datos usando el email.
     */
    @Bean
    public UserDetailsService userDetailsService() {
        return username -> usuarioDao.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario: " + username + " no encontrado"));
    }

    /**
     * Configura el proveedor de autenticación.
     * Se usa el constructor con argumentos para evitar el error de compilación.
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        // En lugar de usar el constructor vacío, usamos el que recibe el servicio
        // Esto soluciona el error "reason: actual and formal argument lists differ in length"
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailsService());
        
        // Configuramos el codificador de contraseñas
        authProvider.setPasswordEncoder(passwordEncoder());
        
        return authProvider;
    }
    

    /**
     * Gestiona la autenticación de la aplicación.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * Define el algoritmo de hash para las contraseñas.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}