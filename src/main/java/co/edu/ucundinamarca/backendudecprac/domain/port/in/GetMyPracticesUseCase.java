package co.edu.ucundinamarca.backendudecprac.domain.port.in;

import co.edu.ucundinamarca.backendudecprac.domain.model.StudentPracticeSummary;

import java.util.List;

/** Seguimiento de las prácticas del estudiante de la sesión (identificado por el correo del token). */
public interface GetMyPracticesUseCase {
    /** Prácticas del estudiante: primero las que están EN_CURSO y luego las más recientes. */
    List<StudentPracticeSummary> getMyPractices(String email);
}
