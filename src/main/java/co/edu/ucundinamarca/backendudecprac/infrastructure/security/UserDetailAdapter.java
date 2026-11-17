package co.edu.ucundinamarca.backendudecprac.infrastructure.security;

import co.edu.ucundinamarca.backendudecprac.domain.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Locale;

/**
 * Adapta el modelo de dominio User al contrato UserDetails de Spring Security.
 * El rol se normaliza: "administrador" (BD) -> "ROLE_ADMINISTRADOR" (ver Roles).
 */
public class UserDetailAdapter implements UserDetails {

    private final transient User user;

    public UserDetailAdapter(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        String rol = user.getUserRol() == null ? "" : user.getUserRol().trim().toUpperCase(Locale.ROOT);
        return List.of(new SimpleGrantedAuthority("ROLE_" + rol));
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmailAddres();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /** Empresas pendientes y estudiantes sin verificar quedan deshabilitados. */
    @Override
    public boolean isEnabled() {
        return user.isUserStatus();
    }
}
