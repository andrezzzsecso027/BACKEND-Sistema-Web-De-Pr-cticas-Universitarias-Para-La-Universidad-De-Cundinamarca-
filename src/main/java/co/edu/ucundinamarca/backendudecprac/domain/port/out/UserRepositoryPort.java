package co.edu.ucundinamarca.backendudecprac.domain.port.out;

import co.edu.ucundinamarca.backendudecprac.domain.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepositoryPort {
    User saveUser(User user);
    List<User> findAllUsuarios();
    boolean existsByCorreoElectronico(String correoElectronico);

    Optional<User> findById(long idUser);
}
