package co.edu.ucundinamarca.backendudecprac.application.usecase;

import co.edu.ucundinamarca.backendudecprac.domain.model.Company;
import co.edu.ucundinamarca.backendudecprac.domain.model.Notification;
import co.edu.ucundinamarca.backendudecprac.domain.model.User;
import co.edu.ucundinamarca.backendudecprac.domain.port.in.CreateNotificationUseCase;
import co.edu.ucundinamarca.backendudecprac.domain.port.in.VerifyCompanyUseCase;
import co.edu.ucundinamarca.backendudecprac.domain.port.out.CompanyRepositoryPort;
import co.edu.ucundinamarca.backendudecprac.domain.port.out.EmailRepositoryPort;
import co.edu.ucundinamarca.backendudecprac.domain.port.out.UserRepositoryPort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VerifyCompanyUseCaseImpl implements VerifyCompanyUseCase {
    private final CompanyRepositoryPort companyRepositoryPort;
    private final UserRepositoryPort userRepositoryPort;
    private final EmailRepositoryPort emailRepositoryPort;
    private final CreateNotificationUseCase createNotificationUseCase;

    public VerifyCompanyUseCaseImpl(CompanyRepositoryPort companyRepositoryPort, UserRepositoryPort userRepositoryPort, EmailRepositoryPort emailRepositoryPort, CreateNotificationUseCase createNotificationUseCase) {
        this.companyRepositoryPort = companyRepositoryPort;
        this.userRepositoryPort = userRepositoryPort;
        this.emailRepositoryPort = emailRepositoryPort;
        this.createNotificationUseCase = createNotificationUseCase;
    }

    @Override
    public List<Company> getCompaniesPending() {
        return companyRepositoryPort.findByVerificationStatus("PENDIENTE");
    }

    @Override
    public void verifyCompany(String nit, boolean isApproved) {
        Company company = companyRepositoryPort.findByNit(nit)
                .orElseThrow(() -> new IllegalArgumentException("Empresa no encontrada con el NIT: " + nit));
        User user = userRepositoryPort.findById(company.getIdUser())
                .orElseThrow(() -> new IllegalArgumentException("Usuario asociado no encontrado"));

        String correoEmpresa = user.getEmailAddres();
        if (isApproved) {
            company.setVerificationStatus("APROBADA");
            user.setUserStatus(true);

            Notification notification = new Notification();
            notification.setIdUser(company.getIdUser());
            notification.setTypeNotification("SISTEMA_APROBACION");
            notification.setMessage("¡Felicidades! Tu cuenta de empresa ha sido aprobada por la Universidad de Cundinamarca.");
            createNotificationUseCase.createNotification(notification);

            emailRepositoryPort.sendEmailverification(
                    correoEmpresa,
                    "¡Hola, <b>" + company.getCompanyName() + "</b>!<br><br>" +
                            "Tu cuenta corporativa en <b>UdecPrac</b> ha sido aprobada por el administrador de la Universidad de Cundinamarca. " +
                            "Ya puedes iniciar sesión y publicar tus ofertas de práctica."
            );
        } else {
            company.setVerificationStatus("RECHAZADA");
            user.setUserStatus(false);
            emailRepositoryPort.sendEmailverification(
                    correoEmpresa,
                    "Hola, <b>" + company.getCompanyName() + "</b>.<br><br>" +
                            "Te informamos que tu solicitud de registro en UdecPrac no fue aprobada tras el proceso de verificación."
            );
        }
        companyRepositoryPort.saveCompany(company);
        userRepositoryPort.saveUser(user);
    }
}
