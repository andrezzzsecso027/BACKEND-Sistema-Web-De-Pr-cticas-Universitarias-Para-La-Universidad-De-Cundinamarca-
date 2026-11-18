package co.edu.ucundinamarca.backendudecprac.application.usecase;

import co.edu.ucundinamarca.backendudecprac.domain.model.Comment;
import co.edu.ucundinamarca.backendudecprac.domain.model.ContractDocument;
import co.edu.ucundinamarca.backendudecprac.domain.model.Practice;
import co.edu.ucundinamarca.backendudecprac.domain.model.StudentPostulationSummary;
import co.edu.ucundinamarca.backendudecprac.domain.model.StudentPracticeSummary;
import co.edu.ucundinamarca.backendudecprac.domain.model.status.CommentStatus;
import co.edu.ucundinamarca.backendudecprac.domain.model.status.ContractTrackingStatus;
import co.edu.ucundinamarca.backendudecprac.domain.model.status.PostulationStatus;
import co.edu.ucundinamarca.backendudecprac.domain.model.status.PracticeStatus;
import co.edu.ucundinamarca.backendudecprac.domain.port.in.StudentSelfServiceUseCase;
import co.edu.ucundinamarca.backendudecprac.domain.port.out.CommentRepositoryPort;
import co.edu.ucundinamarca.backendudecprac.domain.port.out.ContractDocumentRepositoryPort;
import co.edu.ucundinamarca.backendudecprac.domain.port.out.PracticeRepositoryPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

/** Vista "Mi práctica": arma el seguimiento a partir de las postulaciones aceptadas del estudiante. */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class GetMyPracticesUseCaseTest {

    private static final String CORREO = "ana@ucundinamarca.edu.co";

    @Mock private StudentSelfServiceUseCase studentSelfService;
    @Mock private PracticeRepositoryPort practicePort;
    @Mock private ContractDocumentRepositoryPort contractPort;
    @Mock private CommentRepositoryPort commentPort;

    @InjectMocks private GetMyPracticesUseCaseImpl useCase;

    private static StudentPostulationSummary postulacion(long id, PostulationStatus estado, LocalDateTime aceptada) {
        return new StudentPostulationSummary(id, 10L, "Oferta " + id, "Sistemas", "Remoto", "Bogotá",
                "Nexus", estado, aceptada.minusDays(5), aceptada, null);
    }

    private static Practice practica(long idPractica, long idPostulacion, PracticeStatus estado) {
        Practice p = Practice.startFor(idPostulacion);
        p.setIdPractice(idPractica);
        if (estado == PracticeStatus.FINALIZADA) p.update(null, null, PracticeStatus.FINALIZADA);
        return p;
    }

    @Test
    @DisplayName("Solo incluye postulaciones aceptadas con práctica, con la EN_CURSO primero y luego la más reciente")
    void filtraYOrdena() {
        LocalDateTime hoy = LocalDateTime.now();
        when(studentSelfService.getMyPostulations(CORREO)).thenReturn(List.of(
                postulacion(1L, PostulationStatus.ACEPTADA, hoy.minusMonths(8)),   // finalizada antigua
                postulacion(2L, PostulationStatus.ACEPTADA, hoy.minusMonths(2)),   // en curso
                postulacion(3L, PostulationStatus.RECHAZADA, hoy),                  // se descarta
                postulacion(4L, PostulationStatus.ACEPTADA, hoy.minusMonths(1)),   // sin práctica: se descarta
                postulacion(5L, PostulationStatus.ACEPTADA, hoy.minusMonths(4))    // finalizada más reciente
        ));
        when(practicePort.getPracticeByPostulationId(1L)).thenReturn(practica(11L, 1L, PracticeStatus.FINALIZADA));
        when(practicePort.getPracticeByPostulationId(2L)).thenReturn(practica(12L, 2L, PracticeStatus.EN_CURSO));
        when(practicePort.getPracticeByPostulationId(4L)).thenReturn(null);
        when(practicePort.getPracticeByPostulationId(5L)).thenReturn(practica(15L, 5L, PracticeStatus.FINALIZADA));
        when(contractPort.findByPostulationId(anyLong())).thenReturn(Optional.empty());
        when(commentPort.findByIdPractice(anyLong())).thenReturn(Optional.empty());

        List<StudentPracticeSummary> resultado = useCase.getMyPractices(CORREO);

        assertEquals(List.of(12L, 15L, 11L), resultado.stream().map(StudentPracticeSummary::idPractice).toList());
        assertEquals("Nexus", resultado.get(0).companyName());
        assertNull(resultado.get(0).contract());
        assertNull(resultado.get(0).evaluation());
    }

    @Test
    @DisplayName("Incluye el contrato y la calificación que el estudiante le dio a la empresa")
    void incluyeContratoYCalificacion() {
        when(studentSelfService.getMyPostulations(CORREO))
                .thenReturn(List.of(postulacion(1L, PostulationStatus.ACEPTADA, LocalDateTime.now())));
        when(practicePort.getPracticeByPostulationId(1L)).thenReturn(practica(11L, 1L, PracticeStatus.FINALIZADA));

        ContractDocument contrato = new ContractDocument();
        contrato.setTrackingStatus(ContractTrackingStatus.CONTRATO_FIRMADO);
        contrato.setFileUrl("https://x/contrato.pdf");
        when(contractPort.findByPostulationId(1L)).thenReturn(Optional.of(contrato));

        Comment comentario = new Comment();
        comentario.setCompanyRating(4.0);
        comentario.setCommentText("Buen ambiente");
        comentario.setValidationStatus(CommentStatus.APROBADO);
        when(commentPort.findByIdPractice(11L)).thenReturn(Optional.of(comentario));

        StudentPracticeSummary s = useCase.getMyPractices(CORREO).get(0);

        assertEquals(ContractTrackingStatus.CONTRATO_FIRMADO, s.contract().trackingStatus());
        assertEquals("https://x/contrato.pdf", s.contract().fileUrl());
        assertEquals(4.0, s.evaluation().companyRating());
        assertEquals(CommentStatus.APROBADO, s.evaluation().validationStatus());
    }
}
