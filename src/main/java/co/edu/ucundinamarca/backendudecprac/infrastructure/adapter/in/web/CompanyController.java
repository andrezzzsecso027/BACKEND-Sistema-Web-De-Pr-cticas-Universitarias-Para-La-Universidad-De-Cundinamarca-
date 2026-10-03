package co.edu.ucundinamarca.backendudecprac.infrastructure.adapter.in.web;

import co.edu.ucundinamarca.backendudecprac.domain.model.Company;
import co.edu.ucundinamarca.backendudecprac.domain.model.User;
import co.edu.ucundinamarca.backendudecprac.domain.port.in.createCompanyUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/registro/empresas")
@CrossOrigin(origins = "http://localhost:4200")
public class CompanyController {
    private final createCompanyUseCase createempresausecase;

    public CompanyController(createCompanyUseCase createempresausecase) {
        this.createempresausecase = createempresausecase;
    }

    @GetMapping
    public ResponseEntity<List<Company>> findAllEmpresas() {
        List<Company> lista = createempresausecase.findAllCompanies();
        return ResponseEntity.ok(lista);
    }

    @PostMapping
    public ResponseEntity<Company> registrar(@RequestBody companyRecordRequest request) {
        User user = new User();
        user.setEmailAddres(request.getCorreo());
        user.setPassword(request.getContrasenia());
        user.setUserRol("empresa");

        Company company = new Company();
        company.setNIT(request.getNit());
        company.setCompanyName(request.getNombreEmpresa());
        company.setCompanyType(request.getTipoEmpresa());
        company.setCompanyAddres(request.getDireccionEmpresa());
        company.setCompanyPhoneNumber(request.getTelefonoEmpresa());

        Company creada = createempresausecase.createCompany(user, company);
        return ResponseEntity.status(HttpStatus.CREATED).body(creada);
    }

}
