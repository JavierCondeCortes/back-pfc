package com.javiercondecortes.servicio;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.javiercondecortes.DAO.IMantenimientosDAO;
import com.javiercondecortes.modelo.mantenimientos;

@Service
public class MantenimientosServiceImp implements IMantenimientosService {

    @Autowired
    private IMantenimientosDAO dao;

    @Override
    @Transactional(readOnly = true)
    public List<mantenimientos> findByVehiculoId(Long vehiculoId) {
        // El DAO ahora busca directamente por el ID de la FK
        return dao.findByVehiculoId(vehiculoId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<mantenimientos> findByUsuarioId(Long usuarioId) {
        // Filtro esencial para la privacidad del usuario
        return dao.findByUsuarioId(usuarioId);
    }

    @Override
    @Transactional
    public void saveMantenimiento(mantenimientos mantenimiento) {
        dao.save(mantenimiento);
    }

    @Override
    @Transactional
    public void deleteMantenimiento(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("El ID no puede ser nulo para eliminar.");
        }
        
        // Antes de borrar, verificamos si existe para evitar errores de JPA
        if (dao.existsById(id)) {
            dao.deleteById(id);
        } else {
            System.out.println("Intento de borrar ID inexistente: " + id);
        }
    }

	@Override
	public mantenimientos findById(Long id) {
		// TODO Auto-generated method stub
		return dao.findById(id).orElse(null);
	}
}