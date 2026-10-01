package co.edu.ucundinamarca.backendudecprac.infrastructure.adapter.in.web;

import co.edu.ucundinamarca.backendudecprac.domain.model.Empresa;
import co.edu.ucundinamarca.backendudecprac.domain.model.Usuario;
import co.edu.ucundinamarca.backendudecprac.domain.port.in.createEmpresaUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/registro/empresas")
@CrossOrigin(origins = "http://localhost:4200")
public class empresaController {
    private final createEmpresaUseCase createempresausecase;

    public empresaController(createEmpresaUseCase createempresausecase) {
        this.createempresausecase = createempresausecase;
    }

    @GetMapping
    public ResponseEntity<List<Empresa>> findAllEmpresas() {
        List<Empresa> lista = createempresausecase.findAllEmpresas();
        return ResponseEntity.ok(lista);
    }

    @PostMapping
    public ResponseEntity<Empresa> registrar(@RequestBody EmpresaRegistroRequest request) {
        Usuario usuario = new Usuario();
        usuario.setCorreoElectronico(request.getCorreo());
        usuario.setContrasenia(request.getContrasenia());
        usuario.setRolUsuario("empresa");

        Empresa empresa = new Empresa();
        empresa.setNIT(request.getNit());
        empresa.setNombreEmpresa(request.getNombreEmpresa());
        empresa.setTipoEmpresa(request.getTipoEmpresa());
        empresa.setDireccionEmpresa(request.getDireccionEmpresa());
        empresa.setTelefonoEmpresa(request.getTelefonoEmpresa());

        Empresa creada = createempresausecase.createEmpresa(usuario, empresa);
        return ResponseEntity.status(HttpStatus.CREATED).body(creada);
    }

}
