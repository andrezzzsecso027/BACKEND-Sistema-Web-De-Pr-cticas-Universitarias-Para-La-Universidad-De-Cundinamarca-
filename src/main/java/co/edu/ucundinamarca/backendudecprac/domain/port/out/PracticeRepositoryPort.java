package co.edu.ucundinamarca.backendudecprac.domain.port.out;

import co.edu.ucundinamarca.backendudecprac.domain.model.Practice;

public interface PracticeRepositoryPort {
    Practice savePractice(Practice practice);
    Practice getPracticeByPostulationId(Long idPostulation);
    Practice findById(Long idPractice);
}
