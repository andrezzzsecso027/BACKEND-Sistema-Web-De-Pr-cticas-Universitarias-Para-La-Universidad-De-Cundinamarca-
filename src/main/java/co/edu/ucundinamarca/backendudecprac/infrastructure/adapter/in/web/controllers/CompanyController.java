package co.edu.ucundinamarca.backendudecprac.infrastructure.adapter.in.web.controllers;

import co.edu.ucundinamarca.backendudecprac.domain.model.Company;
import co.edu.ucundinamarca.backendudecprac.domain.model.LegalRepresentative;
import co.edu.ucundinamarca.backendudecprac.domain.model.User;
import co.edu.ucundinamarca.backendudecprac.domain.port.in.AdminUseCase;
import co.edu.ucundinamarca.backendudecprac.domain.port.in.CreateCompanyUseCase;
import co.edu.ucundinamarca.backendudecprac.domain.port.in.GetCompanyUseCase;
import co.edu.ucundinamarca.backendudecprac.domain.port.in.UpdateCompanyProfileUseCase;
import co.edu.ucundinamarca.backendudecprac.infrastructure.adapter.in.web.dtos.CompanyProfileUpdateRequest;
import co.edu.ucundinamarca.backendudecprac.infrastructure.adapter.in.web.dtos.CompanyRecordRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/registro/empresas")
public class CompanyController {

    private final CreateCompanyUseCase createCompanyUseCase;
    private final AdminUseCase adminUseCase;
    private final GetCompanyUseCase getCompanyUseCase;
    private final UpdateCompanyProfileUseCase updateCompanyProfileUseCase;

    public CompanyController(CreateCompanyUseCase createCompanyUseCase,
                             AdminUseCase adminUseCase,
                             GetCompanyUseCase getCompanyUseCase,
                             UpdateCompanyProfileUseCase updateCompanyProfileUseCase) {
        this.createCompanyUseCase = createCompanyUseCase;
        this.adminUseCase = adminUseCase;
        this.getCompanyUseCase = getCompanyUseCase;
        this.updateCompanyProfileUseCase = updateCompanyProfileUseCase;
    }

    @GetMapping
    public ResponseEntity<List<Company>> findAllEmpresas() {
        return ResponseEntity.ok(adminUseCase.getAllCompanies());
    }

    @PostMapping
    public ResponseEntity<Company> registrar(@RequestBody CompanyRecordRequest request) {
        User user = new User();
        user.setEmailAddres(request.getCorreo());
        user.setPassword(request.getContrasenia());
        user.setUserRol("empresa");
        LegalRepresentative representative = new LegalRepresentative();
        representative.setNameRepresentative(request.getNameRepresentive());
        representative.setLastNameRepresentative(request.getLastNameRepresentive());
        representative.setNumberDocument(request.getNumberDocument());
        Company company = new Company();
        company.setNit(request.getNit());
        company.setCompanyName(request.getNombreEmpresa());
        company.setCompanyType(request.getTipoEmpresa());
        company.setCompanyAddress(request.getDireccionEmpresa());
        company.setCompanyPhoneNumber(request.getTelefonoEmpresa());
        company.setLegalName(request.getLegalName());
        company.setCity(request.getCity());
        company.setDepartment(request.getDepartment());
        company.setLegalRepresentative(representative);
        company.setRutPdfUrl(request.getUrlPdfNit());

        Company creada = createCompanyUseCase.createCompany(user, company);
        return ResponseEntity.status(HttpStatus.CREATED).body(creada);
    }

    @GetMapping("/nit/{nit}")
    public ResponseEntity<Company> obtenerEmpresaPorNit(@PathVariable String nit) {
        return getCompanyUseCase.getCompanyByNit(nit)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /** La lógica de "actualizar solo lo enviado" vive en UpdateCompanyProfileUseCaseImpl. */
    @PutMapping("/nit/{nit}/perfil")
    public ResponseEntity<Company> actualizarPerfilEmpresa(@PathVariable String nit,
                                                           @RequestBody CompanyProfileUpdateRequest request) {
        return ResponseEntity.ok(updateCompanyProfileUseCase.updateProfile(nit, request.toDomain()));
    }
}
