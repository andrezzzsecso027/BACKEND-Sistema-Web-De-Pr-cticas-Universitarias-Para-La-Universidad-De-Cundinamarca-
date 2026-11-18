package co.edu.ucundinamarca.backendudecprac.application.usecase;

import co.edu.ucundinamarca.backendudecprac.domain.model.CandidateRanking;
import co.edu.ucundinamarca.backendudecprac.domain.model.Offer;
import co.edu.ucundinamarca.backendudecprac.domain.model.PostulationDetail;
import co.edu.ucundinamarca.backendudecprac.domain.model.StudentProfile;
import co.edu.ucundinamarca.backendudecprac.domain.port.in.GetPostulationsUseCase;
import co.edu.ucundinamarca.backendudecprac.domain.port.out.AiModelPort;
import co.edu.ucundinamarca.backendudecprac.domain.port.out.OfferRepositoryPort;
import co.edu.ucundinamarca.backendudecprac.domain.port.out.StudentProfileRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/** Ranking estructurado de postulados (idPostulation, match, motivo) para la empresa. */
@ExtendWith(MockitoExtension.class)
class AiCandidateRankingTest {

    @Mock private AiModelPort aiModelPort;
    @Mock private OfferRepositoryPort offerRepositoryPort;
    @Mock private StudentProfileRepositoryPort studentProfileRepositoryPort;
    @Mock private GetPostulationsUseCase getPostulationsUseCase;

    @InjectMocks private AiRecommendationUseCaseImpl useCase;

    @BeforeEach
    void oferta() {
        Offer o = new Offer();
        o.setIdOffer(7L);
        o.setPracticeName("Practicante Backend Java");
        o.setProfession("Ingeniería de Sistemas");
        o.setModality("Remoto");
        o.setPracticeDescription("Spring Boot y SQL");
        lenient().when(offerRepositoryPort.findById(7L)).thenReturn(o);
    }

    private static PostulationDetail postulado(long id, String nombre, String doc, String skills) {
        PostulationDetail p = new PostulationDetail();
        p.setIdPostulation(id);
        p.setStudentFullName(nombre);
        p.setDocumentStudent(doc);
        p.setStudentEmail(doc + "@ucundinamarca.edu.co");
        p.setSkills(skills);
        return p;
    }

    @SuppressWarnings("unchecked")
    private static List<CandidateRanking> ranking(Map<String, Object> res) {
        return (List<CandidateRanking>) res.get("ranking");
    }

    @Test
    void laIaOrdenaALosCandidatosYNoRecibeSusDatosPersonales() {
        when(getPostulationsUseCase.getPostulationsByOfferId(7L)).thenReturn(List.of(
                postulado(501, "Ana Pérez", "1070111", "Excel"),
                postulado(502, "Luis Gómez", "1070222", "Java, Spring, SQL")));
        lenient().when(studentProfileRepositoryPort.findByDocumentStudent(anyString())).thenReturn(null);
        when(aiModelPort.generateAnalysis(anyString())).thenReturn("""
                2|91|Domina Java y SQL; recomendado para entrevista.
                1|35|Su perfil no cubre el stack backend.
                7|99|ID que no existe.
                """);

        Map<String, Object> res = useCase.rankCandidatesForOffer(7L);
        List<CandidateRanking> r = ranking(res);

        assertEquals(Boolean.TRUE, res.get("aiPowered"));
        assertEquals(List.of(502L, 501L), r.stream().map(CandidateRanking::idPostulation).toList());
        assertEquals("Luis Gómez", r.get(0).studentFullName(), "el nombre sale de la BD, no de la IA");
        assertEquals(91, r.get(0).match());
        assertTrue(res.get("aiRankingAnalysis").toString().contains("#1. Luis Gómez — Match: 91%"));

        ArgumentCaptor<String> prompt = ArgumentCaptor.forClass(String.class);
        verify(aiModelPort).generateAnalysis(prompt.capture());
        assertFalse(prompt.getValue().contains("Ana Pérez"), "no se envía el nombre");
        assertFalse(prompt.getValue().contains("1070111"), "no se envía la cédula");
        assertFalse(prompt.getValue().contains("@ucundinamarca"), "no se envía el correo");
        assertTrue(prompt.getValue().contains("- ID 2 | Habilidades: [Java, Spring, SQL]"));
    }

    @Test
    void losCandidatosQueLaIaOmiteSeAgreganAlFinalConElCalculoLocal() {
        when(getPostulationsUseCase.getPostulationsByOfferId(7L)).thenReturn(List.of(
                postulado(501, "Ana Pérez", "1", "Excel"),
                postulado(502, "Luis Gómez", "2", "Java, SQL")));
        when(aiModelPort.generateAnalysis(anyString())).thenReturn("1|60|Perfil administrativo con potencial.");

        List<CandidateRanking> r = ranking(useCase.rankCandidatesForOffer(7L));

        assertEquals(2, r.size());
        assertEquals(501L, r.get(0).idPostulation());
        assertEquals(502L, r.get(1).idPostulation());
        assertTrue(r.get(1).reason().contains("Habilidades afines: java, sql"));
    }

    @Test
    void sinIaOrdenaPorHabilidadesConMotivoLocal() {
        when(getPostulationsUseCase.getPostulationsByOfferId(7L)).thenReturn(List.of(
                postulado(501, "Ana Pérez", "1", "Excel"),
                postulado(502, "Luis Gómez", "2", "Java, Spring, SQL")));
        StudentProfile perfil = new StudentProfile();
        perfil.setSkills("Java, Spring, SQL");
        perfil.setGithubUrl("https://github.com/luis");
        when(studentProfileRepositoryPort.findByDocumentStudent("2")).thenReturn(perfil);
        when(aiModelPort.generateAnalysis(anyString())).thenReturn("FALLBACK: sin cuota");

        Map<String, Object> res = useCase.rankCandidatesForOffer(7L);
        List<CandidateRanking> r = ranking(res);

        assertEquals(Boolean.FALSE, res.get("aiPowered"));
        assertEquals(502L, r.get(0).idPostulation(), "el de habilidades afines queda primero");
        assertTrue(r.get(0).match() > r.get(1).match());
        assertTrue(r.get(0).reason().contains("altamente recomendado"));
    }

    @Test
    void elMatchDelRankingSeAcotaEntre0Y100() {
        assertEquals(100, new CandidateRanking(1L, "x", 140, "m").match());
        assertEquals("", new CandidateRanking(1L, "x", 10, null).reason());
    }
}
