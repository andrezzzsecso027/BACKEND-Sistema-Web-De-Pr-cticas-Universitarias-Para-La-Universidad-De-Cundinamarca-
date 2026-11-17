package co.edu.ucundinamarca.backendudecprac.domain.port.in;

import co.edu.ucundinamarca.backendudecprac.domain.model.Company;

import java.util.Optional;

public interface GetCompanyUseCase {
    Optional<Company> getCompanyByNit(String nit);
}
