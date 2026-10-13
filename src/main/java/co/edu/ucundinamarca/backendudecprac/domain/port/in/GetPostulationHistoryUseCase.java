package co.edu.ucundinamarca.backendudecprac.domain.port.in;

import co.edu.ucundinamarca.backendudecprac.domain.model.PostulationHistory;

import java.util.List;

public interface GetPostulationHistoryUseCase {
    List<PostulationHistory> getHistory(Long idPostulation);
}
