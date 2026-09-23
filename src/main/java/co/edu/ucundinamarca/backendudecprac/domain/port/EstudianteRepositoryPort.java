package co.edu.ucundinamarca.backendudecprac.domain.port;

import co.edu.ucundinamarca.backendudecprac.domain.model.Estudiante;
import java.util.List;

public interface EstudianteRepositoryPort {
    Estudiante guardar(Estudiante estudiante);
    List<Estudiante> listarTodos();
}
