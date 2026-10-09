package co.edu.ucundinamarca.backendudecprac.application.usecase;

import co.edu.ucundinamarca.backendudecprac.domain.ResourceAlreadyExistsException;
import co.edu.ucundinamarca.backendudecprac.domain.model.Student;
import co.edu.ucundinamarca.backendudecprac.domain.model.User;
import co.edu.ucundinamarca.backendudecprac.domain.port.in.createStudentUseCase;
import co.edu.ucundinamarca.backendudecprac.domain.port.out.EmailRepositoryPort;
import co.edu.ucundinamarca.backendudecprac.domain.port.out.StudentRepositoryPort;
import co.edu.ucundinamarca.backendudecprac.domain.port.out.UserRepositoryPort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StudentUseCaseUseCaseImpl implements createStudentUseCase {

    private final StudentRepositoryPort studentRepositoryPort;
    private final UserRepositoryPort usuariorepositoryport;
    private final PasswordEncoder passwordEncoder;
    private final EmailRepositoryPort emailRepositoryPort;

    public StudentUseCaseUseCaseImpl(StudentRepositoryPort studentRepositoryPort, UserRepositoryPort usuariorepositoryport, PasswordEncoder passwordEncoder, EmailRepositoryPort emailRepositoryPort) {
        this.studentRepositoryPort = studentRepositoryPort;
        this.usuariorepositoryport = usuariorepositoryport;
        this.passwordEncoder = passwordEncoder;
        this.emailRepositoryPort = emailRepositoryPort;
    }

    @Transactional
    @Override
    public Student createStudent(User user, Student student) {

        String correo = user.getEmailAddres();

        if (correo == null || !correo.endsWith("@ucundinamarca.edu.co")) {
            throw new IllegalArgumentException("Solo se permiten correos institucionales (@ucundinamarca.edu.co)");
        }

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

        // 5. Guardar en Base de Datos (tu flujo original)
        User userGuardado = usuariorepositoryport.saveUser(user);
        student.setIdUser(userGuardado.getIdUser());
        Student estudianteGuardado = studentRepositoryPort.saveStudent(student);

        // 6. Disparar el envío del correo con el código limpio
        // (Asegúrate de que el nombre del método coincida con el que creaste en EmailRepositoryPort)
        emailRepositoryPort.sendEmailverification(correo, codigoLimpio);

        // 7. Truco de desarrollador: Imprimir en consola para probar sin gastar los correos de Resend
        System.out.println("=================================================");
        System.out.println("NUEVO REGISTRO: " + correo);
        System.out.println("CÓDIGO GENERADO (COPIA ESTO PARA PROBAR): " + codigoLimpio);
        System.out.println("=================================================");

        return estudianteGuardado;


    }
    @Override
    public List<Student> FindAllStudents() {
        return studentRepositoryPort.findAllStudents();
    }

}