package co.edu.ucundinamarca.backendudecprac.application.usecase;

import co.edu.ucundinamarca.backendudecprac.domain.exceptions.ResourceNotFoundException;
import co.edu.ucundinamarca.backendudecprac.domain.model.Practice;
import co.edu.ucundinamarca.backendudecprac.domain.model.status.PracticeStatus;
import co.edu.ucundinamarca.backendudecprac.domain.port.in.UpdatePracticeUseCase;
import co.edu.ucundinamarca.backendudecprac.domain.port.out.PracticeRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
public class UpdatePracticeUseCaseImpl implements UpdatePracticeUseCase {

    private final PracticeRepositoryPort practiceRepositoryPort;

    public UpdatePracticeUseCaseImpl(PracticeRepositoryPort practiceRepositoryPort) {
        this.practiceRepositoryPort = practiceRepositoryPort;
    }

    @Transactional
    @Override
    public Practice updatePractice(Long idPractice, LocalDate startDate, LocalDate endDate, PracticeStatus status) {
        Practice practice = practiceRepositoryPort.findById(idPractice);
        if (practice == null) {
            throw new ResourceNotFoundException("La práctica no existe.");
        }
        practice.update(startDate, endDate, status); // valida fechas y transición EN_CURSO -> FINALIZADA
        return practiceRepositoryPort.savePractice(practice);
    }
}
