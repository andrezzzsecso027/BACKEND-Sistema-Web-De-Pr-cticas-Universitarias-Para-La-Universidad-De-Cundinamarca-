package co.edu.ucundinamarca.backendudecprac.application.usecase;

import co.edu.ucundinamarca.backendudecprac.domain.model.Practice;
import co.edu.ucundinamarca.backendudecprac.domain.port.in.UpdatePracticeUseCase;
import co.edu.ucundinamarca.backendudecprac.domain.port.out.PracticeRepositoryPort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class UpdatePracticeUseCaseImpl implements UpdatePracticeUseCase {
    private final PracticeRepositoryPort practiceRepositoryPort;

    public UpdatePracticeUseCaseImpl(PracticeRepositoryPort practiceRepositoryPort) {
        this.practiceRepositoryPort = practiceRepositoryPort;
    }

    @Override
    public Practice updatePractice(Long idPractice, LocalDate startDate, LocalDate endDate, String status) {
        Practice practice = practiceRepositoryPort.findById(idPractice);
        if (practice == null) {
            throw new IllegalArgumentException("La práctica no existe.");
        }
        if (startDate != null) practice.setStartDate(startDate);
        if (endDate != null) practice.setEndDate(endDate);
        if (status != null && !status.isEmpty()) practice.setPracticeStatus(status);
        return practiceRepositoryPort.savePractice(practice);
    }
}
