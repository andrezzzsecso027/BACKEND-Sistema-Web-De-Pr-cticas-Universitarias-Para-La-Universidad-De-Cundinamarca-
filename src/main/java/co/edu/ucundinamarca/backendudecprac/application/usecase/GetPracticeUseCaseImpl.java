package co.edu.ucundinamarca.backendudecprac.application.usecase;

import co.edu.ucundinamarca.backendudecprac.domain.model.Practice;
import co.edu.ucundinamarca.backendudecprac.domain.port.in.GetPracticeUseCase;
import co.edu.ucundinamarca.backendudecprac.domain.port.out.PracticeRepositoryPort;
import org.springframework.stereotype.Service;

@Service
public class GetPracticeUseCaseImpl implements GetPracticeUseCase {
    private final PracticeRepositoryPort practicePort;

    public GetPracticeUseCaseImpl(PracticeRepositoryPort practicePort) {
        this.practicePort = practicePort;
    }

    @Override
    public Practice getPracticeByPostulation(Long idPostulation) {
        Practice practice = practicePort.getPracticeByPostulationId(idPostulation);
        if (practice == null) {
            throw new IllegalArgumentException("No existe una práctica asignada a esta postulación.");
        }
        return practice;
    }
}
