package com.javiercondecortes.modelo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

@Entity
@Table(name = "liquidos")
public class liquidos {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name="vehiculo_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private vehiculos vehiculo;

    // SEGURIDAD: Relación directa con el dueño para filtrado JWT
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name="usuario_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "password"})
    private usuarios usuario;
    
    @Column(nullable = false)
    private String nombre;
    
    @Column(nullable = false)
    private String tipo;
    
    private int km_para_cambio;
    
    private int tiempo_para_cambio;

    public liquidos() {
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public vehiculos getVehiculo() { return vehiculo; }
    public void setVehiculo(vehiculos vehiculo) { this.vehiculo = vehiculo; }

    public usuarios getUsuario() { return usuario; }
    public void setUsuario(usuarios usuario) { this.usuario = usuario; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public int getKm_para_cambio() { return km_para_cambio; }
    public void setKm_para_cambio(int km_para_cambio) { this.km_para_cambio = km_para_cambio; }

    public int getTiempo_para_cambio() { return tiempo_para_cambio; }
    public void setTiempo_para_cambio(int tiempo_para_cambio) { this.tiempo_para_cambio = tiempo_para_cambio; }
}