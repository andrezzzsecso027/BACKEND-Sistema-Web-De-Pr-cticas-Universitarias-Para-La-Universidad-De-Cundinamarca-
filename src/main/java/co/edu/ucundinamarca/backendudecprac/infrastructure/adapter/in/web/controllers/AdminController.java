package co.edu.ucundinamarca.backendudecprac.infrastructure.adapter.in.web.controllers;


import co.edu.ucundinamarca.backendudecprac.domain.model.Company;
import co.edu.ucundinamarca.backendudecprac.domain.model.Offer;
import co.edu.ucundinamarca.backendudecprac.domain.model.Student;
import co.edu.ucundinamarca.backendudecprac.domain.port.in.AdminUseCase;
import co.edu.ucundinamarca.backendudecprac.infrastructure.adapter.in.web.DTOs.VerificationRequest;
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
    @GetMapping("/estudiantes")
    public ResponseEntity<List<Student>> getAllStudents() {
        return ResponseEntity.ok(adminUseCase.getAllStudents());
    }
    @GetMapping("/todas")
    public ResponseEntity<List<Company>> getAllCompanies() {
        return ResponseEntity.ok(adminUseCase.getAllCompanies());
    }
    @GetMapping("/ofertas")
    public ResponseEntity<List<Offer>> getAllOffers() {
        return ResponseEntity.ok(adminUseCase.getAllOffers());
    }
    @PatchMapping("/ofertas/{idOffer}/cerrar")
    public ResponseEntity<String> closeOfferByAdmin(@PathVariable Long idOffer) {
        adminUseCase.closeOfferByAdmin(idOffer);
        return ResponseEntity.ok("La oferta ha sido cerrada por el administrador.");
    }
}
