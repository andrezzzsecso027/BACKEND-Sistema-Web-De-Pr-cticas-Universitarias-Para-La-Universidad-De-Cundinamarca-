package co.edu.ucundinamarca.backendudecprac.application.usecase;

import co.edu.ucundinamarca.backendudecprac.domain.model.Company;
import co.edu.ucundinamarca.backendudecprac.domain.port.in.UpdateCompanyProfileUseCase;
import co.edu.ucundinamarca.backendudecprac.domain.port.out.CompanyRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UpdateCompanyProfileUseCaseImpl implements UpdateCompanyProfileUseCase {

    private final CompanyRepositoryPort companyRepositoryPort;

    public UpdateCompanyProfileUseCaseImpl(CompanyRepositoryPort companyRepositoryPort) {
        this.companyRepositoryPort = companyRepositoryPort;
    }

    @Transactional
    @Override
    public Company updateProfile(String nit, Company updatedData) {
        Company existing = companyRepositoryPort.findByNit(nit)
                .orElseThrow(() -> new IllegalArgumentException("No se encontró la empresa con NIT: " + nit));

        if (updatedData.getCompanyName() != null) existing.setCompanyName(updatedData.getCompanyName());
        if (updatedData.getCompanyDescription() != null) existing.setCompanyDescription(updatedData.getCompanyDescription());
        if (updatedData.getLogoUrl() != null) existing.setLogoUrl(updatedData.getLogoUrl());
        if (updatedData.getRutPdfUrl() != null) existing.setRutPdfUrl(updatedData.getRutPdfUrl());

        return companyRepositoryPort.saveCompany(existing);
    }
}
