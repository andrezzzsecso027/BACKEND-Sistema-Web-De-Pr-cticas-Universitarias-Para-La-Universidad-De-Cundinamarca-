package co.edu.ucundinamarca.backendudecprac.application.usecase;

import co.edu.ucundinamarca.backendudecprac.domain.model.Company;
import co.edu.ucundinamarca.backendudecprac.domain.model.Notification;
import co.edu.ucundinamarca.backendudecprac.domain.model.User;
import co.edu.ucundinamarca.backendudecprac.domain.port.in.AdminUseCase;
import co.edu.ucundinamarca.backendudecprac.domain.port.in.CreateNotificationUseCase;
import co.edu.ucundinamarca.backendudecprac.domain.port.out.CompanyRepositoryPort;
import co.edu.ucundinamarca.backendudecprac.domain.port.out.EmailRepositoryPort;
import co.edu.ucundinamarca.backendudecprac.domain.port.out.UserRepositoryPort;
import co.edu.ucundinamarca.backendudecprac.infrastructure.adapter.out.persistence.NotificationEntity;
import co.edu.ucundinamarca.backendudecprac.infrastructure.adapter.out.persistence.NotificationJPArepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AdminUseCaseImpl implements AdminUseCase {
    private final CompanyRepositoryPort companyRepositoryPort;
    private final UserRepositoryPort userRepositoryPort;
    private final EmailRepositoryPort emailRepositoryPort;
    private final CreateNotificationUseCase createNotificationUseCase;
    public AdminUseCaseImpl(CompanyRepositoryPort companyRepositoryPort, UserRepositoryPort userRepositoryPort, EmailRepositoryPort emailRepositoryPort, CreateNotificationUseCase createNotificationUseCase) {
        this.companyRepositoryPort = companyRepositoryPort;
        this.userRepositoryPort = userRepositoryPort;
        this.emailRepositoryPort = emailRepositoryPort;
        this.createNotificationUseCase = createNotificationUseCase;
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
            Notification notification = new Notification();
            notification.setIdUser(company.getIdUser());
            notification.setTypeNotification("SISTEMA_APROBACION");
            notification.setMessage("¡Felicidades! Tu cuenta de empresa ha sido aprobada por la Universidad de Cundinamarca.");
            createNotificationUseCase.createNotification(notification);
            String correoEmpresa = user.getEmailAddres();
            emailRepositoryPort.sendEmailverification(correoEmpresa, "Tu cuenta ha sido aprobada. Ya puedes publicar ofertas.");
        } else {
            company.setVerificationStatus("RECHAZADA");
            user.setUserStatus(false);
        }
        companyRepositoryPort.saveEmpresa(company);
        userRepositoryPort.saveUser(user);
    }


}
