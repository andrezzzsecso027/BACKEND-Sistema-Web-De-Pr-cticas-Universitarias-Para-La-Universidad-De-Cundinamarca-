package co.edu.ucundinamarca.backendudecprac.domain.port.out;

import co.edu.ucundinamarca.backendudecprac.domain.model.Estudiante;
import java.util.List;

public interface EstudianteRepositoryPort {
    Estudiante saveEstudiante(Estudiante estudiante);
    List<Estudiante> listarTodos();

    boolean existsByDocumento(String documento);
}
