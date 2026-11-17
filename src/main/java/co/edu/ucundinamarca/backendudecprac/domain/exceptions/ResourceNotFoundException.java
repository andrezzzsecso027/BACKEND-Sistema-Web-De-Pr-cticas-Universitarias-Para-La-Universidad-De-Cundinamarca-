package co.edu.ucundinamarca.backendudecprac.domain.exceptions;

/** El recurso solicitado no existe (oferta, postulación, práctica, empresa...). HTTP 404. */
public class ResourceNotFoundException extends DomainException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
