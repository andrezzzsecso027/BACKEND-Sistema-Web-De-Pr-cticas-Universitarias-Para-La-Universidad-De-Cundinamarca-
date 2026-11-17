package co.edu.ucundinamarca.backendudecprac.domain.model.status;

/**
 * Ciclo de vida de una postulación:
 *
 *   POSTULADA (en revisión) ──► ACEPTADA   (crea la práctica)
 *                          └──► RECHAZADA
 *
 * ACEPTADA y RECHAZADA son estados finales. Se conserva el valor "POSTULADA" porque
 * es el que ya está guardado en la BD y el que usa el frontend para mostrar los botones.
 */
public enum PostulationStatus {
    POSTULADA,
    ACEPTADA,
    RECHAZADA;

    public boolean canTransitionTo(PostulationStatus target) {
        return this == POSTULADA && (target == ACEPTADA || target == RECHAZADA);
    }

    public static PostulationStatus from(String value) {
        String v = StatusParser.normalize(value);
        if ("EN_REVISION".equals(v)) return POSTULADA; // alias del concepto "en revisión"
        return StatusParser.parse(PostulationStatus.class, v, value, "la postulación");
    }
}
