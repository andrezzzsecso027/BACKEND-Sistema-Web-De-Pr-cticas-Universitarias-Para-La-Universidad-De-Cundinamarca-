package co.edu.ucundinamarca.backendudecprac.infrastructure.adapter.in.web.controllers;

import co.edu.ucundinamarca.backendudecprac.domain.model.Student;
import co.edu.ucundinamarca.backendudecprac.domain.model.StudentProfile;
import co.edu.ucundinamarca.backendudecprac.domain.model.User;
import co.edu.ucundinamarca.backendudecprac.domain.port.in.AdminUseCase;
import co.edu.ucundinamarca.backendudecprac.domain.port.in.CreateStudentUseCase;
import co.edu.ucundinamarca.backendudecprac.domain.port.in.SaveStudentProfileUseCase;
import co.edu.ucundinamarca.backendudecprac.domain.port.in.StudentProfileUseCase;
import co.edu.ucundinamarca.backendudecprac.infrastructure.adapter.in.web.dtos.StudentProfileRequest;
import co.edu.ucundinamarca.backendudecprac.infrastructure.adapter.in.web.dtos.StudentRecordRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/registro/estudiantes")
public class StudentController {

    private final CreateStudentUseCase createStudentUseCase;
    private final AdminUseCase adminUseCase;
    private final StudentProfileUseCase studentProfileUseCase;
    private final SaveStudentProfileUseCase saveStudentProfileUseCase;

    public StudentController(CreateStudentUseCase createStudentUseCase,
                             AdminUseCase adminUseCase,
                             StudentProfileUseCase studentProfileUseCase,
                             SaveStudentProfileUseCase saveStudentProfileUseCase) {
        this.createStudentUseCase = createStudentUseCase;
        this.adminUseCase = adminUseCase;
        this.studentProfileUseCase = studentProfileUseCase;
        this.saveStudentProfileUseCase = saveStudentProfileUseCase;
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

        Student studentCreado = createStudentUseCase.createStudent(user, student);
        return ResponseEntity.status(HttpStatus.CREATED).body(studentCreado);
    }

    @GetMapping("/perfil/{documento}")
    public ResponseEntity<StudentProfile> obtenerPerfilPorDocumento(@PathVariable String documento) {
        StudentProfile profile = studentProfileUseCase.getProfileByDocument(documento);
        return profile == null ? ResponseEntity.noContent().build() : ResponseEntity.ok(profile);
    }

    /** Crea o actualiza el perfil; la lógica de "actualizar solo lo enviado" vive en el caso de uso. */
    @PostMapping("/perfil")
    public ResponseEntity<StudentProfile> guardarOActualizarPerfil(@RequestBody StudentProfileRequest request) {
        return ResponseEntity.ok(saveStudentProfileUseCase.saveProfile(request.toDomain()));
    }
}
