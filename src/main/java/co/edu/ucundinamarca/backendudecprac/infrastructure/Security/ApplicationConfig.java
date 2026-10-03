package co.edu.ucundinamarca.backendudecprac.infrastructure.Security;

import co.edu.ucundinamarca.backendudecprac.domain.model.User;
import co.edu.ucundinamarca.backendudecprac.infrastructure.adapter.out.persistence.UserJPArepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class ApplicationConfig {
    private final UserJPArepository userRepository;

    public ApplicationConfig(UserJPArepository userRepository) {
        this.userRepository = userRepository;
    }

    // 1. Le enseñamos a Spring cómo buscar un usuario en tu base de datos
    @Bean
    public UserDetailsService userDetailsService() {
        return username -> {
            // Buscamos el Entity en PostgreSQL
            var entity = userRepository.findByCorreoElectronico(username)
                    .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado en la BD"));

            // Transformamos el Entity de Infraestructura al User puro de tu Dominio
            User userDomain = new User();
            userDomain.setEmailAddres(entity.getCorreoElectronico());
            userDomain.setPassword(entity.getContrasenia());
            userDomain.setUserRol(entity.getRolUsuario());

            // Lo envolvemos en el adaptador hexagonal que creaste
            return new UserDetailAdapter(userDomain);
        };
    }

    // 2. Le decimos a Spring que use BCrypt para encriptar/desencriptar contraseñas
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 3. Unimos el buscador de usuarios y el encriptador de contraseñas
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    // 4. Creamos el AuthenticationManager que tu AuthController estaba pidiendo a gritos
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
