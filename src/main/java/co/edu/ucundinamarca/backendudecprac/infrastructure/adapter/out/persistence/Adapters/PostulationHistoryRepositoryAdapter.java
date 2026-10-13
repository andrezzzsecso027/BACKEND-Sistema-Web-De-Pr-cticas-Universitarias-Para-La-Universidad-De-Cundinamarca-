package co.edu.ucundinamarca.backendudecprac.infrastructure.adapter.out.persistence.Adapters;

import co.edu.ucundinamarca.backendudecprac.domain.model.PostulationHistory;
import co.edu.ucundinamarca.backendudecprac.domain.port.out.PostulationHistoryRepositoryPort;
import co.edu.ucundinamarca.backendudecprac.infrastructure.adapter.out.persistence.Entities.PostulationHistoryEntity;
import co.edu.ucundinamarca.backendudecprac.infrastructure.adapter.out.persistence.JPA.JPAPostulationHistoryRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

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
        entity.setPreviousStatus(history.getPreviousStatus());
        entity.setNewStatus(history.getNewStatus());
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
            history.setPreviousStatus(entity.getPreviousStatus());
            history.setNewStatus(entity.getNewStatus());
            history.setChangeDate(entity.getChangeDate());
            return history;
        }).collect(Collectors.toList());
    }
}
