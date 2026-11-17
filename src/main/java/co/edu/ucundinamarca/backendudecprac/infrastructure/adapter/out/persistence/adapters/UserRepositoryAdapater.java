package co.edu.ucundinamarca.backendudecprac.infrastructure.adapter.out.persistence.adapters;

import co.edu.ucundinamarca.backendudecprac.domain.model.User;
import co.edu.ucundinamarca.backendudecprac.domain.port.out.UserRepositoryPort;
import co.edu.ucundinamarca.backendudecprac.infrastructure.adapter.out.persistence.entities.UserEntity;
import co.edu.ucundinamarca.backendudecprac.infrastructure.adapter.out.persistence.jpa.UserJPArepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;


@Component
public class UserRepositoryAdapater implements UserRepositoryPort {

    private final UserJPArepository usuariojparepository;


    public UserRepositoryAdapater(UserJPArepository usuariojparepository) {
        this.usuariojparepository = usuariojparepository;
    }

    @Override
    public User saveUser(User user) {
        UserEntity entity = new UserEntity();
        if (user.getIdUser() != 0) {
            entity.setIdUsario(user.getIdUser());
        }
        entity.setCorreoElectronico(user.getEmailAddres());
        entity.setContrasenia(user.getPassword());
        entity.setRolUsuario(user.getUserRol());
        entity.setEstado(user.isUserStatus());
        entity.setCodigoVerificacion(user.getVerificationCode());
        entity.setFechaExpiracionCodigo(user.getCodeExpiration());
        entity.setFechaRegistro(user.getCreationDate());
        UserEntity guardado = usuariojparepository.save(entity);
        user.setIdUser(guardado.getIdUsario());
        return user;
    }
    @Override
    public boolean existsByCorreoElectronico(String correoElectronico) {
        return usuariojparepository.existsByCorreoElectronico(correoElectronico);
    }

    @Override
    public Optional<User> findByCorreoElectronico(String correoElectronico) {
        return usuariojparepository.findByCorreoElectronico(correoElectronico).map(this::mapToDomain);
    }

    @Override
    public Optional<User> findById(Long idUser) {
        // Antes este método no mapeaba fecha de registro ni código de verificación; como saveUser
        // reescribe la fila completa, aprobar una empresa (findById + saveUser) borraba su fecha_registro.
        return usuariojparepository.findById(idUser).map(this::mapToDomain);
    }

    private User mapToDomain(UserEntity entity) {
        User user = new User();
        user.setIdUser(entity.getIdUsario());
        user.setEmailAddres(entity.getCorreoElectronico());
        user.setPassword(entity.getContrasenia());
        user.setUserRol(entity.getRolUsuario());
        user.setUserStatus(entity.getEstado());
        user.setVerificationCode(entity.getCodigoVerificacion());
        user.setCodeExpiration(entity.getFechaExpiracionCodigo());
        user.setCreationDate(entity.getFechaRegistro());
        return user;
    }

    @Override
    public List<User> findAllUsuarios() {
        return List.of();
    }

    @Override
    public List<User> findByRole(String role) {
        return usuariojparepository.findByRolUsuarioIgnoreCase(role).stream().map(entity -> {
            User user = new User();
            user.setIdUser(entity.getIdUsario()); // o el getter de ID que tengas en UserEntity
            user.setEmailAddres(entity.getCorreoElectronico());
            user.setUserRol(entity.getRolUsuario());
            user.setUserStatus(entity.getEstado());
            return user;
        }).toList();
    }
}
