package co.edu.ucundinamarca.backendudecprac.domain.model;

import co.edu.ucundinamarca.backendudecprac.domain.model.status.CommentStatus;
import co.edu.ucundinamarca.backendudecprac.domain.model.status.ContractTrackingStatus;
import co.edu.ucundinamarca.backendudecprac.domain.model.status.PracticeStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Modelo de lectura para la vista "Mi práctica" del estudiante: la práctica con su oferta,
 * empresa, contrato y la calificación que el estudiante le dio a la empresa (si ya la hizo).
 */
public record StudentPracticeSummary(
        Long idPractice,
        Long idPostulation,
        PracticeStatus practiceStatus,
        LocalDate startDate,
        LocalDate endDate,
        String companyFeedback,
        String offerName,
        String profession,
        String modality,
        String location,
        String companyName,
        LocalDateTime datePostulation,
        LocalDateTime dateAccepted,
        Contract contract,
        Evaluation evaluation
) {
    /** Contrato de práctica cargado por la empresa. */
    public record Contract(ContractTrackingStatus trackingStatus, String fileName, String fileUrl,
                           String companyComments, LocalDateTime uploadDate) {
    }

    /** Calificación del estudiante a la empresa y su estado de moderación. */
    public record Evaluation(Double companyRating, String commentText, CommentStatus validationStatus,
                             LocalDateTime commentDate) {
    }
}
