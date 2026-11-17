package co.edu.ucundinamarca.backendudecprac.infrastructure.adapter.in.web.controllers;

import co.edu.ucundinamarca.backendudecprac.domain.model.Postulation;
import co.edu.ucundinamarca.backendudecprac.domain.model.PostulationHistory;
import co.edu.ucundinamarca.backendudecprac.domain.model.status.PostulationStatus;
import co.edu.ucundinamarca.backendudecprac.domain.port.in.CreatePostulationUseCase;
import co.edu.ucundinamarca.backendudecprac.domain.port.in.GetPostulationHistoryUseCase;
import co.edu.ucundinamarca.backendudecprac.domain.port.in.GetPostulationsUseCase;
import co.edu.ucundinamarca.backendudecprac.domain.port.in.UpdatePostulationStatusUseCase;
import co.edu.ucundinamarca.backendudecprac.infrastructure.adapter.in.web.dtos.PostulationDetailResponse;
import co.edu.ucundinamarca.backendudecprac.infrastructure.adapter.in.web.dtos.PostulationRequest;
import co.edu.ucundinamarca.backendudecprac.infrastructure.adapter.in.web.dtos.UpdatePostulationRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** Los errores de negocio los traduce GlobalExceptionHandler (404 / 409 / 400). */
@RestController
@RequestMapping("/api/postulaciones")
@CrossOrigin(origins = "http://localhost:4200")
public class PostulationController {
    private final CreatePostulationUseCase createPostulationUseCase;
    private final GetPostulationsUseCase getPostulationsUseCase;
    private final UpdatePostulationStatusUseCase updatePostulationStatusUseCase;
    private final GetPostulationHistoryUseCase getPostulationHistoryUseCase;

    public PostulationController(CreatePostulationUseCase createPostulationUseCase, GetPostulationsUseCase getPostulationsUseCase, UpdatePostulationStatusUseCase updatePostulationStatusUseCase, GetPostulationHistoryUseCase getPostulationHistoryUseCase) {
        this.createPostulationUseCase = createPostulationUseCase;
        this.getPostulationsUseCase = getPostulationsUseCase;
        this.updatePostulationStatusUseCase = updatePostulationStatusUseCase;
        this.getPostulationHistoryUseCase = getPostulationHistoryUseCase;
    }

    @PostMapping("/aplicar")
    public ResponseEntity<Postulation> aplicarAOferta(@RequestBody PostulationRequest request) {
        Postulation postulation = new Postulation();
        postulation.setIdOffer(request.getIdOffer());
        postulation.setDocumentNumberStudent(request.getDocumentNumberStudent());
        return ResponseEntity.status(HttpStatus.CREATED).body(createPostulationUseCase.createPostulation(postulation));
    }

    @GetMapping("/oferta/{idOffer}")
    public ResponseEntity<List<PostulationDetailResponse>> verPostuladosAOferta(@PathVariable Long idOffer) {
        // El caso de uso devuelve el modelo de dominio; el adaptador web lo traduce a su DTO
        List<PostulationDetailResponse> postulaciones = getPostulationsUseCase.getPostulationsByOfferId(idOffer)
                .stream()
                .map(PostulationDetailResponse::from)
                .toList();
        return postulaciones.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(postulaciones);
    }

    @PatchMapping("/{idPostulation}/estado")
    public ResponseEntity<Postulation> cambiarEstadoPostulacion(@PathVariable Long idPostulation,
                                                                @RequestBody UpdatePostulationRequest request) {
        // Texto -> enum: un estado desconocido responde 400 con los valores permitidos
        PostulationStatus nuevoEstado = PostulationStatus.from(request.getNewStatus());
        return ResponseEntity.ok(updatePostulationStatusUseCase.updateStatus(idPostulation, nuevoEstado, request.getObservations()));
    }

    @GetMapping("/{idPostulation}/historial")
    public ResponseEntity<List<PostulationHistory>> verHistorialPostulacion(@PathVariable Long idPostulation) {
        List<PostulationHistory> historial = getPostulationHistoryUseCase.getHistory(idPostulation);
        return historial.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(historial);
    }
}
