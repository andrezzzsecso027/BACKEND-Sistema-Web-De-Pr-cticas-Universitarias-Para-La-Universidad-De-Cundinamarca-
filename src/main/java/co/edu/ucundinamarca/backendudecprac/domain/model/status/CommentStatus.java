package co.edu.ucundinamarca.backendudecprac.domain.model.status;

/**
 * Moderación de comentarios: PENDIENTE ──► APROBADO | RECHAZADO (ambos finales).
 */
public enum CommentStatus {
    PENDIENTE,
    APROBADO,
    RECHAZADO;

    public boolean canTransitionTo(CommentStatus target) {
        return this == PENDIENTE && (target == APROBADO || target == RECHAZADO);
    }

    public static CommentStatus from(String value) {
        return StatusParser.parse(CommentStatus.class, StatusParser.normalize(value), value, "el comentario");
    }
}
