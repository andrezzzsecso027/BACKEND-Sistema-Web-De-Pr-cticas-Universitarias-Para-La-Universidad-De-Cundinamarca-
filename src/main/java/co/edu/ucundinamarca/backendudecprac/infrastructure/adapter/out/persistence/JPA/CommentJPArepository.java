package co.edu.ucundinamarca.backendudecprac.infrastructure.adapter.out.persistence.JPA;

import co.edu.ucundinamarca.backendudecprac.infrastructure.adapter.out.persistence.Entities.CommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentJPArepository extends JpaRepository<CommentEntity, Long> {
    boolean existsByIdPractice(Long idPractice);

    List<CommentEntity> findByValidationStatusIgnoreCase(String validationStatus);

    @Query("SELECT c FROM CommentEntity c " +
            "JOIN PracticeEntity pr ON c.idPractice = pr.idPractice " +
            "JOIN PostulationEntity po ON pr.idPostulation = po.idPostulation " +
            "JOIN OfferEntity ofe ON po.idOffer = ofe.idOffer " +
            "WHERE ofe.nitCompany = :nitCompany AND UPPER(c.validationStatus) = 'APROBADO'")
    List<CommentEntity> findApprovedCommentsByCompanyNit(@Param("nitCompany") String nitCompany);
}
