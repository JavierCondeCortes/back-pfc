package com.javiercondecortes.servicio;

import java.util.List;
import com.javiercondecortes.modelo.carpetas;

public interface ICarpetasService {
    List<carpetas> listarPorUsuario(Long usuarioId);
    void guardar(carpetas carpeta);
    void eliminar(Long id);
    carpetas buscarPorIdYUsuario(Long id, Long usuarioId);
    carpetas findById(Long id);
    boolean vincularInforme(Long carpetaId, Long informeId, Long usuarioId);
    boolean desvincularInforme(Long carpetaId, Long informeId, Long usuarioId);
}