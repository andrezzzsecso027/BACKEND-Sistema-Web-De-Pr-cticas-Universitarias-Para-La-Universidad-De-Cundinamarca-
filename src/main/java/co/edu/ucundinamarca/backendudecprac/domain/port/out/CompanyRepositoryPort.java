package co.edu.ucundinamarca.backendudecprac.domain.port.out;

import co.edu.ucundinamarca.backendudecprac.domain.model.Empresa;

import java.util.List;

public interface empresaRepositoryPort {
    Empresa saveEmpresa(Empresa empresa);
    List<Empresa> findAllEmpresas();

    boolean existsByNit(String nit);
}
