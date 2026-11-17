package co.edu.ucundinamarca.backendudecprac.application.usecase;

import co.edu.ucundinamarca.backendudecprac.domain.exceptions.InvalidInputException;
import co.edu.ucundinamarca.backendudecprac.domain.exceptions.ResourceAlreadyExistsException;
import co.edu.ucundinamarca.backendudecprac.domain.model.Student;
import co.edu.ucundinamarca.backendudecprac.domain.model.User;
import co.edu.ucundinamarca.backendudecprac.domain.port.in.CreateStudentUseCase;
import co.edu.ucundinamarca.backendudecprac.domain.port.out.EmailSenderPort;
import co.edu.ucundinamarca.backendudecprac.domain.port.out.StudentRepositoryPort;
import co.edu.ucundinamarca.backendudecprac.domain.port.out.UserRepositoryPort;
import co.edu.ucundinamarca.backendudecprac.domain.port.out.PasswordEncoderPort;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StudentUseCaseUseCaseImpl implements CreateStudentUseCase {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(StudentUseCaseUseCaseImpl.class);
    private static final java.util.regex.Pattern CORREO_INSTITUCIONAL =
            java.util.regex.Pattern.compile("^[a-z0-9._%+-]+@ucundinamarca\\.edu\\.co$");

    private final StudentRepositoryPort studentRepositoryPort;
    private final UserRepositoryPort usuariorepositoryport;
    private final PasswordEncoderPort passwordEncoder;
    private final EmailSenderPort emailSenderPort;

    public StudentUseCaseUseCaseImpl(StudentRepositoryPort studentRepositoryPort, UserRepositoryPort usuariorepositoryport, PasswordEncoderPort passwordEncoder, EmailSenderPort emailSenderPort) {
        this.studentRepositoryPort = studentRepositoryPort;
        this.usuariorepositoryport = usuariorepositoryport;
        this.passwordEncoder = passwordEncoder;
        this.emailSenderPort = emailSenderPort;
    }

    @Transactional
    @Override
    public Student createStudent(User user, Student student) {

        // Se normaliza (sin espacios, minúsculas) y se exige un usuario válido antes del dominio institucional:
        // antes "@ucundinamarca.edu.co" a secas o "Ana@UCUNDINAMARCA.EDU.CO" daban resultados inconsistentes.
        String correo = user.getEmailAddres() == null ? "" : user.getEmailAddres().trim().toLowerCase(java.util.Locale.ROOT);
        if (!CORREO_INSTITUCIONAL.matcher(correo).matches()) {
            throw new InvalidInputException("Solo se permiten correos institucionales (@ucundinamarca.edu.co)");
        }
        user.setEmailAddres(correo);

        if(usuariorepositoryport.existsByCorreoElectronico(user.getEmailAddres())) {
            throw new ResourceAlreadyExistsException("Ya existe una cuenta con ese correo");
        }
        if(studentRepositoryPort.existsByDocumento(student.getDocument())){
            throw new ResourceAlreadyExistsException("Ya existe una cuenta con ese documento");
        }
        SecureRandom random = new SecureRandom();
        String codigoLimpio = String.valueOf(100000 + random.nextInt(900000));


        user.setVerificationCode(passwordEncoder.encode(codigoLimpio)); // Código encriptado
        user.setCodeExpiration(LocalDateTime.now().plusMinutes(15));
        user.setUserStatus(false);
        user.setPassword(passwordEncoder.encode(user.getPassword())); // Contraseña encriptada
        user.setCreationDate(LocalDateTime.now());
        // 5. Guardar en Base de Datos (tu flujo original)
        User userGuardado = usuariorepositoryport.saveUser(user);
        student.setIdUser(userGuardado.getIdUser());
        Student estudianteGuardado = studentRepositoryPort.saveStudent(student);

        // Se envía el código en claro por correo; en BD solo queda el hash
        emailSenderPort.sendEmail(correo, codigoLimpio);

        // Solo visible con logging.level.co.edu.ucundinamarca.backendudecprac=DEBUG (útil en desarrollo
        // si se agota la cuota de Brevo). Con el nivel INFO por defecto el código NO aparece en los logs.
        log.debug("Código de verificación generado para {}: {}", correo, codigoLimpio);

        return estudianteGuardado;
    }

}