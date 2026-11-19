package co.edu.ucundinamarca.backendudecprac.application.usecase;

import co.edu.ucundinamarca.backendudecprac.domain.exceptions.InvalidInputException;
import co.edu.ucundinamarca.backendudecprac.domain.exceptions.ResourceAlreadyExistsException;
import co.edu.ucundinamarca.backendudecprac.domain.model.Company;
import co.edu.ucundinamarca.backendudecprac.domain.model.User;
import co.edu.ucundinamarca.backendudecprac.domain.port.in.CreateCompanyUseCase;
import co.edu.ucundinamarca.backendudecprac.domain.port.out.CompanyRepositoryPort;
import co.edu.ucundinamarca.backendudecprac.domain.port.out.EmailSenderPort;
import co.edu.ucundinamarca.backendudecprac.domain.port.out.UserRepositoryPort;
import co.edu.ucundinamarca.backendudecprac.domain.port.out.PasswordEncoderPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;


@Service
public class CreateCompanyUseCaseImpl implements CreateCompanyUseCase {

    private static final Logger log = LoggerFactory.getLogger(CreateCompanyUseCaseImpl.class);

    private final CompanyRepositoryPort empresarepositoryPort;
    private final UserRepositoryPort usuariorepositoryport;
    private final PasswordEncoderPort passwordEncoder;
    private final EmailSenderPort emailSenderPort;

    public CreateCompanyUseCaseImpl(CompanyRepositoryPort empresarepositoryPort, UserRepositoryPort usuariorepositoryport,
                                    PasswordEncoderPort passwordEncoder, EmailSenderPort emailSenderPort) {
        this.empresarepositoryPort = empresarepositoryPort;
        this.usuariorepositoryport = usuariorepositoryport;
        this.passwordEncoder = passwordEncoder;
        this.emailSenderPort = emailSenderPort;
    }
    @Transactional
    @Override
    public Company createCompany(User user, Company company) {
        // El certificado RUT/NIT es obligatorio: sin él la Coordinación no puede verificar a la empresa
        if (!company.hasCertificadoNit()) {
            throw new InvalidInputException("Debes adjuntar el certificado RUT / NIT en PDF para registrar la empresa.");
        }
        if(usuariorepositoryport.existsByCorreoElectronico(user.getEmailAddres())) {
            throw new ResourceAlreadyExistsException("Ya existe una cuenta con ese correo");
        }
        if(empresarepositoryPort.existsByNit(company.getNit())){
            throw new ResourceAlreadyExistsException("Ya existe una cuenta con ese nit");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setUserStatus(false);
        user.setCreationDate(LocalDateTime.now());
        User userGuardado = usuariorepositoryport.saveUser(user);
        company.setIdUser(userGuardado.getIdUser());
        // createCompany (no saveCompany): si otro registro simultáneo ganó la carrera por este NIT,
        // falla con 409 y el @Transactional deshace el usuario recién creado (sin huérfanos)
        Company creada = empresarepositoryPort.createCompany(company);
        notificarSolicitudRecibida(userGuardado.getEmailAddres(), creada.getCompanyName());
        return creada;
    }

    /** Correo "best effort": si Brevo falla, el registro ya quedó hecho y la empresa ve el aviso en pantalla. */
    private void notificarSolicitudRecibida(String correo, String nombreEmpresa) {
        String nombre = nombreEmpresa == null || nombreEmpresa.isBlank() ? "tu empresa" : "<b>" + nombreEmpresa + "</b>";
        String mensaje = "¡Hola, " + nombre + "!<br><br>"
                + "Recibimos tu solicitud de vinculación a <b>UdecPrac</b>. La Coordinación de la Universidad de Cundinamarca "
                + "revisará tu certificado RUT/NIT y te avisaremos por este medio cuando tu cuenta sea aprobada. "
                + "Mientras tanto no podrás iniciar sesión.";
        try {
            emailSenderPort.sendEmail(correo, mensaje);
        } catch (RuntimeException e) {
            log.warn("No se pudo enviar el correo de solicitud recibida a {}: {}", correo, e.getMessage());
        }
    }



}
