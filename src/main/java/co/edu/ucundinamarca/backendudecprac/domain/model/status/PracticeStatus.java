package co.edu.ucundinamarca.backendudecprac.domain.model.status;

/**
 * Ciclo de vida de una práctica: EN_CURSO ──► FINALIZADA (estado final).
 * Las prácticas antiguas guardadas como "ASIGNADA" se leen como EN_CURSO.
 */
public enum PracticeStatus {
    EN_CURSO,
    FINALIZADA;

    public boolean canTransitionTo(PracticeStatus target) {
        return this == EN_CURSO && target == FINALIZADA;
    }

    public static PracticeStatus from(String value) {
        String v = StatusParser.normalize(value);
        if ("ASIGNADA".equals(v)) return EN_CURSO; // valor histórico en la BD
        return StatusParser.parse(PracticeStatus.class, v, value, "la práctica");
    }
}
