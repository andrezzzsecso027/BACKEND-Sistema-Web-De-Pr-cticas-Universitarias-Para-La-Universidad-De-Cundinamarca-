package co.edu.ucundinamarca.backendudecprac.domain.port.out;

import co.edu.ucundinamarca.backendudecprac.domain.model.status.CompanyVerificationStatus;
import co.edu.ucundinamarca.backendudecprac.domain.model.Company;

import java.util.List;
import java.util.Optional;

public interface CompanyRepositoryPort {
    /**
     * Inserta una empresa NUEVA. Nunca sobrescribe: si el NIT ya existe (incluso si otro registro
     * concurrente lo acaba de guardar) lanza ResourceAlreadyExistsException.
     */
    Company createCompany(Company company);

    /** Actualiza (o inserta) una empresa existente; usar solo para modificaciones. */
    Company saveCompany(Company company);
    List<Company> findAllCompanies();
    boolean existsByNit(String nit);
    List<Company> findByVerificationStatus(CompanyVerificationStatus verificationStatus);
    Optional<Company> findByNit(String nit);
}
