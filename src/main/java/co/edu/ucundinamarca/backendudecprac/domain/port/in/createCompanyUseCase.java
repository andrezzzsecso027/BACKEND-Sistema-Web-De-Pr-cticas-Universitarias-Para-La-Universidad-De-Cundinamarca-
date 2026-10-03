package co.edu.ucundinamarca.backendudecprac.domain.port.in;

import co.edu.ucundinamarca.backendudecprac.domain.model.Company;
import co.edu.ucundinamarca.backendudecprac.domain.model.User;

import java.util.List;

public interface createCompanyUseCase {
    Company createCompany(User user, Company company);
    List<Company> findAllCompanies();
}
