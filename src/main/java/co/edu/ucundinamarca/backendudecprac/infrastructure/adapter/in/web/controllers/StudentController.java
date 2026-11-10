package co.edu.ucundinamarca.backendudecprac.infrastructure.adapter.in.web.controllers;

import co.edu.ucundinamarca.backendudecprac.application.usecase.StudentUseCaseUseCaseImpl;
import co.edu.ucundinamarca.backendudecprac.domain.model.Student;
import co.edu.ucundinamarca.backendudecprac.domain.model.StudentProfile;
import co.edu.ucundinamarca.backendudecprac.domain.model.User;
import co.edu.ucundinamarca.backendudecprac.domain.port.in.AdminUseCase;
import co.edu.ucundinamarca.backendudecprac.domain.port.in.StudentProfileUseCase;
import co.edu.ucundinamarca.backendudecprac.domain.port.out.StudentProfileRepositoryPort;
import co.edu.ucundinamarca.backendudecprac.infrastructure.adapter.in.web.dtos.StudentRecordRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/registro/estudiantes")
@CrossOrigin(origins = "http://localhost:4200")
public class StudentController {

    private final StudentProfileRepositoryPort studentProfileRepositoryPort;
    private final StudentUseCaseUseCaseImpl useCase;
    private final AdminUseCase adminUseCase;

    public StudentController(StudentProfileRepositoryPort studentProfileRepositoryPort, StudentUseCaseUseCaseImpl useCase, AdminUseCase adminUseCase) {
        this.studentProfileRepositoryPort = studentProfileRepositoryPort;
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

    @GetMapping("/perfil/{documento}")
    public ResponseEntity<StudentProfile> obtenerPerfilPorDocumento(@PathVariable String documento) {
        try {
            StudentProfile profile = studentProfileRepositoryPort.findByDocumentStudent(documento);
            if (profile == null) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(profile);
        } catch (Exception e) {
            return ResponseEntity.noContent().build();
        }
    }

    @PostMapping("/perfil")
    public ResponseEntity<Object> guardarOActualizarPerfil(@RequestBody StudentProfile request) {
        try {
            StudentProfile existente = null;
            try {
                existente = studentProfileRepositoryPort.findByDocumentStudent(request.getDocumentStudent());
            } catch (Exception ignored) {}

            if (existente != null) {
                if (request.getAboutMe() != null) existente.setAboutMe(request.getAboutMe());
                if (request.getSkills() != null) existente.setSkills(request.getSkills());
                if (request.getEnglishLevel() != null) existente.setEnglishLevel(request.getEnglishLevel());
                if (request.getGithubUrl() != null) existente.setGithubUrl(request.getGithubUrl());
                if (request.getLinkLifesheet() != null) existente.setLinkLifesheet(request.getLinkLifesheet());
                if (request.getPhotoUrl() != null) existente.setPhotoUrl(request.getPhotoUrl());

                StudentProfile actualizado = studentProfileRepositoryPort.save(existente);
                return ResponseEntity.ok(actualizado);
            }

            StudentProfile creado = studentProfileRepositoryPort.save(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(creado);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }



}