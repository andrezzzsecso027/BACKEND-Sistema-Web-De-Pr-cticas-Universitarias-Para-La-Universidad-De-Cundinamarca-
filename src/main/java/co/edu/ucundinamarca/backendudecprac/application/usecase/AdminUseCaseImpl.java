package co.edu.ucundinamarca.backendudecprac.application.usecase;

import co.edu.ucundinamarca.backendudecprac.domain.model.*;
import co.edu.ucundinamarca.backendudecprac.domain.port.in.AdminUseCase;
import co.edu.ucundinamarca.backendudecprac.domain.port.in.CreateNotificationUseCase;
import co.edu.ucundinamarca.backendudecprac.domain.port.out.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AdminUseCaseImpl implements AdminUseCase {
    private final CompanyRepositoryPort companyRepositoryPort;
    private final UserRepositoryPort userRepositoryPort;
    private final EmailRepositoryPort emailRepositoryPort;
    private final CreateNotificationUseCase createNotificationUseCase;
    private final OfferRepositoryPort offerRepositoryPort;
    private final StudentRepositoryPort studentRepositoryPort;
    public AdminUseCaseImpl(CompanyRepositoryPort companyRepositoryPort, UserRepositoryPort userRepositoryPort, EmailRepositoryPort emailRepositoryPort, CreateNotificationUseCase createNotificationUseCase, OfferRepositoryPort offerRepositoryPort, StudentRepositoryPort studentRepositoryPort) {
        this.companyRepositoryPort = companyRepositoryPort;
        this.userRepositoryPort = userRepositoryPort;
        this.emailRepositoryPort = emailRepositoryPort;
        this.createNotificationUseCase = createNotificationUseCase;
        this.offerRepositoryPort = offerRepositoryPort;
        this.studentRepositoryPort = studentRepositoryPort;
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
        companyRepositoryPort.saveEmpresa(company);
        userRepositoryPort.saveUser(user);
    }

    @Override
    public List<Student> getAllStudents() {
        return studentRepositoryPort.findAllStudents();
    }

    @Override
    public List<Company> getAllCompanies() {
        return companyRepositoryPort.findAllCompanies();
    }

    @Override
    public List<Offer> getAllOffers() {
        return offerRepositoryPort.findAllOffers();
    }

    @Transactional
    @Override
    public void closeOfferByAdmin(Long idOffer) {
        Offer offer = offerRepositoryPort.findById(idOffer);
        if (offer == null) {
            throw new RuntimeException("Oferta no encontrada con ID: " + idOffer);
        }
        offer.setOfferStatus(false);
        offerRepositoryPort.saveOffer(offer);
    }


}
