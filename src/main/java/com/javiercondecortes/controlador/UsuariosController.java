package com.javiercondecortes.controlador;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.javiercondecortes.modelo.usuarios;
import com.javiercondecortes.servicio.IUsuariosService;
import com.javiercondecortes.seguridad.JwtService;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "*")
public class UsuariosController {

    @Autowired
    private IUsuariosService service;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtService jwtService;

    /**
     * REGISTRO: Encripta la contraseña con BCrypt antes de guardar.
     */
    @PostMapping("/registro")
    public ResponseEntity<?> registrar(@RequestBody usuarios usuario) {
        try {
            if (service.findByEmail(usuario.getEmail()) != null) {
                return ResponseEntity.badRequest().body("El email ya está registrado.");
            }
            
            // Encriptación vital para que Spring Security lo reconozca después
            usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
            service.saveUsuarios(usuario);
            
            return ResponseEntity.status(HttpStatus.CREATED).body("Usuario registrado con éxito.");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error en el registro.");
        }
    }

    /**
     * LOGIN JWT: Valida contra la base de datos y genera el token.
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credenciales) {
        try {
            // 1. Esto dispara internamente el UserDetailsService y valida el Password con BCrypt
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(credenciales.get("email"), credenciales.get("password"))
            );

            // 2. Si llegamos aquí, el usuario es válido. Buscamos sus datos para el token.
            usuarios user = service.findByEmail(credenciales.get("email"));
            
            // Generamos el token incluyendo info extra si quieres (ej: id del usuario)
            Map<String, Object> extraClaims = new HashMap<>();
            extraClaims.put("userId", user.getId());
            extraClaims.put("nombre", user.getNombre());

            String token = jwtService.generateToken(user.getEmail(), extraClaims);

            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("user", user); // Opcional: devolver datos del usuario para el Front

            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            // Si el password no coincide o el usuario no existe, saltará aquí
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Email o contraseña incorrectos.");
        }
    }

    /**
     * ELIMINAR CUENTA
     */
    @PostMapping("/eliminar-cuenta")
    public ResponseEntity<String> eliminarCuentaPropia() {
        try {
            // 1. Extraemos el email del usuario autenticado (desde el Token)
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String emailDesdeToken = auth.getName(); 

            // 2. Buscamos al usuario en la DB
            usuarios usuarioActual = service.findByEmail(emailDesdeToken);

            if (usuarioActual == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado.");
            }

            // 3. Borramos al usuario. 
            // IMPORTANTE: Al borrar el usuario, si tienes configurado "CascadeType.ALL", 
            // también se borrarán sus coches automáticamente.
            service.deleteUsuarios(usuarioActual.getId());

            return ResponseEntity.ok("Tu cuenta ha sido eliminada correctamente. Lamentamos verte partir.");

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error al intentar eliminar la cuenta.");
        }
    }
    
    @PostMapping("/editar-perfil")
    public ResponseEntity<String> editarPerfil(@RequestBody usuarios datosEditados) {
        try {
            // 1. Obtener el usuario que está logueado según su Token
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String emailToken = auth.getName();
            usuarios usuarioDb = service.findByEmail(emailToken);

            if (usuarioDb == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado.");
            }

            // 2. ACTUALIZACIÓN DE CAMPOS PERMITIDOS
            // Solo dejamos cambiar el nombre. El email y el rol están bloqueados.
            usuarioDb.setNombre(datosEditados.getNombre());
            
            // Si quieres permitir cambio de password, debe ir encriptada:
            // if(datosEditados.getPassword() != null) {
            //    usuarioDb.setPassword(passwordEncoder.encode(datosEditados.getPassword()));
            // }

            service.saveUsuarios(usuarioDb);
            return ResponseEntity.ok("Perfil actualizado correctamente.");

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error al actualizar perfil.");
        }
    }
    
    @PostMapping("/admin/eliminar-usuario")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> adminEliminarUsuario(@RequestBody Map<String, Long> payload) {
        try {
            Long idABorrar = payload.get("id");
            
            // Llamamos al método exacto que tienes en tu objeto servicio
            service.deleteUsuarios(idABorrar); 
            
            return ResponseEntity.ok("Usuario eliminado correctamente por el Admin.");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }
}