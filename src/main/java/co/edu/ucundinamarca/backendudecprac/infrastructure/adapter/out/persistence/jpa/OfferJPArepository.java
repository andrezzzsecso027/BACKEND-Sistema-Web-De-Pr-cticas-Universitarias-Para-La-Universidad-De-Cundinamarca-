package co.edu.ucundinamarca.backendudecprac.infrastructure.adapter.out.persistence.jpa;

import co.edu.ucundinamarca.backendudecprac.infrastructure.adapter.out.persistence.entities.OfferEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OfferJPArepository extends JpaRepository<OfferEntity, Long> {
    List<OfferEntity> findByOfferStatusTrue();
}
