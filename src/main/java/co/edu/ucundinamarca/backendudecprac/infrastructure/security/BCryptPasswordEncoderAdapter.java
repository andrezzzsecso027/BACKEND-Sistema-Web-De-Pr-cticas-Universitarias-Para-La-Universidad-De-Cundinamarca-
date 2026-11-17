package co.edu.ucundinamarca.backendudecprac.infrastructure.security;

import co.edu.ucundinamarca.backendudecprac.domain.port.out.PasswordEncoderPort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Adaptador de salida: implementa el puerto del dominio usando el BCrypt de Spring Security.
 */
@Component
public class BCryptPasswordEncoderAdapter implements PasswordEncoderPort {

    private final PasswordEncoder passwordEncoder;

    public BCryptPasswordEncoderAdapter(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public String encode(String rawValue) {
        return passwordEncoder.encode(rawValue);
    }

    @Override
    public boolean matches(String rawValue, String encodedValue) {
        return passwordEncoder.matches(rawValue, encodedValue);
    }
}
