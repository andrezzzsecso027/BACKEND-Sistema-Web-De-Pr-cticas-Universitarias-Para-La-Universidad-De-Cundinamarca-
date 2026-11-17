package co.edu.ucundinamarca.backendudecprac.infrastructure.adapter.in.web.controllers;

import co.edu.ucundinamarca.backendudecprac.domain.model.Student;
import co.edu.ucundinamarca.backendudecprac.domain.model.StudentPostulationSummary;
import co.edu.ucundinamarca.backendudecprac.domain.port.in.StudentSelfServiceUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

/**
 * Endpoints del estudiante autenticado ("yo"). La identidad sale del token JWT (Principal),
 * así el frontend ya no necesita descargar la lista de todos los estudiantes para encontrarse.
 */
@RestController
@RequestMapping("/api/estudiantes/me")
@CrossOrigin(origins = "http://localhost:4200")
public class StudentMeController {

    private final StudentSelfServiceUseCase studentSelfServiceUseCase;

    public StudentMeController(StudentSelfServiceUseCase studentSelfServiceUseCase) {
        this.studentSelfServiceUseCase = studentSelfServiceUseCase;
    }

    @GetMapping
    public ResponseEntity<Student> miPerfil(Principal principal) {
        return ResponseEntity.ok(studentSelfServiceUseCase.getMyStudent(principal.getName()));
    }

    @GetMapping("/postulaciones")
    public ResponseEntity<List<StudentPostulationSummary>> misPostulaciones(Principal principal) {
        return ResponseEntity.ok(studentSelfServiceUseCase.getMyPostulations(principal.getName()));
    }
}
