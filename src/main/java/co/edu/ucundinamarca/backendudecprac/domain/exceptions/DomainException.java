package co.edu.ucundinamarca.backendudecprac.domain.exceptions;

/**
 * Base de todas las excepciones de negocio. El dominio solo expresa QUÉ salió mal;
 * el adaptador web (GlobalExceptionHandler) decide el código HTTP.
 */
public abstract class DomainException extends RuntimeException {
    protected DomainException(String message) {
        super(message);
    }
}
