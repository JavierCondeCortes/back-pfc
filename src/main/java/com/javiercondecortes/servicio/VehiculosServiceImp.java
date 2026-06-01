package com.javiercondecortes.servicio;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.javiercondecortes.DAO.IInformesDAO;
import com.javiercondecortes.DAO.ILiquidosDAO;
import com.javiercondecortes.DAO.IVehiculosDAO;
import com.javiercondecortes.modelo.vehiculos;

@Service
public class VehiculosServiceImp implements IVehiculosService {

    @Autowired
    private IVehiculosDAO dao;

    @Autowired
    private IInformesDAO informesDao;
    
    @Autowired
    private ILiquidosDAO liquidosDao;

    @Override
    @Transactional(readOnly = true)
    public List<vehiculos> findByUsuarioId(Long usuarioId) {
        return dao.findByUsuarioId(usuarioId);
    }

    @Override
    @Transactional(readOnly = true)
    public vehiculos findById(Long id) {
        return dao.findById(id).orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public vehiculos findByMatriculaUsuario(String matricula, Long usuarioId) {
        return dao.findByMatriculaAndUsuarioId(matricula, usuarioId).orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public vehiculos findByMatricula(String matricula) {
        return dao.findByMatricula(matricula).orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByIdAndUsuarioId(Long id, Long usuarioId) {
        return dao.existsByIdAndUsuarioId(id, usuarioId);
    }

    @Override
    @Transactional
    public void saveVehiculos(vehiculos vehiculo) {
        dao.save(vehiculo);
    }

    @Override
    @Transactional
    public void deleteVehiculos(Long id) {
        informesDao.deleteByVehiculoId(id);
        liquidosDao.deleteByVehiculoId(id);
        dao.deleteById(id);
    }
}
