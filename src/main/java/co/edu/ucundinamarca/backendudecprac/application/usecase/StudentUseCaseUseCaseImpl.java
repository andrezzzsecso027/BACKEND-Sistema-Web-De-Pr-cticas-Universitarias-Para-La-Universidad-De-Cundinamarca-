package co.edu.ucundinamarca.backendudecprac.application.usecase;

import co.edu.ucundinamarca.backendudecprac.domain.ResourceAlreadyExistsException;
import co.edu.ucundinamarca.backendudecprac.domain.model.Student;
import co.edu.ucundinamarca.backendudecprac.domain.model.User;
import co.edu.ucundinamarca.backendudecprac.domain.port.in.createStudentUseCase;
import co.edu.ucundinamarca.backendudecprac.domain.port.out.StudentRepositoryPort;
import co.edu.ucundinamarca.backendudecprac.domain.port.out.UserRepositoryPort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class StudentUseCaseUseCaseImpl implements createStudentUseCase {

    private final StudentRepositoryPort studentRepositoryPort;
    private final UserRepositoryPort usuariorepositoryport;
    private final PasswordEncoder passwordEncoder;
    public StudentUseCaseUseCaseImpl(StudentRepositoryPort studentRepositoryPort, UserRepositoryPort usuariorepositoryport, PasswordEncoder passwordEncoder) {
        this.studentRepositoryPort = studentRepositoryPort;
        this.usuariorepositoryport = usuariorepositoryport;
        this.passwordEncoder = passwordEncoder;
    }


    @Override
    public Student createStudent(User user, Student student) {
        if(usuariorepositoryport.existsByCorreoElectronico(user.getEmailAddres())) {
            throw new ResourceAlreadyExistsException("Ya existe una cuenta con ese correo");
        }
        if(studentRepositoryPort.existsByDocumento(student.getDocument())){
            throw new ResourceAlreadyExistsException("Ya existe una cuenta con ese documento");
        }
        String encryptedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encryptedPassword);
        User userGuardado = usuariorepositoryport.saveUser(user);
        student.setIdUser(userGuardado.getIdUser());
        return studentRepositoryPort.saveStudent(student);
    }

    @Override
    public List<Student> FindAllStudents() {
        return studentRepositoryPort.findAllStudents();
    }

}