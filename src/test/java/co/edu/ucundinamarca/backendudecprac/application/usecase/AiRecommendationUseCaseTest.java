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
public class AiRecommendationUseCaseTest {
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
    void debeRecomendarOfertasParaEstudianteConIa() {
        Offer oferta = new Offer();
        oferta.setIdOffer(1L);
        oferta.setPracticeName("Desarrollador Backend Spring Boot");
        oferta.setProfession("Ingeniería de Sistemas");
        oferta.setModality("Remoto");
        oferta.setLocation("Fusagasugá");
        oferta.setPracticeDescription("API REST con Arquitectura Hexagonal");

        when(studentProfileRepositoryPort.findByDocumentStudent("1003558974")).thenReturn(new StudentProfile());
        when(offerRepositoryPort.getActiveOffers()).thenReturn(List.of(oferta));
        when(aiModelPort.generateAnalysis(anyString())).thenReturn("Match 95%: Excelente perfil para Backend.");

        AiController controller = new AiController(aiUseCase, aiUseCase);
        ResponseEntity<?> response = controller.recomendarOfertasEstudiante("1003558974");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        Map<?, ?> body = (Map<?, ?>) response.getBody();
        assertEquals(1, body.get("totalOffersAnalyzed"));
        assertTrue(body.get("aiRecommendation").toString().contains("Match 95%"));
    }

    @Test
    void debeRecomendarOfertasCuandoPerfilEsNuloYNoHayOfertas() {
        when(studentProfileRepositoryPort.findByDocumentStudent("0000")).thenReturn(null);
        when(offerRepositoryPort.getActiveOffers()).thenReturn(Collections.emptyList());
        when(aiModelPort.generateAnalysis(anyString())).thenReturn("Sin ofertas disponibles");

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
}
