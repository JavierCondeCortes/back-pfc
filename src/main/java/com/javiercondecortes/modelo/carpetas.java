package com.javiercondecortes.modelo;

import java.util.Set;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

@Entity
@Table(name = "carpetas")
public class carpetas {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 100)
    private String nombre;
    
    // Cambiamos a FetchType.LAZY para que no traiga al usuario si no lo pides explícitamente.
    // Usamos JsonIgnoreProperties para evitar bucles infinitos al convertir a JSON.
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "password"}) 
    private usuarios usuario;
    
    @ManyToMany
    @JoinTable(
        name = "informes_carpetas",
        joinColumns = @JoinColumn(name = "carpeta_id", referencedColumnName = "id"),
        inverseJoinColumns = @JoinColumn(name = "informe_id", referencedColumnName = "id")
    )
    @JsonIgnoreProperties("carpetas")
    private Set<informes> informes;

    public carpetas() {
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public usuarios getUsuario() { return usuario; }
    public void setUsuario(usuarios usuario) { this.usuario = usuario; }

    public Set<informes> getInformes() { return informes; }
    public void setInformes(Set<informes> informes) { this.informes = informes; }
}