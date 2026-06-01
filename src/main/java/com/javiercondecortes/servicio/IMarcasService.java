package com.javiercondecortes.servicio;

import java.util.List;
import com.javiercondecortes.modelo.marcas;

public interface IMarcasService {
    List<marcas> findAll();
    marcas findByNombre(String nombre);
    void saveMarcas(marcas marca);
    void deleteMarcas(Long id); // Cambiado a ID por seguridad
}