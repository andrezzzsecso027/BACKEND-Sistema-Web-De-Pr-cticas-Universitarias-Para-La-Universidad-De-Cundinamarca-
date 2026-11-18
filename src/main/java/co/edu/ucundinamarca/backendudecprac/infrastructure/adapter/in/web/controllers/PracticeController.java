package co.edu.ucundinamarca.backendudecprac.infrastructure.adapter.in.web.controllers;

import co.edu.ucundinamarca.backendudecprac.domain.model.Practice;
import co.edu.ucundinamarca.backendudecprac.domain.model.status.PracticeStatus;
import co.edu.ucundinamarca.backendudecprac.domain.port.in.GetPracticeUseCase;
import co.edu.ucundinamarca.backendudecprac.domain.port.in.UpdatePracticeUseCase;
import co.edu.ucundinamarca.backendudecprac.infrastructure.adapter.in.web.dtos.UpdatePracticeRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/practicas")
public class PracticeController {
    private final GetPracticeUseCase getPracticeUseCase;
    private final UpdatePracticeUseCase updatePracticeUseCase;

    public PracticeController(GetPracticeUseCase getPracticeUseCase, UpdatePracticeUseCase updatePracticeUseCase) {
        this.getPracticeUseCase = getPracticeUseCase;
        this.updatePracticeUseCase = updatePracticeUseCase;
    }

    @GetMapping("/postulacion/{idPostulation}")
    public ResponseEntity<Practice> verPracticaPorPostulacion(@PathVariable Long idPostulation) {
        return ResponseEntity.ok(getPracticeUseCase.getPracticeByPostulation(idPostulation));
    }

    @PatchMapping("/{idPractice}")
    public ResponseEntity<Practice> actualizarFechasPractica(@PathVariable Long idPractice,
                                                             @RequestBody UpdatePracticeRequest request) {
        String estado = request.getPracticeStatus();
        PracticeStatus nuevoEstado = (estado == null || estado.isBlank()) ? null : PracticeStatus.from(estado);
        return ResponseEntity.ok(updatePracticeUseCase.updatePractice(
                idPractice, request.getStartDate(), request.getEndDate(), nuevoEstado));
    }
}
