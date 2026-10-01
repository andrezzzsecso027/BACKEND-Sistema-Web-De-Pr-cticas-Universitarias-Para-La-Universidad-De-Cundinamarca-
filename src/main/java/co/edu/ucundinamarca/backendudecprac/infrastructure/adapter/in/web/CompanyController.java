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
public class empresaController {
    private final createCompanyUseCase createempresausecase;

    public empresaController(createCompanyUseCase createempresausecase) {
        this.createempresausecase = createempresausecase;
    }

    @GetMapping
    public ResponseEntity<List<Company>> findAllEmpresas() {
        List<Company> lista = createempresausecase.findAllEmpresas();
        return ResponseEntity.ok(lista);
    }

    @PostMapping
    public ResponseEntity<Company> registrar(@RequestBody EmpresaRegistroRequest request) {
        User user = new User();
        user.setCorreoElectronico(request.getCorreo());
        user.setContrasenia(request.getContrasenia());
        user.setRolUsuario("empresa");

        Company company = new Company();
        company.setNIT(request.getNit());
        company.setNombreEmpresa(request.getNombreEmpresa());
        company.setTipoEmpresa(request.getTipoEmpresa());
        company.setDireccionEmpresa(request.getDireccionEmpresa());
        company.setTelefonoEmpresa(request.getTelefonoEmpresa());

        Company creada = createempresausecase.createEmpresa(user, company);
        return ResponseEntity.status(HttpStatus.CREATED).body(creada);
    }

}
