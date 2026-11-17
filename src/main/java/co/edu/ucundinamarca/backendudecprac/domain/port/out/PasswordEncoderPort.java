package co.edu.ucundinamarca.backendudecprac.domain.port.out;

/**
 * Puerto de salida para cifrar y verificar contraseñas/códigos.
 * Evita que la capa de aplicación dependa de Spring Security (implementado con BCrypt en infraestructura).
 */
public interface PasswordEncoderPort {
    String encode(String rawValue);

    boolean matches(String rawValue, String encodedValue);
}
