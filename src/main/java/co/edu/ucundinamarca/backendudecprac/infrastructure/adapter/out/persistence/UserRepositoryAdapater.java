package co.edu.ucundinamarca.backendudecprac.infrastructure.adapter.out.persistence;

import co.edu.ucundinamarca.backendudecprac.domain.model.User;
import co.edu.ucundinamarca.backendudecprac.domain.port.out.UserRepositoryPort;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserRepositoryAdapater implements UserRepositoryPort {

    private final UserJPArepository usuariojparepository;

    public UserRepositoryAdapater(UserJPArepository usuariojparepository, StudentJpaRepository jpaRepository, JPACompanyRepository jpaCompanyRepository) {
        this.usuariojparepository = usuariojparepository;
    }

    @Override
    public User saveUser(User user) {
        UserEntity entity = new UserEntity();
        entity.setCorreoElectronico(user.getEmailAddres());
        entity.setContrasenia(user.getPassword());
        entity.setRolUsuario(user.getUserRol());
        entity.setEstado(user.isUserStatus());

        UserEntity guardado = usuariojparepository.save(entity);
        user.setIdUser(guardado.getIdUsario());
        return user;
    }
    @Override
    public boolean existsByCorreoElectronico(String correoElectronico) {
        return usuariojparepository.existsByCorreoElectronico(correoElectronico);
    }
    @Override
    public List<User> findAllUsuarios() {
        return List.of();
    }
}
