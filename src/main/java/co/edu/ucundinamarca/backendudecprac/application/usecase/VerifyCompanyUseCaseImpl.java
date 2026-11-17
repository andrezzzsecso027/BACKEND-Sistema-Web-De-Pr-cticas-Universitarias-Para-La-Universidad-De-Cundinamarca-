package co.edu.ucundinamarca.backendudecprac.application.usecase;

import co.edu.ucundinamarca.backendudecprac.domain.exceptions.ResourceNotFoundException;
import co.edu.ucundinamarca.backendudecprac.domain.model.Company;
import co.edu.ucundinamarca.backendudecprac.domain.model.Notification;
import co.edu.ucundinamarca.backendudecprac.domain.model.User;
import co.edu.ucundinamarca.backendudecprac.domain.model.status.CompanyVerificationStatus;
import co.edu.ucundinamarca.backendudecprac.domain.port.in.CreateNotificationUseCase;
import co.edu.ucundinamarca.backendudecprac.domain.port.in.VerifyCompanyUseCase;
import co.edu.ucundinamarca.backendudecprac.domain.port.out.CompanyRepositoryPort;
import co.edu.ucundinamarca.backendudecprac.domain.port.out.EmailSenderPort;
import co.edu.ucundinamarca.backendudecprac.domain.port.out.UserRepositoryPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class VerifyCompanyUseCaseImpl implements VerifyCompanyUseCase {

    private static final Logger log = LoggerFactory.getLogger(VerifyCompanyUseCaseImpl.class);

    private final CompanyRepositoryPort companyRepositoryPort;
    private final UserRepositoryPort userRepositoryPort;
    private final EmailSenderPort emailSenderPort;
    private final CreateNotificationUseCase createNotificationUseCase;

    public VerifyCompanyUseCaseImpl(CompanyRepositoryPort companyRepositoryPort, UserRepositoryPort userRepositoryPort, EmailSenderPort emailSenderPort, CreateNotificationUseCase createNotificationUseCase) {
        this.companyRepositoryPort = companyRepositoryPort;
        this.userRepositoryPort = userRepositoryPort;
        this.emailSenderPort = emailSenderPort;
        this.createNotificationUseCase = createNotificationUseCase;
    }

    @Override
    public List<Company> getCompaniesPending() {
        return companyRepositoryPort.findByVerificationStatus(CompanyVerificationStatus.PENDIENTE);
    }

    @Transactional
    @Override
    public void verifyCompany(String nit, boolean isApproved) {
        Company company = companyRepositoryPort.findByNit(nit)
                .orElseThrow(() -> new ResourceNotFoundException("Empresa no encontrada con el NIT: " + nit));
        User user = userRepositoryPort.findById(company.getIdUser())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario asociado no encontrado"));

        String mensaje;
        if (isApproved) {
            company.approve();
            user.setUserStatus(true);

            Notification notification = new Notification();
            notification.setIdUser(company.getIdUser());
            notification.setTypeNotification("SISTEMA_APROBACION");
            notification.setMessage("¡Felicidades! Tu cuenta de empresa ha sido aprobada por la Universidad de Cundinamarca.");
            createNotificationUseCase.createNotification(notification);

            mensaje = "¡Hola, <b>" + company.getCompanyName() + "</b>!<br><br>"
                    + "Tu cuenta corporativa en <b>UdecPrac</b> ha sido aprobada por el administrador de la Universidad de Cundinamarca. "
                    + "Ya puedes iniciar sesión y publicar tus ofertas de práctica.";
        } else {
            company.reject();
            user.setUserStatus(false);
            mensaje = "Hola, <b>" + company.getCompanyName() + "</b>.<br><br>"
                    + "Te informamos que tu solicitud de registro en UdecPrac no fue aprobada tras el proceso de verificación.";
        }

        companyRepositoryPort.saveCompany(company);
        userRepositoryPort.saveUser(user);

        // Notificación "best effort": si el correo falla, la verificación ya quedó registrada
        try {
            emailSenderPort.sendEmail(user.getEmailAddres(), mensaje);
        } catch (RuntimeException e) {
            log.warn("No se pudo enviar el correo de verificación a la empresa {}: {}", nit, e.getMessage());
        }
    }
}
