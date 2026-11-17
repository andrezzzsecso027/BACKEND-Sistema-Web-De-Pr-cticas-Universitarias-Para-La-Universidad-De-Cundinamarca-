package co.edu.ucundinamarca.backendudecprac.domain.port.out;

/**
 * Puerto de salida para enviar correos (implementado hoy con Brevo).
 * El dominio no sabe qué proveedor se usa.
 */
public interface EmailSenderPort {
    void sendEmail(String toEmail, String htmlContent);
}
