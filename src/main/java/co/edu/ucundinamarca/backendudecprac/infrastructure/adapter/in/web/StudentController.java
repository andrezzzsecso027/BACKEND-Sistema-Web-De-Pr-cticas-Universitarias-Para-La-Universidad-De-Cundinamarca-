package co.edu.ucundinamarca.backendudecprac.infrastructure.adapter.in.web;

import co.edu.ucundinamarca.backendudecprac.application.usecase.StudentUseCaseUseCaseImpl;
import co.edu.ucundinamarca.backendudecprac.domain.model.Student;
import co.edu.ucundinamarca.backendudecprac.domain.model.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/registro/estudiantes")
@CrossOrigin(origins = "http://localhost:4200")
public class StudentController {

    private final StudentUseCaseUseCaseImpl useCase;

    public StudentController(StudentUseCaseUseCaseImpl useCase) {
        this.useCase = useCase;
    }


    @GetMapping
    public ResponseEntity<List<Student>> listar() {
        return ResponseEntity.ok(useCase.FindAllStudents());
    }


    @PostMapping
    public ResponseEntity<Student> crear(@RequestBody StudentRecordRequest request) {
        User user = new User();
        user.setEmailAddres(request.getCorreo());
        user.setPassword(request.getContrasenia());
        user.setUserRol("estudiante");
        Student student = new Student();
        student.setName(request.getNombreEstudiante());
        student.setLastName(request.getApellidoEstudiante());
        student.setDocument(request.getDocumento());
        student.setPhoneNumber(request.getTelefono());
        student.setAddres(request.getDireccion());
        student.setHeadquarters(request.getSede());
        student.setAcademicProgram(request.getProgramaAcademico());

        Student studentCreado = useCase.createStudent(user, student);
        return ResponseEntity.status(HttpStatus.CREATED).body(studentCreado);
    }
}