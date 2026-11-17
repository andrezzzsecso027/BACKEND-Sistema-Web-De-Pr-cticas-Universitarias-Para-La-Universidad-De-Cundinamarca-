package co.edu.ucundinamarca.backendudecprac.infrastructure.adapter.out.external.email;

/**
 * El proveedor de correo (Brevo) no pudo entregar el mensaje. Es una falla de infraestructura:
 * los casos de uso que notifican la tratan como "best effort" y no revierten la operación.
 */
public class EmailDeliveryException extends RuntimeException {
    public EmailDeliveryException(String message, Throwable cause) {
        super(message, cause);
    }
}
