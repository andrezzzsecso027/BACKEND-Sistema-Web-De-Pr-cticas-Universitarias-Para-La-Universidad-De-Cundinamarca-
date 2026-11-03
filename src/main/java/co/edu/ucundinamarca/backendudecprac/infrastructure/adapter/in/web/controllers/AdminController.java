package co.edu.ucundinamarca.backendudecprac.infrastructure.adapter.in.web.controllers;


import co.edu.ucundinamarca.backendudecprac.domain.model.*;
import co.edu.ucundinamarca.backendudecprac.domain.port.in.*;
import co.edu.ucundinamarca.backendudecprac.domain.port.out.StudentProfileRepositoryPort;
import co.edu.ucundinamarca.backendudecprac.infrastructure.adapter.in.web.dtos.VerificationRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "http://localhost:4200")
public class AdminController {
    private final AdminUseCase adminUseCase;
    private final VerifyCompanyUseCase verifyCompanyUseCase;
    private final ClosedOfferByAdminUseCase closedOfferByAdminUseCase;
    private final ModerateCommentUseCase moderateCommentUseCase;
    private final StudentProfileRepositoryPort studentProfileRepositoryPort;

    public AdminController(AdminUseCase adminUseCase, VerifyCompanyUseCase verifyCompanyUseCase, ClosedOfferByAdminUseCase closedOfferByAdminUseCase, ModerateCommentUseCase moderateCommentUseCase, StudentProfileRepositoryPort studentProfileRepositoryPort) {
        this.adminUseCase = adminUseCase;
        this.verifyCompanyUseCase = verifyCompanyUseCase;
        this.closedOfferByAdminUseCase = closedOfferByAdminUseCase;
        this.moderateCommentUseCase = moderateCommentUseCase;
        this.studentProfileRepositoryPort = studentProfileRepositoryPort;
    }


    @GetMapping("/empresas/pendientes")
    public ResponseEntity<List<Company>> getEmpresasPendientes() {
        List<Company> pendientes = verifyCompanyUseCase.getCompaniesPending();
        return ResponseEntity.ok(pendientes);
    }

    @PutMapping("/empresas/{nit}/verificar")
    public ResponseEntity<String> verificarEmpresa(
            @PathVariable String nit,
            @RequestBody VerificationRequest request) {

        // Llamamos al caso de uso pasando el NIT de la URL y el booleano del Body
        verifyCompanyUseCase.verifyCompany(nit, request.isAprobada());

        String mensaje = request.isAprobada() ?
                "La empresa ha sido aprobada y su acceso habilitado." :
                "La empresa ha sido rechazada.";

        return ResponseEntity.ok(mensaje);
    }

    @GetMapping("/estudiantes")
    public ResponseEntity<List<Student>> getAllStudents() {
        return ResponseEntity.ok(adminUseCase.getAllStudents());
    }

    @GetMapping("/empresas/todas")
    public ResponseEntity<List<Company>> getAllCompanies() {
        return ResponseEntity.ok(adminUseCase.getAllCompanies());
    }

    @GetMapping("/ofertas")
    public ResponseEntity<List<Offer>> getAllOffers() {
        return ResponseEntity.ok(adminUseCase.getAllOffers());
    }

    @PatchMapping("/ofertas/{idOffer}/cerrar")
    public ResponseEntity<String> closeOfferByAdmin(@PathVariable Long idOffer) {
        closedOfferByAdminUseCase.closeOfferByAdmin(idOffer);
        return ResponseEntity.ok("La oferta ha sido cerrada por el administrador.");
    }

    @GetMapping("/comentarios/pendientes")
    public ResponseEntity<List<Comment>> getPendingCommentsForAdmin() {
        return ResponseEntity.ok(moderateCommentUseCase.getPendingCommentsForAdmin());
    }

    @PatchMapping("/comentarios/{idComment}/moderar")
    public ResponseEntity<Object> moderateComment(
            @PathVariable Long idComment,
            @RequestParam("aprobado") boolean isApproved) {
        try {
            Comment moderated = moderateCommentUseCase.moderateComment(idComment, isApproved);
            return ResponseEntity.ok(moderated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }

    }
    @GetMapping("/estudiantes/{documento}/perfil")
    public ResponseEntity<StudentProfile> getStudentProfile(@PathVariable String documento) {
        try {
            StudentProfile profile = studentProfileRepositoryPort.findByDocumentStudent(documento);
            if (profile == null) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(profile);
        } catch (Exception e) {
            return ResponseEntity.noContent().build();
        }
    }


}
