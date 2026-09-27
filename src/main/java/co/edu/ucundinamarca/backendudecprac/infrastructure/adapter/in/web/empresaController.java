package co.edu.ucundinamarca.backendudecprac.infrastructure.adapter.in.web;

import co.edu.ucundinamarca.backendudecprac.domain.model.Empresa;
import co.edu.ucundinamarca.backendudecprac.domain.model.Usuario;
import co.edu.ucundinamarca.backendudecprac.domain.port.in.createEmpresaUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/registro/empresas")
public class empresaController {
    private final createEmpresaUseCase createempresausecase;

    public empresaController(createEmpresaUseCase createempresausecase) {
        this.createempresausecase = createempresausecase;
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
