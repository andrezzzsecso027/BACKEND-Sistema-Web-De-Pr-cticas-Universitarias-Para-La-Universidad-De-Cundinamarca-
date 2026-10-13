package co.edu.ucundinamarca.backendudecprac.infrastructure.adapter.in.web.controllers;

import co.edu.ucundinamarca.backendudecprac.domain.model.Practice;
import co.edu.ucundinamarca.backendudecprac.domain.port.in.GetPracticeUseCase;
import co.edu.ucundinamarca.backendudecprac.domain.port.in.UpdatePracticeUseCase;
import co.edu.ucundinamarca.backendudecprac.infrastructure.adapter.in.web.DTOs.UpdatePracticeRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/practicas")
@CrossOrigin(origins = "http://localhost:4200")
public class PracticeController {
    private final GetPracticeUseCase getPracticeUseCase;
    private final UpdatePracticeUseCase updatePracticeUseCase;

    public PracticeController(GetPracticeUseCase getPracticeUseCase, UpdatePracticeUseCase updatePracticeUseCase) {
        this.getPracticeUseCase = getPracticeUseCase;
        this.updatePracticeUseCase = updatePracticeUseCase;
    }
    @GetMapping("/postulacion/{idPostulation}")
    public ResponseEntity<?> verPracticaPorPostulacion(@PathVariable Long idPostulation) {
        try {
            Practice practice = getPracticeUseCase.getPracticeByPostulation(idPostulation);
            return ResponseEntity.ok(practice);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al consultar la práctica.");
        }
    }
    @PatchMapping("/{idPractice}")
    public ResponseEntity<?> actualizarFechasPractica(
            @PathVariable Long idPractice,
            @RequestBody UpdatePracticeRequest request) {
        try {
            Practice actualizada = updatePracticeUseCase.updatePractice(
                    idPractice,
                    request.getStartDate(),
                    request.getEndDate(),
                    request.getPracticeStatus()
            );
            return ResponseEntity.ok(actualizada);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al actualizar la práctica.");
        }
    }

}

