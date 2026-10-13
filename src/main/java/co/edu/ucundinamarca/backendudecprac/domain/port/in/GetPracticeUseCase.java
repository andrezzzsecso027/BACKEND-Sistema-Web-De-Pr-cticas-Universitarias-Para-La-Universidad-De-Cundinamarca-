package co.edu.ucundinamarca.backendudecprac.domain.port.in;

import co.edu.ucundinamarca.backendudecprac.domain.model.Practice;

public interface GetPracticeUseCase {
    Practice getPracticeByPostulation(Long idPostulation);
}
