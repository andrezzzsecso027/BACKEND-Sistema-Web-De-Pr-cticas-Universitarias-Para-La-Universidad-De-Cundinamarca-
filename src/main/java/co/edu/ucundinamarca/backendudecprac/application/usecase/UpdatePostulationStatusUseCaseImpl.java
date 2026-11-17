package co.edu.ucundinamarca.backendudecprac.application.usecase;

import co.edu.ucundinamarca.backendudecprac.domain.exceptions.ResourceNotFoundException;
import co.edu.ucundinamarca.backendudecprac.domain.model.*;
import co.edu.ucundinamarca.backendudecprac.domain.model.status.PostulationStatus;
import co.edu.ucundinamarca.backendudecprac.domain.port.in.UpdatePostulationStatusUseCase;
import co.edu.ucundinamarca.backendudecprac.domain.port.out.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class UpdatePostulationStatusUseCaseImpl implements UpdatePostulationStatusUseCase {

    private static final Logger log = LoggerFactory.getLogger(UpdatePostulationStatusUseCaseImpl.class);

    private final PostulationRepositoryPort postulationPort;
    private final PostulationHistoryRepositoryPort historyPort;
    private final PracticeRepositoryPort practicePort;
    private final OfferRepositoryPort offerPort;
    private final StudentRepositoryPort studentPort;
    private final UserRepositoryPort userPort;
    private final EmailSenderPort emailPort;

    public UpdatePostulationStatusUseCaseImpl(PostulationRepositoryPort postulationPort, PostulationHistoryRepositoryPort historyPort, PracticeRepositoryPort practicePort, OfferRepositoryPort offerPort, StudentRepositoryPort studentPort, UserRepositoryPort userPort, EmailSenderPort emailPort) {
        this.postulationPort = postulationPort;
        this.historyPort = historyPort;
        this.practicePort = practicePort;
        this.offerPort = offerPort;
        this.studentPort = studentPort;
        this.userPort = userPort;
        this.emailPort = emailPort;
    }

    @Transactional
    @Override
    public Postulation updateStatus(Long idPostulation, PostulationStatus newStatus, String observations) {
        Postulation postulation = postulationPort.findById(idPostulation);
        if (postulation == null) {
            throw new ResourceNotFoundException("La postulación no existe.");
        }

        LocalDateTime now = LocalDateTime.now();
        // El dominio valida la transición (solo POSTULADA -> ACEPTADA | RECHAZADA)
        PostulationStatus estadoAnterior = postulation.changeStatus(newStatus, observations, now);

        Offer oferta = null;
        if (postulation.isAccepted()) {
            oferta = offerPort.findById(postulation.getIdOffer());
            if (oferta == null) {
                throw new ResourceNotFoundException("La oferta asociada no existe.");
            }
            oferta.reserveSpot(); // lanza BusinessRuleException si no hay cupos; cierra la oferta al llegar a 0
            offerPort.saveOffer(oferta);
            practicePort.savePractice(Practice.startFor(idPostulation));
        }

        Postulation postulationActualizada = postulationPort.savePostulation(postulation);

        PostulationHistory historial = new PostulationHistory();
        historial.setIdPostulation(idPostulation);
        historial.setPreviousStatus(estadoAnterior);
        historial.setNewStatus(newStatus);
        historial.setChangeDate(now);
        historyPort.saveHistory(historial);

        if (oferta != null) {
            notificarAceptacion(postulation, oferta, observations);
        }
        return postulationActualizada;
    }

    /**
     * El correo es una notificación "best effort": si Brevo falla, la aceptación NO se revierte
     * (antes una caída del proveedor de correo deshacía toda la transacción).
     */
    private void notificarAceptacion(Postulation postulation, Offer oferta, String observations) {
        Student student = studentPort.findByDocumento(postulation.getDocumentNumberStudent());
        if (student == null || student.getIdUser() == null) {
            return;
        }
        userPort.findById(student.getIdUser()).ifPresent(user -> {
            String mensajeCorreo = "¡Felicidades, <b>" + student.getName() + "</b>! Has sido <b>ACEPTADO</b> en la oferta de práctica: <b>"
                    + oferta.getPracticeName() + "</b>.<br><br>"
                    + (observations != null && !observations.isBlank()
                    ? "<b>Observaciones de la empresa:</b> " + observations
                    : "Ingresa a UdecPrac para consultar los detalles de tu vinculación.");
            try {
                emailPort.sendEmail(user.getEmailAddres(), mensajeCorreo);
            } catch (RuntimeException e) {
                log.warn("No se pudo enviar el correo de aceptación de la postulación {}: {}", postulation.getIdPostulation(), e.getMessage());
            }
        });
    }
}
