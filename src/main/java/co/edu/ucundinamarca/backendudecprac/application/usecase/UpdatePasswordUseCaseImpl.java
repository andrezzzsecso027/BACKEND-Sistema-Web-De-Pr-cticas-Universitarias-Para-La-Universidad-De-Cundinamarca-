package co.edu.ucundinamarca.backendudecprac.application.usecase;


import co.edu.ucundinamarca.backendudecprac.domain.exceptions.InvalidCredentialsException;
import co.edu.ucundinamarca.backendudecprac.domain.exceptions.ResourceNotFoundException;
import co.edu.ucundinamarca.backendudecprac.domain.exceptions.InvalidInputException;
import co.edu.ucundinamarca.backendudecprac.domain.model.User;
import co.edu.ucundinamarca.backendudecprac.domain.port.in.UpdatePasswordUseCase;
import co.edu.ucundinamarca.backendudecprac.domain.port.out.UserRepositoryPort;
import co.edu.ucundinamarca.backendudecprac.domain.port.out.PasswordEncoderPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UpdatePasswordUseCaseImpl implements UpdatePasswordUseCase {
    private final UserRepositoryPort userRepositoryPort;
    private final PasswordEncoderPort passwordEncoder;

    public UpdatePasswordUseCaseImpl(UserRepositoryPort userRepositoryPort, PasswordEncoderPort passwordEncoder) {
        this.userRepositoryPort = userRepositoryPort;
        this.passwordEncoder = passwordEncoder;
    }
    @Transactional
    @Override
    public void updatePassword(String email, String currentPassword, String newPassword) {
        if (newPassword == null || newPassword.trim().length() < 6) {
            throw new InvalidInputException("La nueva contraseña debe tener al menos 6 caracteres.");
        }

        User user = userRepositoryPort.findByCorreoElectronico(email)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró un usuario con el correo: " + email));

        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new InvalidCredentialsException("La contraseña actual ingresada es incorrecta.");
        }

        if (passwordEncoder.matches(newPassword, user.getPassword())) {
            throw new InvalidInputException("La nueva contraseña no puede ser igual a la contraseña actual.");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepositoryPort.saveUser(user);
    }
}
