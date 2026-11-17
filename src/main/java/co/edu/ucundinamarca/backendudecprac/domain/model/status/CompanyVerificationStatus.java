package co.edu.ucundinamarca.backendudecprac.domain.model.status;

/**
 * Verificación de una empresa por la Universidad:
 *
 *   PENDIENTE ──► APROBADA ◄──► RECHAZADA (inhabilitada)
 *            └──► RECHAZADA
 *
 * El administrador puede aprobar, rechazar o inhabilitar; lo único prohibido es repetir el mismo estado
 * o volver a PENDIENTE.
 */
public enum CompanyVerificationStatus {
    PENDIENTE,
    APROBADA,
    RECHAZADA;

    public boolean canTransitionTo(CompanyVerificationStatus target) {
        return target != PENDIENTE && target != this;
    }

    public static CompanyVerificationStatus from(String value) {
        return StatusParser.parse(CompanyVerificationStatus.class, StatusParser.normalize(value), value, "la empresa");
    }
}
