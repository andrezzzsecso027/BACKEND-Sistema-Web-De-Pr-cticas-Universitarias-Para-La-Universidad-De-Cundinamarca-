package co.edu.ucundinamarca.backendudecprac.application.usecase;

import co.edu.ucundinamarca.backendudecprac.domain.model.Company;
import co.edu.ucundinamarca.backendudecprac.domain.model.User;
import co.edu.ucundinamarca.backendudecprac.domain.port.in.AdminUseCase;
import co.edu.ucundinamarca.backendudecprac.domain.port.out.CompanyRepositoryPort;
import co.edu.ucundinamarca.backendudecprac.domain.port.out.UserRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class AdminUseCaseImpl implements AdminUseCase {
    private final CompanyRepositoryPort companyRepositoryPort;
    private final UserRepositoryPort userRepositoryPort;

    public AdminUseCaseImpl(CompanyRepositoryPort companyRepositoryPort, UserRepositoryPort userRepositoryPort) {
        this.companyRepositoryPort = companyRepositoryPort;
        this.userRepositoryPort = userRepositoryPort;
    }

    @Override
    public List<Company> getCompaniesPending() {
        return companyRepositoryPort.findByVerificationStatus("PENDIENTE");
    }

    @Transactional
    @Override
    public void verifyCompany(String nit, boolean isApproved) {
        Company company = companyRepositoryPort.findByNit(nit)
                .orElseThrow(() -> new RuntimeException("Empresa no encontrada con el NIT: " + nit));
        User user = userRepositoryPort.findById(company.getIdUser())
                .orElseThrow(() -> new RuntimeException("Usuario asociado no encontrado"));

        if (isApproved) {
            company.setVerificationStatus("APROBADA");
            user.setUserStatus(true);
        } else {
            company.setVerificationStatus("RECHAZADA");
            user.setUserStatus(false);
        }
        companyRepositoryPort.saveEmpresa(company);
        userRepositoryPort.saveUser(user);
    }


}
