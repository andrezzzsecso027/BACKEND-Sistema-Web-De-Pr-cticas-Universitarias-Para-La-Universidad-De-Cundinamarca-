package co.edu.ucundinamarca.backendudecprac.domain.model;

/**
 * Una oferta recomendada a un estudiante: identifica la oferta (para que el cliente pueda
 * enlazarla), su porcentaje de compatibilidad (0–100) y el motivo en lenguaje natural.
 */
public record OfferRecommendation(Long idOffer, String practiceName, int match, String reason) {

    public OfferRecommendation {
        match = Math.max(0, Math.min(100, match));
        reason = reason == null ? "" : reason.trim();
    }
}
