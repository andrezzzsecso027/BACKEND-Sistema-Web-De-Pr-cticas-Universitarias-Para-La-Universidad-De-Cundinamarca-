package co.edu.ucundinamarca.backendudecprac.infrastructure.adapter.out.persistence.JPA;

import co.edu.ucundinamarca.backendudecprac.infrastructure.adapter.out.persistence.Entities.PracticeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JPAPracticeRepository extends JpaRepository<PracticeEntity, Long> {
    Optional<PracticeEntity> findByIdPostulation(Long idPostulation);
}
