package co.edu.ucundinamarca.backendudecprac.infrastructure.adapter.in.web.controllers;

import co.edu.ucundinamarca.backendudecprac.domain.model.Postulation;
import co.edu.ucundinamarca.backendudecprac.domain.model.PostulationHistory;
import co.edu.ucundinamarca.backendudecprac.domain.port.in.CreatePostulationUseCase;
import co.edu.ucundinamarca.backendudecprac.domain.port.in.GetPostulationHistoryUseCase;
import co.edu.ucundinamarca.backendudecprac.domain.port.in.GetPostulationsUseCase;
import co.edu.ucundinamarca.backendudecprac.domain.port.in.UpdatePostulationStatusUseCase;
import co.edu.ucundinamarca.backendudecprac.infrastructure.adapter.in.web.DTOs.PostulationDetailResponse;
import co.edu.ucundinamarca.backendudecprac.infrastructure.adapter.in.web.DTOs.PostulationRequest;
import co.edu.ucundinamarca.backendudecprac.infrastructure.adapter.in.web.DTOs.UpdatePostulationRequest;
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
    private final UpdatePostulationStatusUseCase updatePostulationStatusUseCase;
    private final GetPostulationHistoryUseCase getPostulationHistoryUseCase;

    public PostulationController(CreatePostulationUseCase createPostulationUseCase, GetPostulationsUseCase getPostulationsUseCase, UpdatePostulationStatusUseCase updatePostulationStatusUseCase, GetPostulationHistoryUseCase getPostulationHistoryUseCase) {
        this.createPostulationUseCase = createPostulationUseCase;
        this.getPostulationsUseCase = getPostulationsUseCase;
        this.updatePostulationStatusUseCase = updatePostulationStatusUseCase;
        this.getPostulationHistoryUseCase = getPostulationHistoryUseCase;
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

    @PatchMapping("/{idPostulation}/estado")
    public ResponseEntity<?> cambiarEstadoPostulacion(
            @PathVariable Long idPostulation,
            @RequestBody UpdatePostulationRequest request) {
        try {
            // Capturamos el objeto que devuelve tu Caso de Uso
            Postulation actualizada = updatePostulationStatusUseCase.updateStatus(
                    idPostulation,
                    request.getNewStatus(),
                    request.getObservations()
            );

            // Devolvemos el objeto real con un 200 OK
            return ResponseEntity.ok(actualizada);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error procesando la solicitud.");
        }
    }

    @GetMapping("/{idPostulation}/historial")
    public ResponseEntity<?> verHistorialPostulacion(@PathVariable Long idPostulation) {
        try {
            List<PostulationHistory> historial = getPostulationHistoryUseCase.getHistory(idPostulation);
            if (historial.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(historial);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al consultar el historial.");
        }
    }

}
