package com.javiercondecortes.modelo;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

@Entity
@Table(name = "vehiculos")
public class vehiculos {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name="marca_id", nullable = false)
	@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
	private marcas marca;
	
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name="usuario_id", nullable = false)
	@JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "password"})
	private usuarios usuario;
	
	private String modelo;
	private String pegatina;
	private String tipo_combustible;
	private int km_recorridos;
	
	private Date ultima_fecha_itv;

	@Column(unique = true, nullable = false)
	private String matricula;
	
	private String n_bastidor;
	
	private Date fecha_agregado;

    @PrePersist
    protected void onCreate() {
        fecha_agregado = new Date();
    }

	public vehiculos() {}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public marcas getMarca() {
		return marca;
	}

	public void setMarca(marcas marca) {
		this.marca = marca;
	}

	public usuarios getUsuario() {
		return usuario;
	}

	public void setUsuario(usuarios usuario) {
		this.usuario = usuario;
	}

	public String getModelo() {
		return modelo;
	}

	public void setModelo(String modelo) {
		this.modelo = modelo;
	}

	public String getPegatina() {
		return pegatina;
	}

	public void setPegatina(String pegatina) {
		this.pegatina = pegatina;
	}

	public String getTipo_combustible() {
		return tipo_combustible;
	}

	public void setTipo_combustible(String tipo_combustible) {
		this.tipo_combustible = tipo_combustible;
	}

	public int getKm_recorridos() {
		return km_recorridos;
	}

	public void setKm_recorridos(int km_recorridos) {
		this.km_recorridos = km_recorridos;
	}

	public Date getUltima_fecha_itv() {
		return ultima_fecha_itv;
	}

	public void setUltima_fecha_itv(Date ultima_fecha_itv) {
		this.ultima_fecha_itv = ultima_fecha_itv;
	}

	public String getMatricula() {
		return matricula;
	}

	public void setMatricula(String matricula) {
		this.matricula = matricula;
	}

	public String getN_bastidor() {
		return n_bastidor;
	}

	public void setN_bastidor(String n_bastidor) {
		this.n_bastidor = n_bastidor;
	}

	public Date getFecha_agregado() {
		return fecha_agregado;
	}

	public void setFecha_agregado(Date fecha_agregado) {
		this.fecha_agregado = fecha_agregado;
	}

	
}