package co.edu.ucundinamarca.backendudecprac.infrastructure.adapter.in.web.controllers;

import co.edu.ucundinamarca.backendudecprac.domain.model.ContractDocument;
import co.edu.ucundinamarca.backendudecprac.domain.port.in.FindContractDocumentUseCase;
import co.edu.ucundinamarca.backendudecprac.domain.port.in.SaveContractDocumentUseCase;
import co.edu.ucundinamarca.backendudecprac.infrastructure.adapter.in.web.dtos.ContractDocumentRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/documentos")
public class DocumentController {

    private final SaveContractDocumentUseCase saveUseCase;
    private final FindContractDocumentUseCase findUseCase;

    public DocumentController(SaveContractDocumentUseCase saveUseCase, FindContractDocumentUseCase findUseCase) {
        this.saveUseCase = saveUseCase;
        this.findUseCase = findUseCase;
    }

    @PostMapping("/seguimiento")
    public ResponseEntity<ContractDocument> guardarSeguimiento(@RequestBody ContractDocumentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(saveUseCase.saveOrUpdateContract(request.toDomain()));
    }

    @GetMapping("/empresa/{nitCompany}")
    public ResponseEntity<List<ContractDocument>> obtenerPorEmpresa(@PathVariable String nitCompany) {
        return ResponseEntity.ok(findUseCase.getContractsByCompanyNit(nitCompany));
    }

    @GetMapping("/postulacion/{idPostulation}")
    public ResponseEntity<ContractDocument> obtenerPorPostulacion(@PathVariable Long idPostulation) {
        return findUseCase.getContractByPostulationId(idPostulation)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }
    @GetMapping("/todos")
    public ResponseEntity<List<ContractDocument>> obtenerTodos() {
        return ResponseEntity.ok(findUseCase.getAllContracts());
    }
}
