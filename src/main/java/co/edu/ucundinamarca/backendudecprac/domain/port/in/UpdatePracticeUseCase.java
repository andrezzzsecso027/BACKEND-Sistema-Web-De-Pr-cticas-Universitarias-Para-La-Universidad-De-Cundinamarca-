package co.edu.ucundinamarca.backendudecprac.domain.port.in;

import co.edu.ucundinamarca.backendudecprac.domain.model.Practice;

import java.time.LocalDate;

public interface UpdatePracticeUseCase {
    Practice updatePractice(Long idPractice, LocalDate startDate, LocalDate endDate, String status);
}
