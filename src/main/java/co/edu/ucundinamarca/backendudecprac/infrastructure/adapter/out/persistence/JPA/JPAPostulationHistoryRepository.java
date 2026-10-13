package co.edu.ucundinamarca.backendudecprac.infrastructure.adapter.out.persistence.JPA;

import co.edu.ucundinamarca.backendudecprac.infrastructure.adapter.out.persistence.Entities.PostulationHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JPAPostulationHistoryRepository extends JpaRepository<PostulationHistoryEntity, Long> {
    List<PostulationHistoryEntity> findByIdPostulationOrderByChangeDateDesc(Long idPostulation);
}
