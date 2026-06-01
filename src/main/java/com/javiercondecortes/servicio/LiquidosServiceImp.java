package com.javiercondecortes.servicio;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.javiercondecortes.DAO.ILiquidosDAO;
import com.javiercondecortes.modelo.liquidos;

@Service
public class LiquidosServiceImp implements ILiquidosService {

    @Autowired
    private ILiquidosDAO dao;

    @Override
    @Transactional(readOnly = true)
    public List<liquidos> findByVehiculoId(Long vehiculoId) {
        // Buscamos directamente por la FK para mayor rapidez
        return dao.findByVehiculoId(vehiculoId);
    }

    @Override
    @Transactional(readOnly = true)
    public liquidos findByTipoAndVehiculoId(String tipo, Long vehiculoId) {
        // Retornamos el líquido específico o null de forma controlada
        return dao.findByTipoAndVehiculoId(tipo, vehiculoId).orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public List<liquidos> findByUsuarioId(Long usuarioId) {
        // Este es el filtro de seguridad que usará tu futuro sistema JWT
        return dao.findByUsuarioId(usuarioId);
    }

    @Override
    @Transactional
    public void saveLiquidos(liquidos liquido) {
        // Guardamos el objeto (JPA se encarga de verificar si es crear o actualizar)
        dao.save(liquido);
    }

    @Override
    @Transactional
    public void deleteLiquidos(Long id) {
        // Borramos por ID: es más rápido y evita errores de persistencia
        dao.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public liquidos findById(Long id) {
        // .orElse(null) devuelve el objeto si existe, o null si no lo encuentra
        return dao.findById(id).orElse(null);
    }
}