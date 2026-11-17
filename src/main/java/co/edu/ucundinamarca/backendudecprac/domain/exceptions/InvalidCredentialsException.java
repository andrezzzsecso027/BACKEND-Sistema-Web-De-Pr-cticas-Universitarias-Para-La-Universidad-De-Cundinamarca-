package co.edu.ucundinamarca.backendudecprac.domain.exceptions;

/** La contraseña actual no coincide. HTTP 401. */
public class InvalidCredentialsException extends DomainException {
    public InvalidCredentialsException(String message) {
        super(message);
    }
}
