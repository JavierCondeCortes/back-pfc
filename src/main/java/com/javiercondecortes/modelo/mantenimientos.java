package com.javiercondecortes.modelo;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

@Entity
@Table(name = "mantenimientos")
public class mantenimientos {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name="vehiculo_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler","usuario"})
    private vehiculos vehiculo;
    
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name="usuario_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "password"})
    private usuarios usuario;
    
    @Column(nullable = false)
    private String componente_cambiado;
    
    private String descripcion;
    
    private int km_cambiado;
    
    private float costo;

    private Date fecha_cambio;

    public mantenimientos() {}

    public vehiculos getVehiculo() {
        return vehiculo;
    }

    public void setVehiculo(vehiculos vehiculo) {
        this.vehiculo = vehiculo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public usuarios getUsuario() {
        return usuario;
    }

    public void setUsuario(usuarios usuario) {
        this.usuario = usuario;
    }

    public String getComponente_cambiado() {
        return componente_cambiado;
    }

    public void setComponente_cambiado(String componente_cambiado) {
        this.componente_cambiado = componente_cambiado;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public int getKm_cambiado() {
        return km_cambiado;
    }

    public void setKm_cambiado(int km_cambiado) {
        this.km_cambiado = km_cambiado;
    }

    public float getCosto() {
        return costo;
    }

    public void setCosto(float costo) {
        this.costo = costo;
    }

    public Date getFecha_cambio() {
        return fecha_cambio;
    }

    public void setFecha_cambio(Date fecha_cambio) {
        this.fecha_cambio = fecha_cambio;
    }
}