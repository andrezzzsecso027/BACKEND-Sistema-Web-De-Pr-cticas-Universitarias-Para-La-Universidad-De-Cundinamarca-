package co.edu.ucundinamarca.backendudecprac.application.usecase;

import co.edu.ucundinamarca.backendudecprac.domain.exceptions.ResourceNotFoundException;
import co.edu.ucundinamarca.backendudecprac.domain.model.Company;
import co.edu.ucundinamarca.backendudecprac.domain.model.LegalRepresentative;
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

    /**
     * Actualiza solo los campos enviados (no nulos). El NIT y el estado de verificación
     * no se pueden modificar desde el perfil.
     */
    @Transactional
    @Override
    public Company updateProfile(String nit, Company updatedData) {
        Company existing = companyRepositoryPort.findByNit(nit)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró la empresa con NIT: " + nit));

        if (updatedData.getCompanyName() != null) existing.setCompanyName(updatedData.getCompanyName());
        if (updatedData.getLegalName() != null) existing.setLegalName(updatedData.getLegalName());
        if (updatedData.getCompanyType() != null) existing.setCompanyType(updatedData.getCompanyType());
        if (updatedData.getCompanyPhoneNumber() != null) existing.setCompanyPhoneNumber(updatedData.getCompanyPhoneNumber());
        if (updatedData.getDepartment() != null) existing.setDepartment(updatedData.getDepartment());
        if (updatedData.getCity() != null) existing.setCity(updatedData.getCity());
        if (updatedData.getCompanyAddress() != null) existing.setCompanyAddress(updatedData.getCompanyAddress());
        if (updatedData.getCompanyDescription() != null) existing.setCompanyDescription(updatedData.getCompanyDescription());
        if (updatedData.getLogoUrl() != null) existing.setLogoUrl(updatedData.getLogoUrl());
        if (updatedData.getRutPdfUrl() != null) existing.setRutPdfUrl(updatedData.getRutPdfUrl());

        LegalRepresentative newRep = updatedData.getLegalRepresentative();
        if (newRep != null) {
            if (existing.getLegalRepresentative() == null) {
                existing.setLegalRepresentative(new LegalRepresentative());
            }
            LegalRepresentative rep = existing.getLegalRepresentative();
            if (newRep.getNameRepresentative() != null) rep.setNameRepresentative(newRep.getNameRepresentative());
            if (newRep.getLastNameRepresentative() != null) rep.setLastNameRepresentative(newRep.getLastNameRepresentative());
        }

        return companyRepositoryPort.saveCompany(existing);
    }
}
