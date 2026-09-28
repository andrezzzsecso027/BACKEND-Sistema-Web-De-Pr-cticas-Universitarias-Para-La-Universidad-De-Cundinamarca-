package co.edu.ucundinamarca.backendudecprac.application.usecase;

import co.edu.ucundinamarca.backendudecprac.domain.model.Empresa;
import co.edu.ucundinamarca.backendudecprac.domain.model.Usuario;
import co.edu.ucundinamarca.backendudecprac.domain.port.in.createEmpresaUseCase;
import co.edu.ucundinamarca.backendudecprac.domain.port.out.empresaRepositoryPort;
import co.edu.ucundinamarca.backendudecprac.domain.port.out.usuarioRepositoryPort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class createEmpresaUseCaseImpl implements createEmpresaUseCase{

    private final empresaRepositoryPort empresarepositoryPort;
    private final usuarioRepositoryPort usuariorepositoryport;

    public createEmpresaUseCaseImpl(empresaRepositoryPort empresarepositoryPort, usuarioRepositoryPort usuariorepositoryport) {
        this.empresarepositoryPort = empresarepositoryPort;
        this.usuariorepositoryport = usuariorepositoryport;
    }

    @Override
    public Empresa createEmpresa(Usuario usuario, Empresa empresa) {
        Usuario usuarioGuardado = usuariorepositoryport.saveUsuario(usuario);
        empresa.setIdUsuario(usuarioGuardado.getIdUsuario());
        return empresarepositoryPort.saveEmpresa(empresa);
    }

    @Override
    public List<Empresa> findAllEmpresas() {
        return empresarepositoryPort.findAllEmpresas();
    }


}
