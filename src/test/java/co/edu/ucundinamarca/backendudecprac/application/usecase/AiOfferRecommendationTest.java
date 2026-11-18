package co.edu.ucundinamarca.backendudecprac.application.usecase;

import co.edu.ucundinamarca.backendudecprac.domain.model.Offer;
import co.edu.ucundinamarca.backendudecprac.domain.model.OfferRecommendation;
import co.edu.ucundinamarca.backendudecprac.domain.model.StudentProfile;
import co.edu.ucundinamarca.backendudecprac.domain.port.in.GetPostulationsUseCase;
import co.edu.ucundinamarca.backendudecprac.domain.port.out.AiModelPort;
import co.edu.ucundinamarca.backendudecprac.domain.port.out.OfferRepositoryPort;
import co.edu.ucundinamarca.backendudecprac.domain.port.out.StudentProfileRepositoryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/** Recomendación de ofertas al estudiante con respuesta estructurada (idOffer, match, motivo). */
@ExtendWith(MockitoExtension.class)
class AiOfferRecommendationTest {

    @Mock private AiModelPort aiModelPort;
    @Mock private OfferRepositoryPort offerRepositoryPort;
    @Mock private StudentProfileRepositoryPort studentProfileRepositoryPort;
    @Mock private GetPostulationsUseCase getPostulationsUseCase;

    @InjectMocks private AiRecommendationUseCaseImpl useCase;

    private static Offer oferta(long id, String nombre, String descripcion) {
        Offer o = new Offer();
        o.setIdOffer(id);
        o.setPracticeName(nombre);
        o.setProfession("Ingeniería de Sistemas");
        o.setModality("Remoto");
        o.setLocation("Fusagasugá");
        o.setAvailableSpots(1);
        o.setPracticeDescription(descripcion);
        return o;
    }

    private void perfilConHabilidades(String doc, String skills) {
        StudentProfile p = new StudentProfile();
        p.setSkills(skills);
        when(studentProfileRepositoryPort.findByDocumentStudent(doc)).thenReturn(p);
    }

    @SuppressWarnings("unchecked")
    private static List<OfferRecommendation> recomendaciones(Map<String, Object> res) {
        return (List<OfferRecommendation>) res.get("recommendations");
    }

    @Test
    void interpretaLaRespuestaDeLaIaYDescartaOfertasInventadasYRepetidas() {
        perfilConHabilidades("1", "Angular, Java");
        when(offerRepositoryPort.getActiveOffers()).thenReturn(List.of(
                oferta(10, "Frontend Angular", "Componentes Angular"),
                oferta(20, "Backend Java", "API con Java")));
        when(aiModelPort.generateAnalysis(anyString())).thenReturn("""
                Aquí tienes el ranking:
                20|75|Usa Java en el backend.
                1. [ID 10] | 92% | Domina Angular, igual que pide la oferta.
                999|99|Oferta que no existe.
                20|10|Repetida.
                """);

        Map<String, Object> res = useCase.recommendOffersForStudent("1");
        List<OfferRecommendation> recs = recomendaciones(res);

        assertEquals(Boolean.TRUE, res.get("aiPowered"));
        assertEquals(List.of(10L, 20L), recs.stream().map(OfferRecommendation::idOffer).toList(), "ordenadas por match");
        assertEquals(92, recs.get(0).match());
        assertEquals("Frontend Angular", recs.get(0).practiceName(), "el nombre sale de la BD, no de la IA");
        assertEquals("Domina Angular, igual que pide la oferta.", recs.get(0).reason());
        assertTrue(res.get("aiRecommendation").toString().contains("1. Frontend Angular — Compatibilidad: 92%"));
    }

    @Test
    void siLaIaRespondeEnTextoLibreUsaElCalculoLocal() {
        perfilConHabilidades("2", "Java, SQL");
        when(offerRepositoryPort.getActiveOffers()).thenReturn(List.of(oferta(7, "Backend Java", "Java y SQL")));
        when(aiModelPort.generateAnalysis(anyString())).thenReturn("Te recomiendo la oferta de backend, encaja al 95%.");

        Map<String, Object> res = useCase.recommendOffersForStudent("2");
        List<OfferRecommendation> recs = recomendaciones(res);

        assertEquals(Boolean.FALSE, res.get("aiPowered"));
        assertEquals(1, recs.size());
        assertEquals(7L, recs.get(0).idOffer());
        assertTrue(recs.get(0).reason().contains("java") && recs.get(0).reason().contains("sql"),
                "el motivo local menciona las habilidades que coinciden");
    }

    @Test
    void sinIaDisponibleRecomiendaConPuntajeLocalYMotivoGenerico() {
        when(studentProfileRepositoryPort.findByDocumentStudent("3")).thenThrow(new IllegalStateException("BD caída"));
        when(offerRepositoryPort.getActiveOffers()).thenReturn(List.of(oferta(5, "Auxiliar contable", "Conciliaciones")));
        when(aiModelPort.generateAnalysis(anyString())).thenReturn("FALLBACK: sin cuota");

        List<OfferRecommendation> recs = recomendaciones(useCase.recommendOffersForStudent("3"));

        assertEquals(1, recs.size());
        assertTrue(recs.get(0).reason().startsWith("Compatibilidad general"));
        assertTrue(recs.get(0).match() >= 0 && recs.get(0).match() <= 100);
    }

    /** Nombre sin dígitos (a, b, …, aa, ab…) para simular ofertas distintas entre sí. */
    private static String letras(int n) {
        StringBuilder sb = new StringBuilder();
        for (int x = n; x > 0; x = (x - 1) / 26) sb.insert(0, (char) ('a' + (x - 1) % 26));
        return sb.toString();
    }

    @Test
    void lasOfertasClonadasNoDesplazanAlRestoDelCatalogo() {
        perfilConHabilidades("5", "Angular, Java");
        List<Offer> ofertas = new java.util.ArrayList<>(List.of(
                oferta(1, "Frontend Angular", "Componentes Angular"),
                oferta(2, "Backend Java", "API con Java")));
        // 500 ofertas de carga masiva (más nuevas) que solo difieren en un UUID, como las de JMeter.
        IntStream.rangeClosed(1, 500).forEach(i -> ofertas.add(oferta(1000L + i,
                "Flujo JMeter " + java.util.UUID.randomUUID(), "Prueba JMeter")));
        when(offerRepositoryPort.getActiveOffers()).thenReturn(ofertas);
        when(aiModelPort.generateAnalysis(anyString())).thenReturn("FALLBACK:");

        Map<String, Object> res = useCase.recommendOffersForStudent("5");
        List<Long> ids = recomendaciones(res).stream().map(OfferRecommendation::idOffer).toList();

        assertEquals(502, res.get("totalOffersAnalyzed"));
        assertTrue(ids.containsAll(List.of(1L, 2L)), "las ofertas reales deben llegar: " + ids);
        assertEquals(3, ids.size(), "las 500 clones cuentan como una sola");
    }

    @Test
    void conCientosDeOfertasSoloEnviaLasMasAfinesALaIaYDevuelveUnTop() {
        perfilConHabilidades("4", "Angular");
        List<Offer> muchas = IntStream.rangeClosed(1, 500)
                .mapToObj(i -> i == 321
                        ? oferta(i, "Desarrollador Angular", "Angular y TypeScript")
                        : oferta(i, "Auxiliar administrativo " + letras(i), "Archivo y atención al cliente"))
                .toList();
        when(offerRepositoryPort.getActiveOffers()).thenReturn(muchas);
        when(aiModelPort.generateAnalysis(anyString())).thenReturn("FALLBACK:");

        Map<String, Object> res = useCase.recommendOffersForStudent("4");

        ArgumentCaptor<String> prompt = ArgumentCaptor.forClass(String.class);
        verify(aiModelPort).generateAnalysis(prompt.capture());
        long ofertasEnPrompt = prompt.getValue().lines().filter(l -> l.startsWith("- ID ")).count();
        assertEquals(AiRecommendationUseCaseImpl.MAX_OFERTAS_PARA_IA, ofertasEnPrompt);
        assertTrue(prompt.getValue().contains("- ID 321 |"), "la oferta afín pasa el prefiltro");
        assertFalse(prompt.getValue().contains("CC"), "no se envía la cédula del estudiante a la IA");

        List<OfferRecommendation> recs = recomendaciones(res);
        assertEquals(500, res.get("totalOffersAnalyzed"));
        assertEquals(AiRecommendationUseCaseImpl.MAX_RECOMENDACIONES, recs.size());
        assertEquals(321L, recs.get(0).idOffer(), "la más afín queda primera");
    }

    @Test
    void elMatchSeAcotaEntre0Y100() {
        assertEquals(100, new OfferRecommendation(1L, "x", 150, "m").match());
        assertEquals(0, new OfferRecommendation(1L, "x", -5, null).match());
        assertEquals("", new OfferRecommendation(1L, "x", 50, null).reason());
    }
}
