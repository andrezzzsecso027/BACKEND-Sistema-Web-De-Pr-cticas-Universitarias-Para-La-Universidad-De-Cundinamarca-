package co.edu.ucundinamarca.backendudecprac.domain.port.in;

import co.edu.ucundinamarca.backendudecprac.domain.model.Company;

public interface UpdateCompanyProfileUseCase {
    Company updateProfile(String nit, Company updatedData);
}
