package com.javiercondecortes.modelo;

import java.util.Date;
import java.util.Set;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

@Entity
@Table(name = "informes")
public class informes {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String nombre;
    
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name="vehiculo_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler","usuario"})
    private vehiculos vehiculo;
    
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name="usuario_id", nullable = false)
    @JsonIgnoreProperties({
        "hibernateLazyInitializer", 
        "handler", 
        "password", 
        "authorities", 
        "enabled", 
        "accountNonLocked", 
        "accountNonExpired", 
        "credentialsNonExpired",
        "authorities",
        "username"
    })
    private usuarios usuario;
    
    @ManyToMany(mappedBy = "informes")
    @JsonIgnoreProperties("informes")
    private Set<carpetas> carpetas;
    
    @Column(nullable = false)
    private String ruta_doc; // Ruta al PDF/Imagen en el servidor
    
    private float costo;
    
    @Temporal(TemporalType.TIMESTAMP)
    private Date fecha_informe;

    @PrePersist
    protected void onCreate() {
        if (fecha_informe == null) fecha_informe = new Date();
    }

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

	public vehiculos getVehiculo() {
		return vehiculo;
	}

	public void setVehiculo(vehiculos vehiculo) {
		this.vehiculo = vehiculo;
	}

	public usuarios getUsuario() {
		return usuario;
	}

	public void setUsuario(usuarios usuario) {
		this.usuario = usuario;
	}

	public Set<carpetas> getCarpetas() {
		return carpetas;
	}

	public void setCarpetas(Set<carpetas> carpetas) {
		this.carpetas = carpetas;
	}

	public String getRuta_doc() {
		return ruta_doc;
	}

	public void setRuta_doc(String ruta_doc) {
		this.ruta_doc = ruta_doc;
	}

	public float getCosto() {
		return costo;
	}

	public void setCosto(float costo) {
		this.costo = costo;
	}

	public Date getFecha_informe() {
		return fecha_informe;
	}

	public void setFecha_informe(Date fecha_informe) {
		this.fecha_informe = fecha_informe;
	}
    
}