package co.edu.ucundinamarca.backendudecprac.domain.port.out;

import co.edu.ucundinamarca.backendudecprac.domain.model.Company;

import java.util.List;

public interface CompanyRepositoryPort {
    Company saveEmpresa(Company company);
    List<Company> findAllCompanies();

    boolean existsByNit(String nit);
}
