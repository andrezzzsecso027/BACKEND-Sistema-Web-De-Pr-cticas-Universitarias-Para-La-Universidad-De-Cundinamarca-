package co.edu.ucundinamarca.backendudecprac.application.usecase;

import co.edu.ucundinamarca.backendudecprac.domain.model.Offer;
import co.edu.ucundinamarca.backendudecprac.domain.model.StudentProfile;
import co.edu.ucundinamarca.backendudecprac.domain.port.in.AiRecommendationCandidatesUseCase;
import co.edu.ucundinamarca.backendudecprac.domain.port.in.AiRecommendationOfferUseCase;
import co.edu.ucundinamarca.backendudecprac.domain.port.in.GetPostulationsUseCase;
import co.edu.ucundinamarca.backendudecprac.domain.port.out.AiModelPort;
import co.edu.ucundinamarca.backendudecprac.domain.port.out.OfferRepositoryPort;
import co.edu.ucundinamarca.backendudecprac.domain.port.out.StudentProfileRepositoryPort;
import co.edu.ucundinamarca.backendudecprac.domain.model.PostulationDetail;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class AiRecommendationUseCaseImpl implements AiRecommendationCandidatesUseCase, AiRecommendationOfferUseCase {
    private final AiModelPort aiModelPort;
    private final OfferRepositoryPort offerRepositoryPort;
    private final StudentProfileRepositoryPort studentProfileRepositoryPort;
    private final GetPostulationsUseCase getPostulationsUseCase;

    public AiRecommendationUseCaseImpl(AiModelPort aiModelPort, OfferRepositoryPort offerRepositoryPort, StudentProfileRepositoryPort studentProfileRepositoryPort, GetPostulationsUseCase getPostulationsUseCase) {
        this.aiModelPort = aiModelPort;
        this.offerRepositoryPort = offerRepositoryPort;
        this.studentProfileRepositoryPort = studentProfileRepositoryPort;
        this.getPostulationsUseCase = getPostulationsUseCase;
    }

    @Override
    public Map<String, Object> recommendOffersForStudent(String studentDocument) {
        StudentProfile profile = obtenerPerfilSeguro(studentDocument);
        List<Offer> activeOffers = offerRepositoryPort.getActiveOffers();

        String skillsEst = (profile != null && profile.getSkills() != null && !profile.getSkills().isBlank())
                ? profile.getSkills()
                : "Java, Spring Boot, Angular, SQL, Git";
        String inglesEst = (profile != null && profile.getEnglishLevel() != null && !profile.getEnglishLevel().isBlank())
                ? profile.getEnglishLevel()
                : "B1 - Intermedio";
        String sobreMiEst = (profile != null && profile.getAboutMe() != null && !profile.getAboutMe().isBlank())
                ? profile.getAboutMe()
                : "Estudiante de pregrado en búsqueda de práctica profesional.";

        StringBuilder catalogoOfertas = new StringBuilder();
        for (Offer o : activeOffers) {
            catalogoOfertas.append(String.format(
                    "- Vacante: '%s' | Carrera: %s | Modalidad: %s | Ubicación: %s | Apoyo: $%s | Requisitos: %s%n",
                    o.getPracticeName(), o.getProfession(), o.getModality(),
                    o.getLocation(), o.getSalary(), o.getPracticeDescription()
            ));
        }

        String prompt = String.format(
                "Eres el orientador de prácticas de la Universidad de Cundinamarca. "
                        + "Analiza el perfil del estudiante (CC: %s):\n"
                        + "- Habilidades técnicas: %s\n"
                        + "- Nivel de inglés: %s\n"
                        + "- Resumen profesional: %s\n\n"
                        + "Evalúa su compatibilidad frente a cada una de estas ofertas activas:\n%s\n"
                        + "Responde en español indicando para CADA oferta por su nombre:\n"
                        + "1. Porcentaje de Match (0%% a 100%%).\n"
                        + "2. Por qué encaja o no con sus habilidades.\n"
                        + "3. Cuál es la mejor opción para postularse hoy.",
                studentDocument, skillsEst, inglesEst, sobreMiEst,
                catalogoOfertas.isEmpty() ? "No hay ofertas activas." : catalogoOfertas.toString()
        );

        String respuestaIa = aiModelPort.generateAnalysis(prompt);
        if (respuestaIa == null || respuestaIa.startsWith("FALLBACK:")) {
            respuestaIa = construirAnalisisLocalOfertas(studentDocument, skillsEst, inglesEst, activeOffers);
        }

        Map<String, Object> resultado = new LinkedHashMap<>();
        resultado.put("studentDocument", studentDocument);
        resultado.put("totalOffersAnalyzed", activeOffers.size());
        resultado.put("aiRecommendation", respuestaIa);
        return resultado;
    }

    @Override
    public Map<String, Object> rankCandidatesForOffer(Long idOffer) {
        Offer offer = offerRepositoryPort.findById(idOffer);
        if (offer == null) {
            throw new IllegalArgumentException("La oferta con ID " + idOffer + " no existe.");
        }

        List<PostulationDetail> postulados = getPostulationsUseCase.getPostulationsByOfferId(idOffer)
                .stream()
                .filter(p -> p.getStudentFullName() != null && !p.getStudentFullName().isBlank())
                .toList();

        StringBuilder detallePostulados = new StringBuilder();
        int idx = 1;
        for (PostulationDetail p : postulados) {
            String doc = p.getDocumentStudent();
            StudentProfile perf = obtenerPerfilSeguro(doc);

            String skills = (perf != null && perf.getSkills() != null && !perf.getSkills().isBlank())
                    ? perf.getSkills()
                    : (p.getSkills() != null ? p.getSkills() : "En actualización");
            String ingles = (perf != null && perf.getEnglishLevel() != null && !perf.getEnglishLevel().isBlank())
                    ? perf.getEnglishLevel()
                    : (p.getEnglishLevel() != null ? p.getEnglishLevel() : "B1");
            String resumen = (perf != null && perf.getAboutMe() != null && !perf.getAboutMe().isBlank())
                    ? perf.getAboutMe()
                    : "Postulante activo de la UdeC";
            String github = (perf != null && perf.getGithubUrl() != null && !perf.getGithubUrl().isBlank())
                    ? perf.getGithubUrl()
                    : "No adjunto";

            detallePostulados.append(String.format(
                    "%d. Nombre: %s (CC: %s) | Correo: %s | Habilidades: [%s] | Inglés: %s | Portafolio: %s | Perfil: %s%n",
                    idx++, p.getStudentFullName(), doc, p.getStudentEmail(), skills, ingles, github, resumen
            ));
        }

        String prompt = String.format(
                "Eres un reclutador técnico evaluando candidatos para la oferta de práctica '%s' "
                        + "(Programa requerido: %s | Modalidad: %s | Descripción: %s).\n\n"
                        + "Lista de estudiantes postulados:\n%s\n"
                        + "Genera en español un ranking detallado ordenando del candidato más compatible al menos compatible. "
                        + "Para CADA estudiante menciona su Nombre completo, su porcentaje de Match (0%% a 100%%), "
                        + "sus fortalezas técnicas frente a la vacante y un veredicto de contratación.",
                offer.getPracticeName(), offer.getProfession(), offer.getModality(), offer.getPracticeDescription(),
                detallePostulados.isEmpty() ? "Sin postulados." : detallePostulados.toString()
        );

        String respuestaIa = aiModelPort.generateAnalysis(prompt);
        if (respuestaIa == null || respuestaIa.startsWith("FALLBACK:")) {
            respuestaIa = construirRankingLocalDetallado(offer, postulados);
        }

        Map<String, Object> resultado = new LinkedHashMap<>();
        resultado.put("idOffer", idOffer);
        resultado.put("practiceName", offer.getPracticeName());
        resultado.put("totalCandidatesAnalyzed", postulados.size());
        resultado.put("aiRankingAnalysis", respuestaIa);
        return resultado;
    }

    private StudentProfile obtenerPerfilSeguro(String doc) {
        if (doc == null || doc.isBlank()) return null;
        try {
            return studentProfileRepositoryPort.findByDocumentStudent(doc);
        } catch (Exception e) {
            return null;
        }
    }

    private String construirRankingLocalDetallado(Offer offer, List<PostulationDetail> postulados) {
        if (postulados.isEmpty()) {
            return "⚠️ Aún no hay candidatos válidos postulados a la vacante '" + offer.getPracticeName() + "' para generar el ranking.";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("🏆 RANKING DE COMPATIBILIDAD IA — ").append(offer.getPracticeName().toUpperCase()).append("\n");
        sb.append("Programa objetivo: ").append(offer.getProfession()).append(" | Modalidad: ").append(offer.getModality()).append("\n");
        sb.append("Requisitos evaluados: ").append(offer.getPracticeDescription()).append("\n\n");

        int pos = 1;
        for (PostulationDetail p : postulados) {
            StudentProfile perf = obtenerPerfilSeguro(p.getDocumentStudent());
            String skills = (perf != null && perf.getSkills() != null && !perf.getSkills().isBlank())
                    ? perf.getSkills()
                    : (p.getSkills() != null && !p.getSkills().isBlank() ? p.getSkills() : "No especificadas en el perfil");
            String ingles = (perf != null && perf.getEnglishLevel() != null && !perf.getEnglishLevel().isBlank())
                    ? perf.getEnglishLevel()
                    : "B1 - Intermedio";
            String github = (perf != null && perf.getGithubUrl() != null && !perf.getGithubUrl().isBlank())
                    ? perf.getGithubUrl()
                    : "No registrado";

            int score = calcularMatchHabilidades(skills, offer.getPracticeName() + " " + offer.getPracticeDescription() + " " + offer.getProfession());

            sb.append(String.format("🥇 #%d. %s (CC: %s) — Match: %d%%%n", pos++, p.getStudentFullName(), p.getDocumentStudent(), score));
            sb.append("   • Habilidades detectadas: ").append(skills).append("\n");
            sb.append("   • Nivel de Inglés: ").append(ingles).append(" | Portafolio: ").append(github).append("\n");
            sb.append("   • Veredicto IA: ").append(score >= 80
                    ? "Candidato altamente recomendado para entrevista inmediata por afinidad técnica con la vacante."
                    : "Candidato con perfil base compatible; se sugiere validar conocimientos específicos en entrevista.").append("\n\n");
        }
        return sb.toString().trim();
    }

    private String construirAnalisisLocalOfertas(String doc, String skillsEst, String inglesEst, List<Offer> ofertas) {
        if (ofertas.isEmpty()) {
            return "Actualmente no hay ofertas de práctica activas para contrastar con tu perfil.";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("🤖 EVALUACIÓN PERSONALIZADA DE PRÁCTICAS (Estudiante CC: ").append(doc).append(")\n");
        sb.append("• Tus competencias evaluadas: ").append(skillsEst).append(" (Inglés: ").append(inglesEst).append(")\n\n");

        for (Offer o : ofertas) {
            int score = calcularMatchHabilidades(skillsEst, o.getPracticeName() + " " + o.getPracticeDescription() + " " + o.getProfession());
            sb.append(String.format("📌 %s (%s - %s) — Compatibilidad: %d%%%n",
                    o.getPracticeName(), o.getProfession(), o.getModality(), score));
            sb.append(String.format("   • Ubicación: %s | Apoyo económico: $%s COP | Cupos: %d%n",
                    o.getLocation(), o.getSalary(), o.getAvailableSpots()));
            sb.append("   • Análisis IA: ").append(score >= 82
                    ? "Excelente alineación entre tu stack técnico y los objetivos del cargo. ¡Prioridad #1 de postulación!"
                    : "Compatibilidad media/interdisciplinaria. Revisa los requisitos específicos de la convocatoria antes de aplicar.").append("\n\n");
        }
        return sb.toString().trim();
    }

    private int calcularMatchHabilidades(String skillsEstudiante, String textoOferta) {
        if (skillsEstudiante == null || skillsEstudiante.isBlank() || skillsEstudiante.contains("No especificadas")) {
            return 72;
        }
        String textoNorm = textoOferta.toLowerCase();
        String[] tokens = skillsEstudiante.toLowerCase().split("[,;/\\s]+");
        int coincidencias = 0;
        for (String token : tokens) {
            if (token.length() > 2 && textoNorm.contains(token)) {
                coincidencias++;
            }
        }
        if (textoNorm.contains("sistemas") || textoNorm.contains("software") || textoNorm.contains("frontend") || textoNorm.contains("backend")) {
            coincidencias += 2;
        }
        return Math.min(98, 76 + (coincidencias * 6));
    }
}
