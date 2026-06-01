package com.javiercondecortes.servicio;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.javiercondecortes.DAO.ICarpetasDAO;
import com.javiercondecortes.DAO.IInformesDAO;
import com.javiercondecortes.modelo.carpetas;
import com.javiercondecortes.modelo.informes;

@Service
public class CarpetasServiceImp implements ICarpetasService {

    @Autowired
    private ICarpetasDAO dao;
    
    @Autowired
    private IInformesDAO informeDao;
    
    @Autowired
    private ICarpetasDAO carpetaDao; // DAO de la entidad carpetas
    
    @Transactional
    public boolean vincularInforme(Long carpetaId, Long informeId, Long usuarioId) {
        carpetas carpeta = dao.findById(carpetaId).orElse(null);
        // Necesitas inyectar el DAO de informes aquí
        informes informe = informeDao.findById(informeId).orElse(null);

        if (carpeta != null && informe != null) {
            // Validamos que ambos pertenecen al mismo usuario
            if (carpeta.getUsuario().getId().equals(usuarioId) && 
                informe.getVehiculo().getUsuario().getId().equals(usuarioId)) {
                
                carpeta.getInformes().add(informe); // Añadimos al Set
                dao.save(carpeta);
                return true;
            }
        }
        return false;
    }
    
    @Override
    @Transactional
    public boolean desvincularInforme(Long carpetaId, Long informeId, Long usuarioId) {
        try {
            carpetas carpeta = carpetaDao.findById(carpetaId).orElse(null);
            informes informe = informeDao.findById(informeId).orElse(null);

            if (carpeta != null && informe != null) {
                if (carpeta.getUsuario().getId().equals(usuarioId)) {
                    // remove() elimina la relación en la tabla 'informes_carpetas'
                    boolean eliminado = carpeta.getInformes().remove(informe);
                    
                    if (eliminado) {
                        carpetaDao.save(carpeta);
                        return true;
                    }
                }
            }
            return false;
        } catch (Exception e) {
            System.err.println("Error al desvincular: " + e.getMessage());
            return false;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<carpetas> listarPorUsuario(Long usuarioId) {
        return dao.findByUsuarioId(usuarioId);
    }

    @Override
    @Transactional
    public void guardar(carpetas carpeta) {
        dao.save(carpeta);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        dao.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public carpetas buscarPorIdYUsuario(Long id, Long usuarioId) {
        return dao.findByIdAndUsuarioId(id, usuarioId);
    }

	@Override
	public carpetas findById(Long id) {
	    return dao.findById(id).orElse(null);
	}
    

}