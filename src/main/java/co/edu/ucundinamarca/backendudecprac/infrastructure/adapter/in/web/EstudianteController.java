package co.edu.ucundinamarca.backendudecprac.infrastructure.adapter.in.web;

import co.edu.ucundinamarca.backendudecprac.application.usecase.EstudianteUseCaseUseCaseImpl;
import co.edu.ucundinamarca.backendudecprac.domain.model.Estudiante;
import co.edu.ucundinamarca.backendudecprac.domain.model.Usuario;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/registro/estudiantes")
@CrossOrigin(origins = "http://localhost:4200")
public class EstudianteController {

    private final EstudianteUseCaseUseCaseImpl useCase;

    public EstudianteController(EstudianteUseCaseUseCaseImpl useCase) {
        this.useCase = useCase;
    }


    @GetMapping
    public ResponseEntity<List<Estudiante>> listar() {
        return ResponseEntity.ok(useCase.listarTodos());
    }


    @PostMapping
    public ResponseEntity<Estudiante> crear(@RequestBody EstudianteRegistroRequest request) {
        Usuario usuario = new Usuario();
        usuario.setCorreoElectronico(request.getCorreo());
        usuario.setContrasenia(request.getContrasenia());
        usuario.setRolUsuario("estudiante");
        Estudiante estudiante = new Estudiante();
        estudiante.setNombres(request.getNombreEstudiante());
        estudiante.setApellidos(request.getApellidoEstudiante());
        estudiante.setDocumento(request.getDocumento());
        estudiante.setTelefono(request.getTelefono());
        estudiante.setDireccion(request.getDireccion());
        estudiante.setSede(request.getSede());
        estudiante.setProgramaAcademico(request.getProgramaAcademico());

        Estudiante estudianteCreado = useCase.createEstudiante(usuario, estudiante);
        return ResponseEntity.status(HttpStatus.CREATED).body(estudianteCreado);
    }
}