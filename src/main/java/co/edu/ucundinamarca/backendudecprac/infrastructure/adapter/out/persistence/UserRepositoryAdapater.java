package co.edu.ucundinamarca.backendudecprac.infrastructure.adapter.out.persistence;

import co.edu.ucundinamarca.backendudecprac.domain.model.User;
import co.edu.ucundinamarca.backendudecprac.domain.port.out.UserRepositoryPort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

import static org.springframework.data.jpa.domain.AbstractPersistable_.id;

@Component
public class UserRepositoryAdapater implements UserRepositoryPort {

    private final UserJPArepository usuariojparepository;


    public UserRepositoryAdapater(UserJPArepository usuariojparepository, StudentJpaRepository jpaRepository, JPACompanyRepository jpaCompanyRepository) {
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

        UserEntity guardado = usuariojparepository.save(entity);
        user.setIdUser(guardado.getIdUsario());
        return user;
    }
    @Override
    public boolean existsByCorreoElectronico(String correoElectronico) {
        return usuariojparepository.existsByCorreoElectronico(correoElectronico);
    }

    @Override
    public Optional<User> findById(long idUser) {
        Optional<UserEntity> entityOptional = usuariojparepository.findById(idUser);

        if (entityOptional.isPresent()) {
            UserEntity entity = entityOptional.get();
            User user = new User();
            // Mapea los datos del entity al dominio
            user.setIdUser(entity.getIdUsario());
            user.setEmailAddres(entity.getCorreoElectronico());
            user.setPassword(entity.getContrasenia());
            user.setUserRol(entity.getRolUsuario());
            user.setUserStatus(entity.isEstado()); // Asegúrate de tener este campo mapeado
            return Optional.of(user);
        }

        return Optional.empty();
    }

    @Override
    public List<User> findAllUsuarios() {
        return List.of();
    }
}
