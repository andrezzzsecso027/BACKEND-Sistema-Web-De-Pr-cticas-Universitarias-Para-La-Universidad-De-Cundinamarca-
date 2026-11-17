package co.edu.ucundinamarca.backendudecprac.domain.exceptions;

public class ResourceAlreadyExistsException extends DomainException {
    public ResourceAlreadyExistsException(String message) {
        super(message);
    }
}
