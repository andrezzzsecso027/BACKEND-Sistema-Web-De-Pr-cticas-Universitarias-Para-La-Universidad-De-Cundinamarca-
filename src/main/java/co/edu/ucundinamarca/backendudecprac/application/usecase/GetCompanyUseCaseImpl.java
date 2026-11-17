package co.edu.ucundinamarca.backendudecprac.application.usecase;

import co.edu.ucundinamarca.backendudecprac.domain.model.Company;
import co.edu.ucundinamarca.backendudecprac.domain.port.in.GetCompanyUseCase;
import co.edu.ucundinamarca.backendudecprac.domain.port.out.CompanyRepositoryPort;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class GetCompanyUseCaseImpl implements GetCompanyUseCase {

    private final CompanyRepositoryPort companyRepositoryPort;

    public GetCompanyUseCaseImpl(CompanyRepositoryPort companyRepositoryPort) {
        this.companyRepositoryPort = companyRepositoryPort;
    }

    @Override
    public Optional<Company> getCompanyByNit(String nit) {
        return companyRepositoryPort.findByNit(nit);
    }
}
