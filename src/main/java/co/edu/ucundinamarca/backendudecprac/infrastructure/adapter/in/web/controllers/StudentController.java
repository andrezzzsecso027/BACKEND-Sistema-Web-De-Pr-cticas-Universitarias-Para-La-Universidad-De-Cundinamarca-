package co.edu.ucundinamarca.backendudecprac.infrastructure.adapter.in.web.controllers;

import co.edu.ucundinamarca.backendudecprac.application.usecase.StudentUseCaseUseCaseImpl;
import co.edu.ucundinamarca.backendudecprac.domain.model.Student;
import co.edu.ucundinamarca.backendudecprac.domain.model.User;
import co.edu.ucundinamarca.backendudecprac.domain.port.in.AdminUseCase;
import co.edu.ucundinamarca.backendudecprac.infrastructure.adapter.in.web.DTOs.StudentRecordRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/registro/estudiantes")
@CrossOrigin(origins = "http://localhost:4200")
public class StudentController {

    private final StudentUseCaseUseCaseImpl useCase;
    private final AdminUseCase adminUseCase;
    public StudentController(StudentUseCaseUseCaseImpl useCase, AdminUseCase adminUseCase) {
        this.useCase = useCase;
        this.adminUseCase = adminUseCase;
    }


    @GetMapping
    public ResponseEntity<List<Student>> listar() {
        return ResponseEntity.ok(adminUseCase.getAllStudents());
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