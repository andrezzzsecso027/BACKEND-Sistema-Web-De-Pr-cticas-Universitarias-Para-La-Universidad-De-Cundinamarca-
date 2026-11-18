package co.edu.ucundinamarca.backendudecprac.application.usecase;
import co.edu.ucundinamarca.backendudecprac.domain.model.Offer;
import co.edu.ucundinamarca.backendudecprac.domain.model.StudentProfile;
import co.edu.ucundinamarca.backendudecprac.domain.port.in.GetPostulationsUseCase;
import co.edu.ucundinamarca.backendudecprac.domain.port.out.AiModelPort;
import co.edu.ucundinamarca.backendudecprac.domain.port.out.OfferRepositoryPort;
import co.edu.ucundinamarca.backendudecprac.domain.port.out.StudentProfileRepositoryPort;
import co.edu.ucundinamarca.backendudecprac.infrastructure.adapter.in.web.controllers.AiController;
import co.edu.ucundinamarca.backendudecprac.domain.model.PostulationDetail;
import co.edu.ucundinamarca.backendudecprac.infrastructure.adapter.out.external.ai.GeminiAiAdapter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AiRecommendationUseCaseTest {
    @Mock
    private AiModelPort aiModelPort;

    @Mock
    private OfferRepositoryPort offerRepositoryPort;

    @Mock
    private StudentProfileRepositoryPort studentProfileRepositoryPort;

    @Mock
    private GetPostulationsUseCase getPostulationsUseCase;

    @InjectMocks
    private AiRecommendationUseCaseImpl aiUseCase;

    @Test
    void debeRecomendarOfertasCuandoPerfilEsNuloYNoHayOfertas() {
        when(studentProfileRepositoryPort.findByDocumentStudent("0000")).thenReturn(null);
        when(offerRepositoryPort.getActiveOffers()).thenReturn(Collections.emptyList());

        Map<String, Object> res = aiUseCase.recommendOffersForStudent("0000");
        assertEquals(0, res.get("totalOffersAnalyzed"));
        assertEquals("0000", res.get("studentDocument"));
    }

    @Test
    void debeGenerarRankingDePostuladosYValidarOfertaInexistente() {
        Offer oferta = new Offer();
        oferta.setIdOffer(10L);
        oferta.setPracticeName("Analista QA");
        oferta.setProfession("Ingeniería de Sistemas");
        oferta.setPracticeDescription("Pruebas con JMeter y Playwright");

        when(offerRepositoryPort.findById(10L)).thenReturn(oferta);
        // El caso de uso descarta postulantes sin nombre, así que el candidato de prueba debe tenerlo
        PostulationDetail candidato = new PostulationDetail();
        candidato.setStudentFullName("Brayan Alfaro");
        candidato.setDocumentStudent("1003558974");
        when(getPostulationsUseCase.getPostulationsByOfferId(10L)).thenReturn(List.of(candidato));
        when(aiModelPort.generateAnalysis(anyString())).thenReturn("1. Brayan Alfaro - 96% Match");

        AiController controller = new AiController(aiUseCase, aiUseCase);
        ResponseEntity<Object> okResponse = controller.generarRankingPostulados(10L);
        assertEquals(HttpStatus.OK, okResponse.getStatusCode());

        Map<?, ?> okBody = (Map<?, ?>) okResponse.getBody();
        assertNotNull(okBody);
        assertEquals(1, okBody.get("totalCandidatesAnalyzed"));

        when(offerRepositoryPort.findById(999L)).thenReturn(null);
        ResponseEntity<Object> notFoundResponse = controller.generarRankingPostulados(999L);
        assertEquals(HttpStatus.NOT_FOUND, notFoundResponse.getStatusCode());
    }

    @Test
    void debeUsarFallbackResilienteCuandoNoHayApiKeyOFallaLaRedEnGeminiAdapter() {
        // 1. Sin API Key (cadena vacía)
        GeminiAiAdapter adapterSinKey = new GeminiAiAdapter("", "https://localhost");
        String respRanking = adapterSinKey.generateAnalysis("genera un ranking de compatibilidad");
        String respEst = adapterSinKey.generateAnalysis("Analiza el siguiente perfil de estudiante");

        assertTrue(respRanking.contains("Análisis IA UdecPrac"));
        assertTrue(respEst.contains("Recomendación IA UdecPrac"));

        // 2. Con API Key de prueba hacia puerto local inactivo (cubre el bloque try/catch de red)
        GeminiAiAdapter adapterErrorRed = new GeminiAiAdapter("AIzaFakeKeyTest", "http://localhost:1/invalid");
        String respFallbackRed = adapterErrorRed.generateAnalysis("genera un ranking de compatibilidad");
        assertTrue(respFallbackRed.contains("Análisis IA UdecPrac"));
    }

    private static Offer ofertaBackend() {
        Offer o = new Offer();
        o.setIdOffer(7L);
        o.setPracticeName("Practicante Backend Java");
        o.setProfession("Ingeniería de Sistemas");
        o.setModality("Remoto");
        o.setLocation("Fusagasugá");
        o.setAvailableSpots(2);
        o.setPracticeDescription("Spring Boot, SQL y Git");
        return o;
    }

    private static StudentProfile perfilCompleto() {
        StudentProfile perfil = new StudentProfile();
        perfil.setSkills("Java, Spring, SQL, Git");
        perfil.setEnglishLevel("B2");
        perfil.setAboutMe("Desarrollador backend");
        perfil.setGithubUrl("https://github.com/udec");
        return perfil;
    }

    @Test
    void sinIaYSinOfertasInformaQueNoHayOfertas() {
        when(offerRepositoryPort.getActiveOffers()).thenReturn(Collections.emptyList());

        String analisis = aiUseCase.recommendOffersForStudent("").get("aiRecommendation").toString();

        assertTrue(analisis.contains("no hay ofertas de práctica activas"));
    }

    @Test
    void sinIaGeneraRankingLocalConVeredictoPorCandidato() {
        when(offerRepositoryPort.findById(7L)).thenReturn(ofertaBackend());
        PostulationDetail conPerfil = new PostulationDetail();
        conPerfil.setStudentFullName("Ana Pérez");
        conPerfil.setDocumentStudent("111");
        PostulationDetail sinPerfil = new PostulationDetail();
        sinPerfil.setStudentFullName("Luis Gómez");
        sinPerfil.setDocumentStudent("222");
        sinPerfil.setSkills("No especificadas");
        PostulationDetail sinNombre = new PostulationDetail();
        sinNombre.setStudentFullName(" ");
        when(getPostulationsUseCase.getPostulationsByOfferId(7L)).thenReturn(List.of(conPerfil, sinPerfil, sinNombre));
        when(studentProfileRepositoryPort.findByDocumentStudent("111")).thenReturn(perfilCompleto());
        when(aiModelPort.generateAnalysis(anyString())).thenReturn("FALLBACK: cuota agotada");

        Map<String, Object> res = aiUseCase.rankCandidatesForOffer(7L);
        String ranking = res.get("aiRankingAnalysis").toString();

        assertEquals(2, res.get("totalCandidatesAnalyzed"), "descarta postulantes sin nombre");
        assertTrue(ranking.contains("RANKING DE COMPATIBILIDAD IA"));
        assertTrue(ranking.contains("Ana Pérez") && ranking.contains("Luis Gómez"));
        assertTrue(ranking.contains("altamente recomendado"));
        assertTrue(ranking.contains("validar conocimientos"));
    }

    @Test
    void sinIaYSinPostuladosAvisaQueNoHayCandidatos() {
        when(offerRepositoryPort.findById(7L)).thenReturn(ofertaBackend());
        when(getPostulationsUseCase.getPostulationsByOfferId(7L)).thenReturn(Collections.emptyList());

        String ranking = aiUseCase.rankCandidatesForOffer(7L).get("aiRankingAnalysis").toString();

        assertTrue(ranking.contains("Aún no hay candidatos"));
    }

    @Test
    void ofertaInexistenteEnRankingLanzaExcepcion() {
        when(offerRepositoryPort.findById(5L)).thenReturn(null);
        assertThrows(IllegalArgumentException.class, () -> aiUseCase.rankCandidatesForOffer(5L));
    }

    @Test
    void primerNoVacioDevuelveElPrimerValorConContenido() {
        assertEquals("b", AiRecommendationUseCaseImpl.primerNoVacio(null, " ", "b", "c"));
        assertEquals("defecto", AiRecommendationUseCaseImpl.primerNoVacio(null, "", "defecto"));
    }
}
