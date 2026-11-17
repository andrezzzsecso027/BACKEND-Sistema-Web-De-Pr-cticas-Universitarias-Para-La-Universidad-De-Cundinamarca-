package co.edu.ucundinamarca.backendudecprac.infrastructure;

import co.edu.ucundinamarca.backendudecprac.domain.exceptions.*;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/** Cada excepción del dominio se traduce a su código HTTP con el mensaje en texto plano. */
class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    private static HttpServletRequest peticion() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getMethod()).thenReturn("POST");
        when(request.getRequestURI()).thenReturn("/api/registro/empresas");
        return request;
    }

    private static void assertRespuesta(HttpStatus esperado, String mensaje, ResponseEntity<String> r) {
        assertEquals(esperado, r.getStatusCode());
        assertEquals(mensaje, r.getBody());
        assertTrue(r.getHeaders().getContentType().toString().startsWith("text/plain"));
    }

    @Test
    void excepcionesDelDominioSeTraducenASuCodigoHttp() {
        assertRespuesta(HttpStatus.NOT_FOUND, "no existe", handler.handleNotFound(new ResourceNotFoundException("no existe")));
        assertRespuesta(HttpStatus.BAD_REQUEST, "dato inválido", handler.handleInvalidInput(new InvalidInputException("dato inválido")));
        assertRespuesta(HttpStatus.UNAUTHORIZED, "clave incorrecta",
                handler.handleInvalidCredentials(new InvalidCredentialsException("clave incorrecta")));
        assertRespuesta(HttpStatus.CONFLICT, "sin cupos", handler.handleConflict(new BusinessRuleException("sin cupos")));
        assertRespuesta(HttpStatus.CONFLICT, "duplicado", handler.handleConflict(new ResourceAlreadyExistsException("duplicado")));
        assertRespuesta(HttpStatus.CONFLICT, "No se puede cambiar el estado de la oferta de A a B.",
                handler.handleConflict(new InvalidStateTransitionException("la oferta", "A", "B")));
    }

    @Test
    void excepcionesGenericasDeJavaTambienTienenSuCodigo() {
        assertRespuesta(HttpStatus.BAD_REQUEST, "argumento", handler.handleIllegalArgument(new IllegalArgumentException("argumento")));
        assertRespuesta(HttpStatus.CONFLICT, "estado", handler.handleIllegalState(new IllegalStateException("estado")));
    }

    @Test
    void violacionDeRestriccionUnicaEsConflictoNoErrorDelServidor() {
        DataIntegrityViolationException ex = new DataIntegrityViolationException("dup",
                new SQLException("duplicate key value violates unique constraint \"empresas_pkey\""));

        ResponseEntity<String> r = handler.handleDataIntegrity(ex, peticion());

        assertEquals(HttpStatus.CONFLICT, r.getStatusCode());
        assertTrue(r.getBody().contains("ya existe"));
    }

    @Test
    void errorInesperadoEs500SinExponerDetalles() {
        ResponseEntity<String> r = handler.handleUnexpected(new NullPointerException("detalle interno"), peticion());

        assertRespuesta(HttpStatus.INTERNAL_SERVER_ERROR, "Ocurrió un error inesperado en el servidor.", r);
    }
}
