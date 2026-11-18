package co.edu.ucundinamarca.backendudecprac.domain.model;

/**
 * Posición de un postulado en el ranking de una oferta: identifica la postulación (para que el
 * cliente pueda enlazar a la tarjeta del candidato), su compatibilidad (0–100) y el motivo.
 */
public record CandidateRanking(Long idPostulation, String studentFullName, int match, String reason) {

    public CandidateRanking {
        match = Math.max(0, Math.min(100, match));
        reason = reason == null ? "" : reason.trim();
    }
}
