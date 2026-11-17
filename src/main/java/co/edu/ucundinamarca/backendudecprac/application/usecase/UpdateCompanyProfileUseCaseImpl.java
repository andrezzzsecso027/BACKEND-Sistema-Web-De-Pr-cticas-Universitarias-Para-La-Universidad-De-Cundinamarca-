package co.edu.ucundinamarca.backendudecprac.application.usecase;

import java.util.function.Consumer;

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

        siPresente(updatedData.getCompanyName(), existing::setCompanyName);
        siPresente(updatedData.getLegalName(), existing::setLegalName);
        siPresente(updatedData.getCompanyType(), existing::setCompanyType);
        siPresente(updatedData.getCompanyPhoneNumber(), existing::setCompanyPhoneNumber);
        siPresente(updatedData.getDepartment(), existing::setDepartment);
        siPresente(updatedData.getCity(), existing::setCity);
        siPresente(updatedData.getCompanyAddress(), existing::setCompanyAddress);
        siPresente(updatedData.getCompanyDescription(), existing::setCompanyDescription);
        siPresente(updatedData.getLogoUrl(), existing::setLogoUrl);
        siPresente(updatedData.getRutPdfUrl(), existing::setRutPdfUrl);

        LegalRepresentative newRep = updatedData.getLegalRepresentative();
        if (newRep != null) {
            if (existing.getLegalRepresentative() == null) {
                existing.setLegalRepresentative(new LegalRepresentative());
            }
            LegalRepresentative rep = existing.getLegalRepresentative();
            siPresente(newRep.getNameRepresentative(), rep::setNameRepresentative);
            siPresente(newRep.getLastNameRepresentative(), rep::setLastNameRepresentative);
        }

        return companyRepositoryPort.saveCompany(existing);
    }

    /** Aplica el valor solo si llegó informado (actualización parcial del perfil). */
    private static <T> void siPresente(T valor, Consumer<T> setter) {
        if (valor != null) {
            setter.accept(valor);
        }
    }
}
