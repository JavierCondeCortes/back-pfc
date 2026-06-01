package com.javiercondecortes.servicio;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.javiercondecortes.DAO.IInformesDAO;
import com.javiercondecortes.modelo.informes;

@Service
public class InformesServiceImp implements IInformesService {

    @Autowired
    private IInformesDAO dao;

    @Override
    @Transactional(readOnly = true)
    public List<informes> findByUsuarioId(Long usuarioId) {
        return dao.findByUsuarioId(usuarioId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<informes> findByVehiculoId(Long vehiculoId) {
        return dao.findByVehiculoId(vehiculoId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<informes> findByCarpetaId(Long carpetaId) {
        return dao.findByCarpetasId(carpetaId);
    }

    @Override
    @Transactional
    public void saveInformes(informes informe) {
        dao.save(informe);
    }

    @Override
    public informes findById(Long id) {
        return dao.findById(id).orElse(null);
    }

    @Override
    @Transactional
    public void deleteInformes(Long id) {
        dao.deleteById(id);
    }
}