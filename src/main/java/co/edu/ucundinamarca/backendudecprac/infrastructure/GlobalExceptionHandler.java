package co.edu.ucundinamarca.backendudecprac.infrastructure;

import co.edu.ucundinamarca.backendudecprac.domain.exceptions.BusinessRuleException;
import co.edu.ucundinamarca.backendudecprac.domain.exceptions.InvalidCredentialsException;
import co.edu.ucundinamarca.backendudecprac.domain.exceptions.InvalidInputException;
import co.edu.ucundinamarca.backendudecprac.domain.exceptions.InvalidStateTransitionException;
import co.edu.ucundinamarca.backendudecprac.domain.exceptions.ResourceAlreadyExistsException;
import co.edu.ucundinamarca.backendudecprac.domain.exceptions.ResourceNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.nio.charset.StandardCharsets;

/**
 * Adaptador web que traduce las excepciones del dominio a códigos HTTP, en un solo lugar.
 * Gracias a esto los controladores no necesitan try/catch.
 *
 *   ResourceNotFoundException      -> 404
 *   InvalidInputException          -> 400
 *   InvalidCredentialsException    -> 401
 *   BusinessRuleException          -> 409
 *   InvalidStateTransitionException -> 409
 *   ResourceAlreadyExistsException -> 409
 *   Cualquier otra                 -> 500 (se registra en el log, sin exponer detalles al cliente)
 *
 * El cuerpo es el mensaje en texto plano porque el frontend lo muestra tal cual (err.error).
 * Los errores propios de Spring MVC (JSON mal formado, método no permitido...) los resuelve
 * ResponseEntityExceptionHandler con su código correcto en vez de convertirlos en 500.
 */
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    private static final MediaType TEXT_UTF8 = new MediaType("text", "plain", StandardCharsets.UTF_8);

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<String> handleNotFound(ResourceNotFoundException ex) {
        return message(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(InvalidInputException.class)
    public ResponseEntity<String> handleInvalidInput(InvalidInputException ex) {
        return message(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<String> handleInvalidCredentials(InvalidCredentialsException ex) {
        return message(HttpStatus.UNAUTHORIZED, ex.getMessage());
    }

    @ExceptionHandler({BusinessRuleException.class, InvalidStateTransitionException.class, ResourceAlreadyExistsException.class})
    public ResponseEntity<String> handleConflict(RuntimeException ex) {
        return message(HttpStatus.CONFLICT, ex.getMessage());
    }

    /**
     * Red de seguridad ante concurrencia: si dos peticiones simultáneas pasan la validación previa
     * y la BD rechaza la segunda por una restricción única (correo, NIT...), es un conflicto (409),
     * no un error del servidor (500).
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<String> handleDataIntegrity(DataIntegrityViolationException ex, HttpServletRequest request) {
        log.warn("Conflicto de integridad en {} {}: {}", request.getMethod(), request.getRequestURI(),
                ex.getMostSpecificCause().getMessage());
        return message(HttpStatus.CONFLICT, "El registro ya existe o entra en conflicto con otro dato registrado.");
    }

    /** Compatibilidad con código que aún lanza excepciones genéricas de Java (p. ej. el módulo de IA). */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgument(IllegalArgumentException ex) {
        return message(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<String> handleIllegalState(IllegalStateException ex) {
        return message(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleUnexpected(Exception ex, HttpServletRequest request) {
        log.error("Error inesperado en {} {}", request.getMethod(), request.getRequestURI(), ex);
        return message(HttpStatus.INTERNAL_SERVER_ERROR, "Ocurrió un error inesperado en el servidor.");
    }

    private ResponseEntity<String> message(HttpStatus status, String message) {
        return ResponseEntity.status(status).contentType(TEXT_UTF8).body(message);
    }
}
