package co.edu.ucundinamarca.backendudecprac.domain.exceptions;

/** Datos de entrada inválidos (campo obligatorio vacío, estado desconocido, fecha en el pasado...). HTTP 400. */
public class InvalidInputException extends DomainException {
    public InvalidInputException(String message) {
        super(message);
    }
}
