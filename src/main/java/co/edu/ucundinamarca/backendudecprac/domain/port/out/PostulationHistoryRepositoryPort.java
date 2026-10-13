package co.edu.ucundinamarca.backendudecprac.domain.port.out;

import co.edu.ucundinamarca.backendudecprac.domain.model.PostulationHistory;

import java.util.List;

public interface PostulationHistoryRepositoryPort {
    PostulationHistory saveHistory(PostulationHistory history);
    List<PostulationHistory> getHistoryByPostulationId(Long idPostulation);
}
