package co.edu.ucundinamarca.backendudecprac.domain.exceptions;

/** Cambio de estado no permitido por el ciclo de vida (p. ej. RECHAZADA -> ACEPTADA). HTTP 409. */
public class InvalidStateTransitionException extends BusinessRuleException {
    public InvalidStateTransitionException(String entity, Object from, Object to) {
        super("No se puede cambiar el estado de " + entity + " de " + from + " a " + to + ".");
    }
}
