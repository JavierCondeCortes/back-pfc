package com.javiercondecortes.servicio;

import java.util.List;
import com.javiercondecortes.modelo.vehiculos;

public interface IVehiculosService {
    
    // Listar vehículos por el ID del usuario (Seguridad JWT)
    List<vehiculos> findByUsuarioId(Long usuarioId);
    
 // IVehiculosService.java
    public vehiculos findById(Long id); // <--- Debe decir 'vehiculos', no 'void' ni 'Object'
    
    // Buscar un vehículo específico por su matrícula y el ID de su dueño
    vehiculos findByMatriculaUsuario(String matricula, Long usuarioId);
    
    // Buscar por matrícula para validaciones (ej: evitar matrículas duplicadas)
    vehiculos findByMatricula(String matricula);
    
    boolean existsByIdAndUsuarioId(Long id, Long usuarioId);
    
    // Guardar o actualizar un vehículo
    void saveVehiculos(vehiculos vehiculo);
    
    // Eliminar un vehículo por su ID
    void deleteVehiculos(Long id);
}