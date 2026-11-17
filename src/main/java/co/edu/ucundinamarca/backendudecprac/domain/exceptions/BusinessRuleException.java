package co.edu.ucundinamarca.backendudecprac.domain.exceptions;

/**
 * Los datos son válidos, pero una regla de negocio impide la operación
 * (oferta sin cupos, empresa no aprobada, ya postulado...). HTTP 409.
 */
public class BusinessRuleException extends DomainException {
    public BusinessRuleException(String message) {
        super(message);
    }
}
