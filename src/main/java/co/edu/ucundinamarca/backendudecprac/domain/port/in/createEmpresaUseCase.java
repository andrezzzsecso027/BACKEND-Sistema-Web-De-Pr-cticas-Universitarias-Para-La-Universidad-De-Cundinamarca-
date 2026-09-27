package co.edu.ucundinamarca.backendudecprac.domain.port.in;

import co.edu.ucundinamarca.backendudecprac.domain.model.Empresa;
import co.edu.ucundinamarca.backendudecprac.domain.model.Usuario;

public interface createEmpresaUseCase {
    Empresa createEmpresa(Usuario usuario, Empresa empresa);
}
