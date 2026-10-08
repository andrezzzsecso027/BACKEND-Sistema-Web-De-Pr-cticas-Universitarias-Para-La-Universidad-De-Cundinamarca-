package co.edu.ucundinamarca.backendudecprac.domain.port.out;

import co.edu.ucundinamarca.backendudecprac.domain.model.Company;

import java.util.List;
import java.util.Optional;

public interface CompanyRepositoryPort {
    Company saveEmpresa(Company company);
    List<Company> findAllCompanies();

    boolean existsByNit(String nit);
    List<Company> findByVerificationStatus(String verificationStatus);
    Optional<Company> findByNit(String nit);
}
