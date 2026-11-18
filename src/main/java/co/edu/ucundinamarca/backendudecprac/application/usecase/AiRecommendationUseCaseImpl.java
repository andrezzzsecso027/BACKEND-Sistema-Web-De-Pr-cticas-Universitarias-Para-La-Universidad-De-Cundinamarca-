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

import co.edu.ucundinamarca.backendudecprac.domain.model.CandidateRanking;
import co.edu.ucundinamarca.backendudecprac.domain.model.OfferRecommendation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

@Service
public class AiRecommendationUseCaseImpl implements AiRecommendationCandidatesUseCase, AiRecommendationOfferUseCase {

    private static final String PREFIJO_FALLBACK = "FALLBACK:";

    /** Ofertas que se envían a la IA tras el prefiltro local (con cientos de ofertas el prompt sería lento y costoso). */
    static final int MAX_OFERTAS_PARA_IA = 30;
    /** Recomendaciones que se devuelven al estudiante. */
    static final int MAX_RECOMENDACIONES = 10;

    /** Una línea de respuesta de la IA: ID|MATCH|MOTIVO (tolera viñetas, corchetes, "%" y espacios). */
    private static final Pattern LINEA_RECOMENDACION =
            Pattern.compile("^[\\s\\-*•\\d.)]*?\\[?(?:ID\\s*)?(\\d+)]?\\s*\\|\\s*(\\d{1,3})\\s*%?\\s*\\|\\s*(.+)$", Pattern.CASE_INSENSITIVE);

    private static final String PROMPT_OFERTAS = """
            Eres el orientador de prácticas de la Universidad de Cundinamarca. \
            Analiza el perfil del estudiante:
            - Habilidades técnicas: %s
            - Nivel de inglés: %s
            - Resumen profesional: %s

            Ofertas de práctica abiertas (cada una con su ID):
            %s
            Elige las %d ofertas más compatibles con el estudiante, de la más a la menos compatible.
            Responde ÚNICAMENTE con una línea por oferta, sin encabezados ni texto adicional, con este formato exacto:
            ID|MATCH|MOTIVO
            donde ID es el número de la oferta, MATCH un entero de 0 a 100 y MOTIVO una frase en español \
            (máximo 200 caracteres) que explique por qué encaja con sus habilidades.
            Ejemplo: 42|88|Su experiencia con Angular y TypeScript coincide con el desarrollo frontend del cargo.""";

    /** Postulados que se envían a la IA en un ranking (los demás se ordenan con el cálculo local). */
    static final int MAX_CANDIDATOS_PARA_IA = 50;

    // Solo se envía el perfil técnico: ni nombre, ni cédula, ni correo del estudiante salen hacia la IA.
    private static final String PROMPT_RANKING = """
            Eres un reclutador técnico evaluando candidatos para la oferta de práctica '%s' \
            (Programa requerido: %s | Modalidad: %s | Descripción: %s).

            Candidatos postulados (cada uno con su ID):
            %s
            Ordena a TODOS los candidatos del más al menos compatible con la vacante.
            Responde ÚNICAMENTE con una línea por candidato, sin encabezados ni texto adicional, con este formato exacto:
            ID|MATCH|MOTIVO
            donde ID es el número del candidato, MATCH un entero de 0 a 100 y MOTIVO una frase en español \
            (máximo 200 caracteres) con sus fortalezas frente a la vacante y un veredicto de contratación.
            Ejemplo: 3|85|Domina Java y SQL como pide la vacante; recomendado para entrevista.""";

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

        String skillsEst = primerNoVacio(dePerfil(profile, StudentProfile::getSkills), "Java, Spring Boot, Angular, SQL, Git");
        String inglesEst = primerNoVacio(dePerfil(profile, StudentProfile::getEnglishLevel), "B1 - Intermedio");
        String sobreMiEst = primerNoVacio(dePerfil(profile, StudentProfile::getAboutMe),
                "Estudiante de pregrado en búsqueda de práctica profesional.");

        // 1. Prefiltro local: solo las ofertas más afines llegan a la IA. Las ofertas casi idénticas
        //    (mismo nombre salvo números/códigos, p. ej. cargas masivas) cuentan como una sola, para que
        //    no desplacen al resto del catálogo.
        Map<String, Offer> unaPorPlantilla = new LinkedHashMap<>();
        activeOffers.stream()
                .filter(o -> o.getIdOffer() != null)
                .sorted(Comparator.comparingInt((Offer o) -> calcularMatchHabilidades(skillsEst, textoOferta(o))).reversed()
                        .thenComparing(Offer::getIdOffer, Comparator.reverseOrder()))
                .forEach(o -> unaPorPlantilla.putIfAbsent(plantillaOferta(o), o));
        List<Offer> candidatas = unaPorPlantilla.values().stream()
                .limit(MAX_OFERTAS_PARA_IA)
                .toList();

        // 2. IA con respuesta estructurada; si no responde o responde en otro formato, cálculo local.
        List<OfferRecommendation> recomendaciones = List.of();
        boolean generadoPorIa = false;
        if (!candidatas.isEmpty()) {
            StringBuilder catalogo = new StringBuilder();
            for (Offer o : candidatas) {
                catalogo.append(String.format("- ID %d | '%s' | Carrera: %s | Modalidad: %s | Ubicación: %s | Requisitos: %s%n",
                        o.getIdOffer(), o.getPracticeName(), o.getProfession(), o.getModality(),
                        o.getLocation(), o.getPracticeDescription()));
            }
            String respuestaIa = aiModelPort.generateAnalysis(PROMPT_OFERTAS.formatted(
                    skillsEst, inglesEst, sobreMiEst, catalogo, Math.min(MAX_RECOMENDACIONES, candidatas.size())));
            if (!esFallback(respuestaIa)) {
                recomendaciones = interpretarRecomendaciones(respuestaIa, candidatas);
                generadoPorIa = !recomendaciones.isEmpty();
            }
            if (!generadoPorIa) {
                recomendaciones = recomendacionesLocales(skillsEst, candidatas);
            }
        }

        Map<String, Object> resultado = new LinkedHashMap<>();
        resultado.put("studentDocument", studentDocument);
        resultado.put("totalOffersAnalyzed", activeOffers.size());
        resultado.put("aiPowered", generadoPorIa);
        resultado.put("recommendations", recomendaciones);
        resultado.put("aiRecommendation", resumenTexto(recomendaciones));
        return resultado;
    }

    /**
     * Convierte las líneas "ID|MATCH|MOTIVO" de la IA en recomendaciones. Descarta IDs que no estén
     * entre las ofertas enviadas (la IA no puede inventar ofertas) y los repetidos; ordena por match.
     */
    List<OfferRecommendation> interpretarRecomendaciones(String respuestaIa, List<Offer> candidatas) {
        Map<Long, Offer> porId = new LinkedHashMap<>();
        candidatas.forEach(o -> porId.put(o.getIdOffer(), o));
        return parsearLineas(respuestaIa, porId.keySet()).stream()
                .map(l -> new OfferRecommendation(l.id(), porId.get(l.id()).getPracticeName(), l.match(), l.motivo()))
                .sorted(Comparator.comparingInt(OfferRecommendation::match).reversed())
                .limit(MAX_RECOMENDACIONES)
                .toList();
    }

    /** Una línea válida de la respuesta de la IA. */
    record LineaIa(long id, int match, String motivo) { }

    /** Líneas "ID|MATCH|MOTIVO" cuyo ID está entre los permitidos, sin repetidos y en el orden recibido. */
    static List<LineaIa> parsearLineas(String respuestaIa, Set<Long> idsPermitidos) {
        Map<Long, LineaIa> lineas = new LinkedHashMap<>();
        for (String linea : respuestaIa.split("\\R")) {
            Matcher m = LINEA_RECOMENDACION.matcher(linea.trim());
            if (!m.matches()) continue;
            long id = Long.parseLong(m.group(1));
            if (!idsPermitidos.contains(id) || lineas.containsKey(id)) continue;
            String motivo = m.group(3).trim();
            if (motivo.length() > 240) motivo = motivo.substring(0, 237) + "...";
            lineas.put(id, new LineaIa(id, Integer.parseInt(m.group(2)), motivo));
        }
        return List.copyOf(lineas.values());
    }

    /** Respaldo sin IA: las ofertas del prefiltro con su puntaje local y las habilidades que coinciden. */
    private List<OfferRecommendation> recomendacionesLocales(String skillsEst, List<Offer> candidatas) {
        return candidatas.stream()
                .limit(MAX_RECOMENDACIONES)
                .map(o -> {
                    List<String> coinciden = habilidadesCoincidentes(skillsEst, textoOferta(o));
                    String motivo = coinciden.isEmpty()
                            ? "Compatibilidad general con tu programa y perfil. Revisa los requisitos antes de postularte."
                            : "Coincide con tus habilidades en " + String.join(", ", coinciden) + ".";
                    return new OfferRecommendation(o.getIdOffer(), o.getPracticeName(),
                            calcularMatchHabilidades(skillsEst, textoOferta(o)), motivo);
                })
                .toList();
    }

    /** Versión en texto (campo aiRecommendation) para clientes que aún no usan la lista estructurada. */
    private static String resumenTexto(List<OfferRecommendation> recomendaciones) {
        if (recomendaciones.isEmpty()) {
            return "Actualmente no hay ofertas de práctica activas para contrastar con tu perfil.";
        }
        StringBuilder sb = new StringBuilder();
        int pos = 1;
        for (OfferRecommendation r : recomendaciones) {
            sb.append(String.format("%d. %s — Compatibilidad: %d%%%n   %s%n", pos++, r.practiceName(), r.match(), r.reason()));
        }
        return sb.toString().trim();
    }

    /** Huella de una oferta sin números ni códigos (UUID, consecutivos): agrupa ofertas clonadas. */
    static String plantillaOferta(Offer o) {
        String base = (o.getPracticeName() + " | " + o.getProfession() + " | " + o.getPracticeDescription())
                .toLowerCase();
        return base.replaceAll("[0-9a-f]{8}(-[0-9a-f]{4}){3}-[0-9a-f]{12}", "")
                .replaceAll("\\d+", "")
                .replaceAll("\\s+", " ")
                .trim();
    }

    private static String textoOferta(Offer o) {
        return o.getPracticeName() + " " + o.getPracticeDescription() + " " + o.getProfession();
    }

    @Override
    public Map<String, Object> rankCandidatesForOffer(Long idOffer) {
        Offer offer = offerRepositoryPort.findById(idOffer);
        if (offer == null) {
            throw new IllegalArgumentException("La oferta con ID " + idOffer + " no existe.");
        }

        List<Candidato> candidatos = getPostulationsUseCase.getPostulationsByOfferId(idOffer)
                .stream()
                .filter(p -> p.getStudentFullName() != null && !p.getStudentFullName().isBlank())
                .map(this::aCandidato)
                .toList();

        List<CandidateRanking> local = rankingLocal(candidatos, textoOferta(offer));
        List<CandidateRanking> ranking = local;
        boolean generadoPorIa = false;
        if (!candidatos.isEmpty()) {
            List<Candidato> enviados = candidatos.subList(0, Math.min(MAX_CANDIDATOS_PARA_IA, candidatos.size()));
            List<CandidateRanking> deIa = rankingConIa(offer, enviados);
            if (!deIa.isEmpty()) {
                generadoPorIa = true;
                // Los candidatos que la IA omitió (o que no se le enviaron) van al final con el cálculo local.
                List<CandidateRanking> completo = new ArrayList<>(deIa);
                local.stream().filter(r -> deIa.stream().noneMatch(x -> x.studentFullName().equals(r.studentFullName())
                        && Objects.equals(x.idPostulation(), r.idPostulation()))).forEach(completo::add);
                ranking = List.copyOf(completo);
            }
        }

        Map<String, Object> resultado = new LinkedHashMap<>();
        resultado.put("idOffer", idOffer);
        resultado.put("practiceName", offer.getPracticeName());
        resultado.put("totalCandidatesAnalyzed", candidatos.size());
        resultado.put("aiPowered", generadoPorIa);
        resultado.put("ranking", ranking);
        resultado.put("aiRankingAnalysis", resumenRanking(offer, ranking));
        return resultado;
    }

    /** Ranking de la IA. En el prompt cada candidato se identifica por su posición (1..n), no por datos personales. */
    private List<CandidateRanking> rankingConIa(Offer offer, List<Candidato> enviados) {
        StringBuilder detalle = new StringBuilder();
        for (int i = 0; i < enviados.size(); i++) {
            Candidato c = enviados.get(i);
            detalle.append(String.format("- ID %d | Habilidades: [%s] | Inglés: %s | Portafolio: %s | Perfil: %s%n",
                    i + 1, c.skills(), c.ingles(), c.tienePortafolio() ? "Sí" : "No", c.resumen()));
        }
        String respuestaIa = aiModelPort.generateAnalysis(PROMPT_RANKING.formatted(
                offer.getPracticeName(), offer.getProfession(), offer.getModality(), offer.getPracticeDescription(), detalle));
        if (esFallback(respuestaIa)) return List.of();

        Set<Long> ids = LongStream.rangeClosed(1, enviados.size()).boxed().collect(Collectors.toSet());
        return parsearLineas(respuestaIa, ids).stream()
                .map(l -> {
                    PostulationDetail p = enviados.get((int) l.id() - 1).postulacion();
                    return new CandidateRanking(p.getIdPostulation(), p.getStudentFullName(), l.match(), l.motivo());
                })
                .sorted(Comparator.comparingInt(CandidateRanking::match).reversed())
                .toList();
    }

    /** Postulado con su perfil técnico ya resuelto (perfil profesional o, en su defecto, datos de la postulación). */
    private record Candidato(PostulationDetail postulacion, String skills, String ingles, String resumen, boolean tienePortafolio) { }

    private Candidato aCandidato(PostulationDetail p) {
        StudentProfile perf = obtenerPerfilSeguro(p.getDocumentStudent());
        String github = dePerfil(perf, StudentProfile::getGithubUrl);
        return new Candidato(p,
                primerNoVacio(dePerfil(perf, StudentProfile::getSkills), p.getSkills(), "No especificadas"),
                primerNoVacio(dePerfil(perf, StudentProfile::getEnglishLevel), p.getEnglishLevel(), "B1"),
                primerNoVacio(dePerfil(perf, StudentProfile::getAboutMe), "Postulante activo de la UdeC"),
                github != null && !github.isBlank());
    }

    /** Respaldo sin IA: ordena por coincidencia de habilidades con la oferta. */
    private List<CandidateRanking> rankingLocal(List<Candidato> candidatos, String requisitos) {
        return candidatos.stream()
                .map(c -> {
                    int score = calcularMatchHabilidades(c.skills(), requisitos);
                    List<String> coinciden = habilidadesCoincidentes(c.skills(), requisitos);
                    String motivo = (coinciden.isEmpty() ? "" : "Habilidades afines: " + String.join(", ", coinciden) + ". ")
                            + (score >= 80
                            ? "Candidato altamente recomendado para entrevista por afinidad técnica con la vacante."
                            : "Perfil base compatible; se sugiere validar conocimientos específicos en entrevista.");
                    return new CandidateRanking(c.postulacion().getIdPostulation(), c.postulacion().getStudentFullName(), score, motivo);
                })
                .sorted(Comparator.comparingInt(CandidateRanking::match).reversed())
                .toList();
    }

    /** Versión en texto del ranking (campo aiRankingAnalysis) para clientes que aún no usan la lista. */
    private static String resumenRanking(Offer offer, List<CandidateRanking> ranking) {
        if (ranking.isEmpty()) {
            return "Aún no hay candidatos válidos postulados a la vacante '" + offer.getPracticeName() + "' para generar el ranking.";
        }
        StringBuilder sb = new StringBuilder("RANKING DE COMPATIBILIDAD IA — ")
                .append(offer.getPracticeName().toUpperCase()).append(System.lineSeparator()).append(System.lineSeparator());
        int pos = 1;
        for (CandidateRanking r : ranking) {
            sb.append(String.format("#%d. %s — Match: %d%%%n   %s%n", pos++, r.studentFullName(), r.match(), r.reason()));
        }
        return sb.toString().trim();
    }

    private StudentProfile obtenerPerfilSeguro(String doc) {
        if (doc == null || doc.isBlank()) return null;
        try {
            return studentProfileRepositoryPort.findByDocumentStudent(doc);
        } catch (RuntimeException e) {
            // Un perfil ilegible no debe impedir el análisis: se usan los valores por defecto
            return null;
        }
    }

    private static boolean esFallback(String respuestaIa) {
        return respuestaIa == null || respuestaIa.startsWith(PREFIJO_FALLBACK);
    }

    /** Valor de un campo del perfil, o null si no hay perfil. */
    private static String dePerfil(StudentProfile perfil, Function<StudentProfile, String> campo) {
        return perfil == null ? null : campo.apply(perfil);
    }

    /** Primer valor con contenido; el último argumento actúa como valor por defecto. */
    static String primerNoVacio(String... valores) {
        for (String v : valores) {
            if (v != null && !v.isBlank()) return v;
        }
        return valores[valores.length - 1];
    }

    /** Habilidades del estudiante (palabras de más de 2 letras) que aparecen en el texto de la oferta. */
    private static List<String> habilidadesCoincidentes(String skillsEstudiante, String textoOferta) {
        if (skillsEstudiante == null || skillsEstudiante.isBlank() || skillsEstudiante.contains("No especificadas")) {
            return List.of();
        }
        String textoNorm = String.valueOf(textoOferta).toLowerCase();
        return Arrays.stream(skillsEstudiante.toLowerCase().split("[,;/\\s]+"))
                .filter(t -> t.length() > 2 && textoNorm.contains(t))
                .distinct()
                .toList();
    }

    private int calcularMatchHabilidades(String skillsEstudiante, String textoOferta) {
        if (skillsEstudiante == null || skillsEstudiante.isBlank() || skillsEstudiante.contains("No especificadas")) {
            return 72;
        }
        String textoNorm = String.valueOf(textoOferta).toLowerCase();
        int coincidencias = habilidadesCoincidentes(skillsEstudiante, textoOferta).size();
        if (textoNorm.contains("sistemas") || textoNorm.contains("software") || textoNorm.contains("frontend") || textoNorm.contains("backend")) {
            coincidencias += 2;
        }
        return Math.min(98, 76 + (coincidencias * 6));
    }
}
