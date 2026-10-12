package co.edu.ucundinamarca.backendudecprac.infrastructure.adapter.in.web.controllers;

import co.edu.ucundinamarca.backendudecprac.domain.model.Postulation;
import co.edu.ucundinamarca.backendudecprac.domain.port.in.CreatePostulationUseCase;
import co.edu.ucundinamarca.backendudecprac.domain.port.in.GetPostulationsUseCase;
import co.edu.ucundinamarca.backendudecprac.infrastructure.adapter.in.web.DTOs.PostulationDetailResponse;
import co.edu.ucundinamarca.backendudecprac.infrastructure.adapter.in.web.DTOs.PostulationRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/postulaciones")
@CrossOrigin(origins = "http://localhost:4200")
public class PostulationController {
    private final CreatePostulationUseCase createPostulationUseCase;
    private final GetPostulationsUseCase getPostulationsUseCase;

    public PostulationController(CreatePostulationUseCase createPostulationUseCase, GetPostulationsUseCase getPostulationsUseCase) {
        this.createPostulationUseCase = createPostulationUseCase;
        this.getPostulationsUseCase = getPostulationsUseCase;
    }
    @PostMapping("/aplicar")
    public ResponseEntity<?> aplicarAOferta(@RequestBody PostulationRequest request) {
        try {
            Postulation postulation = new Postulation();
            postulation.setIdOffer(request.getIdOffer());
            postulation.setDocumentNumberStudent(request.getDocumentNumberStudent());

            Postulation creada = createPostulationUseCase.createPostulation(postulation);
            return ResponseEntity.status(HttpStatus.CREATED).body(creada);

        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage()); // 409 Conflict si ya existe
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ocurrió un error en el servidor.");
        }
    }

    @GetMapping("/oferta/{idOffer}")
    public ResponseEntity<List<PostulationDetailResponse>> verPostuladosAOferta(@PathVariable Long idOffer) {
        try {
            // Utilizamos el caso de uso de consulta, que ya nos devuelve el DTO armado
            List<PostulationDetailResponse> postulaciones = getPostulationsUseCase.getPostulationsByOfferId(idOffer);

            if (postulaciones.isEmpty()) {
                return ResponseEntity.noContent().build();
            }

            return ResponseEntity.ok(postulaciones);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
