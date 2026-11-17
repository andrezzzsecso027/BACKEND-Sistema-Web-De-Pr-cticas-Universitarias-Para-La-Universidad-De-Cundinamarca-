package co.edu.ucundinamarca.backendudecprac.infrastructure.security;

import co.edu.ucundinamarca.backendudecprac.domain.port.out.UserRepositoryPort;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Adaptador de Spring Security: carga el usuario a través del puerto de salida del dominio
 * (UserRepositoryPort), sin depender de JPA directamente.
 */
@Service
public class UserDetailsServiceAdapter implements UserDetailsService {

    private final UserRepositoryPort userRepositoryPort;

    public UserDetailsServiceAdapter(UserRepositoryPort userRepositoryPort) {
        this.userRepositoryPort = userRepositoryPort;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepositoryPort.findByCorreoElectronico(email)
                .map(UserDetailAdapter::new)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
    }
}
