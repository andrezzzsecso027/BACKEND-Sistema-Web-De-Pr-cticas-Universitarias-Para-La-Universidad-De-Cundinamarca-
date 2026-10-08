package co.edu.ucundinamarca.backendudecprac.infrastructure.adapter.in.web;


import co.edu.ucundinamarca.backendudecprac.domain.model.Company;
import co.edu.ucundinamarca.backendudecprac.domain.port.in.AdminUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/empresas")
@CrossOrigin(origins = "http://localhost:4200")
public class AdminController {
    private final AdminUseCase adminUseCase;

    public AdminController(AdminUseCase adminUseCase) {
        this.adminUseCase = adminUseCase;
    }
    @GetMapping("/pendientes")
    public ResponseEntity<List<Company>> getEmpresasPendientes() {
        List<Company> pendientes = adminUseCase.getCompaniesPending();
        return ResponseEntity.ok(pendientes);
    }

    @PutMapping("/{nit}/verificar")
    public ResponseEntity<String> verificarEmpresa(
            @PathVariable String nit,
            @RequestBody VerificationRequest request) {

        // Llamamos al caso de uso pasando el NIT de la URL y el booleano del Body
        adminUseCase.verifyCompany(nit, request.isAprobada());

        String mensaje = request.isAprobada() ?
                "La empresa ha sido aprobada y su acceso habilitado." :
                "La empresa ha sido rechazada.";

        return ResponseEntity.ok(mensaje);
    }
}
