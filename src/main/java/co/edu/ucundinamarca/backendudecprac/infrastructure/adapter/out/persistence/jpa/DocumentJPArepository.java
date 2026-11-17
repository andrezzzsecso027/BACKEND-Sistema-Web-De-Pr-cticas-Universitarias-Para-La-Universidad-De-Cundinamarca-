package co.edu.ucundinamarca.backendudecprac.infrastructure.adapter.out.persistence.jpa;

import co.edu.ucundinamarca.backendudecprac.infrastructure.adapter.out.persistence.entities.DocumentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DocumentJPArepository extends JpaRepository<DocumentEntity, Long> {
    Optional<DocumentEntity> findByIdPostulation(Long idPostulation);
    List<DocumentEntity> findByNitCompany(String nitCompany);
}

