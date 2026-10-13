package co.edu.ucundinamarca.backendudecprac.application.usecase;

import co.edu.ucundinamarca.backendudecprac.domain.model.PostulationHistory;
import co.edu.ucundinamarca.backendudecprac.domain.port.in.GetPostulationHistoryUseCase;
import co.edu.ucundinamarca.backendudecprac.domain.port.out.PostulationHistoryRepositoryPort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GetPostulationHistoryUseCaseImpl implements GetPostulationHistoryUseCase {
    private final PostulationHistoryRepositoryPort historyPort;

    public GetPostulationHistoryUseCaseImpl(PostulationHistoryRepositoryPort historyPort) {
        this.historyPort = historyPort;
    }

    @Override
    public List<PostulationHistory> getHistory(Long idPostulation) {
        return historyPort.getHistoryByPostulationId(idPostulation);
    }
}
