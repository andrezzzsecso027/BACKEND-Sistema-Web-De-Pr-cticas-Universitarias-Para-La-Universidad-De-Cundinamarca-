package co.edu.ucundinamarca.backendudecprac.domain.port.in;

import co.edu.ucundinamarca.backendudecprac.domain.model.User;

import java.util.Optional;

public interface FindUserUseCase {
    Optional<User> findByEmail(String email);
}
