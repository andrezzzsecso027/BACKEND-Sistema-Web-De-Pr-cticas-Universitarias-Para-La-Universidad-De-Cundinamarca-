package co.edu.ucundinamarca.backendudecprac.domain.port.in;

import co.edu.ucundinamarca.backendudecprac.domain.model.Student;
import co.edu.ucundinamarca.backendudecprac.domain.model.StudentPostulationSummary;

import java.util.List;

/**
 * Consultas del estudiante sobre sí mismo. Se identifica por el correo de su sesión (token JWT),
 * de modo que nunca puede consultar los datos de otro estudiante.
 */
public interface StudentSelfServiceUseCase {

    Student getMyStudent(String email);

    List<StudentPostulationSummary> getMyPostulations(String email);
}
