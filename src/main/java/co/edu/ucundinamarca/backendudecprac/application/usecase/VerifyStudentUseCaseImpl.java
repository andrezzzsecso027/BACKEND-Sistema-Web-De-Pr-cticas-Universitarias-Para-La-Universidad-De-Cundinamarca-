package co.edu.ucundinamarca.backendudecprac.application.usecase;

import co.edu.ucundinamarca.backendudecprac.domain.model.User;
import co.edu.ucundinamarca.backendudecprac.domain.port.in.VerifyStudentUseCase;
import co.edu.ucundinamarca.backendudecprac.domain.port.out.UserRepositoryPort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
@Service
public class VerifyStudentUseCaseImpl implements VerifyStudentUseCase {

    private final UserRepositoryPort userRepositoryPort;
    private final PasswordEncoder passwordEncoder;


    public VerifyStudentUseCaseImpl(UserRepositoryPort userRepositoryPort, PasswordEncoder passwordEncoder) {
        this.userRepositoryPort = userRepositoryPort;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    @Override
    public void verifyCode(String email, String code) {
        User user = userRepositoryPort.findByCorreoElectronico(email)
                .orElseThrow(() -> new IllegalArgumentException("No se encontró ningún usuario con ese correo."));
        if (user.isUserStatus()) {
            throw new IllegalStateException("La cuenta ya se encuentra verificada.");
        }
        if (user.getCodeExpiration() == null || user.getCodeExpiration().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("El código de verificación ha expirado. Debes solicitar uno nuevo.");
        }
        if (!passwordEncoder.matches(code, user.getVerificationCode())) {
            throw new IllegalArgumentException("El código de verificación es incorrecto.");
        }
        user.setUserStatus(true);
        user.setVerificationCode(null);
        user.setCodeExpiration(null);
        userRepositoryPort.saveUser(user);
    }
}
