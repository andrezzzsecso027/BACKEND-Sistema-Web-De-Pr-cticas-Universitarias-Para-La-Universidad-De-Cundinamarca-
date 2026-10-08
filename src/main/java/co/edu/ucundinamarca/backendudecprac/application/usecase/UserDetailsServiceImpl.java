package co.edu.ucundinamarca.backendudecprac.application.usecase;

import co.edu.ucundinamarca.backendudecprac.infrastructure.adapter.out.persistence.UserEntity;
import co.edu.ucundinamarca.backendudecprac.infrastructure.adapter.out.persistence.UserJPArepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserJPArepository userjparepository;

    public UserDetailsServiceImpl(UserJPArepository userjparepository) {
        this.userjparepository = userjparepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity userEntity = userjparepository.findByCorreoElectronico(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        return org.springframework.security.core.userdetails.User.builder()
                .username(userEntity.getCorreoElectronico())
                .password(userEntity.getContrasenia())
                .roles(userEntity.getRolUsuario())
                // La validación que frena a las empresas no verificadas
                .disabled(!userEntity.isEstado())
                .build();
    }
}
