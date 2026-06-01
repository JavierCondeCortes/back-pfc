package com.javiercondecortes.modelo;

import java.util.Date;
import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "usuarios")
public class usuarios implements UserDetails {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String nombre;

    @Column(unique = true, nullable = false)
    private String email;
    
    @Column(nullable = false)
    private String rol;

    private Boolean verificacion_email = false;
    
    @Column(nullable = false)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY) 
    private String password; // Cambiado a minúscula por convención y seguridad
    
    private Date fecha_alta;

    @PrePersist
    protected void onCreate() {
        fecha_alta = new Date();
    }
    
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
	    // Spring Security espera que los roles empiecen por "ROLE_" 
	    // Si en tu DB tienes "ADMIN", aquí devolvemos "ROLE_ADMIN"
	    return List.of(new SimpleGrantedAuthority("ROLE_" + this.rol));
	}

    public String getRol() {
		return rol;
	}

	public void setRol(String rol) {
		this.rol = rol;
	}

	@Override
    public String getUsername() {
        return email; // Tu "username" para el login será el email
    }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return true; }
    
    public String getPassword() { return password; }
    public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Boolean getVerificacion_email() {
		return verificacion_email;
	}

	public void setVerificacion_email(Boolean verificacion_email) {
		this.verificacion_email = verificacion_email;
	}

	public Date getFecha_alta() {
		return fecha_alta;
	}

	public void setFecha_alta(Date fecha_alta) {
		this.fecha_alta = fecha_alta;
	}

	public void setPassword(String password) { this.password = password; }
}