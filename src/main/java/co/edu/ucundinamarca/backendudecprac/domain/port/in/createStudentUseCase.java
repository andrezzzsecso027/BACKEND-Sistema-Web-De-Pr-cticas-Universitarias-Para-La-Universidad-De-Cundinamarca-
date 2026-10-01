package co.edu.ucundinamarca.backendudecprac.domain.port.in;

import co.edu.ucundinamarca.backendudecprac.domain.model.Empresa;
import co.edu.ucundinamarca.backendudecprac.domain.model.Estudiante;
import co.edu.ucundinamarca.backendudecprac.domain.model.Usuario;

import java.util.List;

public interface createEstudianteUseCase {
    Estudiante createEstudiante(Usuario usuario, Estudiante estudiante);
    List<Estudiante> listarTodos();
}
