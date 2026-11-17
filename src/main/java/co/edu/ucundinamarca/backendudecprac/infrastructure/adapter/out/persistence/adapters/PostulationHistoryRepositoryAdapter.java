package co.edu.ucundinamarca.backendudecprac.infrastructure.adapter.out.persistence.adapters;

import co.edu.ucundinamarca.backendudecprac.domain.model.status.PostulationStatus;
import co.edu.ucundinamarca.backendudecprac.domain.model.PostulationHistory;
import co.edu.ucundinamarca.backendudecprac.domain.port.out.PostulationHistoryRepositoryPort;
import co.edu.ucundinamarca.backendudecprac.infrastructure.adapter.out.persistence.entities.PostulationHistoryEntity;
import co.edu.ucundinamarca.backendudecprac.infrastructure.adapter.out.persistence.jpa.JPAPostulationHistoryRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PostulationHistoryRepositoryAdapter implements PostulationHistoryRepositoryPort {
    private final JPAPostulationHistoryRepository jpaRepository;

    public PostulationHistoryRepositoryAdapter(JPAPostulationHistoryRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public PostulationHistory saveHistory(PostulationHistory history) {
        PostulationHistoryEntity entity = new PostulationHistoryEntity();
        entity.setIdPostulation(history.getIdPostulation());
        entity.setPreviousStatus(history.getPreviousStatus() == null ? null : history.getPreviousStatus().name());
        entity.setNewStatus(history.getNewStatus() == null ? null : history.getNewStatus().name());
        entity.setChangeDate(history.getChangeDate());

        // Guardamos y capturamos la entidad con su nuevo ID
        PostulationHistoryEntity guardado = jpaRepository.save(entity);

        // Le pasamos el ID autogenerado al dominio y lo devolvemos
        history.setIdHistory(guardado.getIdHistory());
        return history;
    }

    @Override
    public List<PostulationHistory> getHistoryByPostulationId(Long idPostulation) {
        List<PostulationHistoryEntity> entidades = jpaRepository.findByIdPostulationOrderByChangeDateDesc(idPostulation);

        return entidades.stream().map(entity -> {
            PostulationHistory history = new PostulationHistory();
            history.setIdHistory(entity.getIdHistory());
            history.setIdPostulation(entity.getIdPostulation());
            history.setPreviousStatus(entity.getPreviousStatus() == null ? null : PostulationStatus.from(entity.getPreviousStatus()));
            history.setNewStatus(entity.getNewStatus() == null ? null : PostulationStatus.from(entity.getNewStatus()));
            history.setChangeDate(entity.getChangeDate());
            return history;
        }).toList();
    }
}
