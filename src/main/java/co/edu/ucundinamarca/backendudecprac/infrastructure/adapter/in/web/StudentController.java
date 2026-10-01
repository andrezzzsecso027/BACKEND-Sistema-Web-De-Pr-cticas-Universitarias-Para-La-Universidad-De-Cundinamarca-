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
public class EstudianteController {

    private final StudentUseCaseUseCaseImpl useCase;

    public EstudianteController(StudentUseCaseUseCaseImpl useCase) {
        this.useCase = useCase;
    }


    @GetMapping
    public ResponseEntity<List<Student>> listar() {
        return ResponseEntity.ok(useCase.listarTodos());
    }


    @PostMapping
    public ResponseEntity<Student> crear(@RequestBody EstudianteRegistroRequest request) {
        User user = new User();
        user.setCorreoElectronico(request.getCorreo());
        user.setContrasenia(request.getContrasenia());
        user.setRolUsuario("estudiante");
        Student student = new Student();
        student.setNombres(request.getNombreEstudiante());
        student.setApellidos(request.getApellidoEstudiante());
        student.setDocumento(request.getDocumento());
        student.setTelefono(request.getTelefono());
        student.setDireccion(request.getDireccion());
        student.setSede(request.getSede());
        student.setProgramaAcademico(request.getProgramaAcademico());

        Student studentCreado = useCase.createEstudiante(user, student);
        return ResponseEntity.status(HttpStatus.CREATED).body(studentCreado);
    }
}