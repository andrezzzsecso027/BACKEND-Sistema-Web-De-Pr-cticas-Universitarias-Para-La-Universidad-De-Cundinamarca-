package co.edu.ucundinamarca.backendudecprac.application.usecase;

import co.edu.ucundinamarca.backendudecprac.domain.model.User;
import co.edu.ucundinamarca.backendudecprac.domain.port.in.FindUserUseCase;
import co.edu.ucundinamarca.backendudecprac.domain.port.out.UserRepositoryPort;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class FindUserUseCaseImpl implements FindUserUseCase {

    private final UserRepositoryPort userRepositoryPort;

    public FindUserUseCaseImpl(UserRepositoryPort userRepositoryPort) {
        this.userRepositoryPort = userRepositoryPort;
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepositoryPort.findByCorreoElectronico(email);
    }
}
