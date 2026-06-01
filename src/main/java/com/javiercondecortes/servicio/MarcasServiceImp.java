package com.javiercondecortes.servicio;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.javiercondecortes.DAO.IMarcasDAO;
import com.javiercondecortes.modelo.marcas;

@Service
public class MarcasServiceImp implements IMarcasService {

    @Autowired
    private IMarcasDAO dao;

    @Override
    @Transactional(readOnly = true)
    public List<marcas> findAll() {
        return dao.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public marcas findByNombre(String nombre) {
        return dao.findByNombre(nombre).orElse(null);
    }

    @Override
    @Transactional
    public void saveMarcas(marcas marca) {
        dao.save(marca);
    }

    @Override
    @Transactional
    public void deleteMarcas(Long id) {
        dao.deleteById(id);
    }
}