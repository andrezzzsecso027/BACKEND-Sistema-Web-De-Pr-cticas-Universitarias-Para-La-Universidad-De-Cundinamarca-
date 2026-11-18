package co.edu.ucundinamarca.backendudecprac.application.usecase;

import co.edu.ucundinamarca.backendudecprac.domain.model.Practice;
import co.edu.ucundinamarca.backendudecprac.domain.model.StudentPostulationSummary;
import co.edu.ucundinamarca.backendudecprac.domain.model.StudentPracticeSummary;
import co.edu.ucundinamarca.backendudecprac.domain.model.status.PostulationStatus;
import co.edu.ucundinamarca.backendudecprac.domain.model.status.PracticeStatus;
import co.edu.ucundinamarca.backendudecprac.domain.port.in.GetMyPracticesUseCase;
import co.edu.ucundinamarca.backendudecprac.domain.port.in.StudentSelfServiceUseCase;
import co.edu.ucundinamarca.backendudecprac.domain.port.out.CommentRepositoryPort;
import co.edu.ucundinamarca.backendudecprac.domain.port.out.ContractDocumentRepositoryPort;
import co.edu.ucundinamarca.backendudecprac.domain.port.out.PracticeRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * Arma la vista "Mi práctica": parte de las postulaciones ACEPTADAS del estudiante (que ya traen
 * oferta y empresa) y les suma la práctica, el contrato y la calificación a la empresa.
 */
@Service
public class GetMyPracticesUseCaseImpl implements GetMyPracticesUseCase {

    private final StudentSelfServiceUseCase studentSelfService;
    private final PracticeRepositoryPort practicePort;
    private final ContractDocumentRepositoryPort contractPort;
    private final CommentRepositoryPort commentPort;

    public GetMyPracticesUseCaseImpl(StudentSelfServiceUseCase studentSelfService, PracticeRepositoryPort practicePort,
                                     ContractDocumentRepositoryPort contractPort, CommentRepositoryPort commentPort) {
        this.studentSelfService = studentSelfService;
        this.practicePort = practicePort;
        this.contractPort = contractPort;
        this.commentPort = commentPort;
    }

    @Override
    @Transactional(readOnly = true)
    public List<StudentPracticeSummary> getMyPractices(String email) {
        return studentSelfService.getMyPostulations(email).stream()
                .filter(p -> p.status() == PostulationStatus.ACEPTADA)
                .map(this::toSummary)
                .filter(Objects::nonNull)
                .sorted(Comparator
                        .comparing((StudentPracticeSummary s) -> s.practiceStatus() != PracticeStatus.EN_CURSO)
                        .thenComparing(StudentPracticeSummary::dateAccepted,
                                Comparator.nullsLast(Comparator.<LocalDateTime>reverseOrder())))
                .toList();
    }

    private StudentPracticeSummary toSummary(StudentPostulationSummary postulation) {
        Practice practice = practicePort.getPracticeByPostulationId(postulation.idPostulation());
        if (practice == null) {
            return null; // aceptada pero sin práctica creada (datos antiguos): no hay nada que seguir
        }
        StudentPracticeSummary.Contract contract = contractPort.findByPostulationId(postulation.idPostulation())
                .map(c -> new StudentPracticeSummary.Contract(c.getTrackingStatus(), c.getFileName(), c.getFileUrl(),
                        c.getCompanyComments(), c.getUploadDate()))
                .orElse(null);
        StudentPracticeSummary.Evaluation evaluation = commentPort.findByIdPractice(practice.getIdPractice())
                .map(c -> new StudentPracticeSummary.Evaluation(c.getCompanyRating(), c.getCommentText(),
                        c.getValidationStatus(), c.getCommentDate()))
                .orElse(null);
        return new StudentPracticeSummary(
                practice.getIdPractice(),
                postulation.idPostulation(),
                practice.getPracticeStatus(),
                practice.getStartDate(),
                practice.getEndDate(),
                practice.getCompanyFeedback(),
                postulation.offerName(),
                postulation.profession(),
                postulation.modality(),
                postulation.location(),
                postulation.companyName(),
                postulation.datePostulation(),
                postulation.dateUpdate(),
                contract,
                evaluation
        );
    }
}
