package co.edu.ucundinamarca.backendudecprac.infrastructure.adapter.out.persistence.JPA;

import co.edu.ucundinamarca.backendudecprac.infrastructure.adapter.out.persistence.Entities.PostulationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JPApostulationRepository extends JpaRepository<PostulationEntity,Long> {
    boolean existsByIdOfferAndNumberDocumentStudent(Long idOffer, String numberDocumentStudent);
    List<PostulationEntity> findByIdOffer(Long idOffer);
}
