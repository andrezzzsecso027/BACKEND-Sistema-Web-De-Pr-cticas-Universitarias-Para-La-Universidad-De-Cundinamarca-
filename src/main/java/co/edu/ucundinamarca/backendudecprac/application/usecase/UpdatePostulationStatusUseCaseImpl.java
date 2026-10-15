package co.edu.ucundinamarca.backendudecprac.application.usecase;

import co.edu.ucundinamarca.backendudecprac.domain.model.*;
import co.edu.ucundinamarca.backendudecprac.domain.port.in.UpdatePostulationStatusUseCase;
import co.edu.ucundinamarca.backendudecprac.domain.port.out.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class UpdatePostulationStatusUseCaseImpl implements UpdatePostulationStatusUseCase {
    private final PostulationRepositoryPort postulationPort;
    private final PostulationHistoryRepositoryPort historyPort;
    private final PracticeRepositoryPort practicePort;
    private final OfferRepositoryPort offerPort;
    private final StudentRepositoryPort studentPort;
    private final UserRepositoryPort userPort;
    private final EmailRepositoryPort emailPort;
    public UpdatePostulationStatusUseCaseImpl(PostulationRepositoryPort postulationPort, PostulationHistoryRepositoryPort historyPort, PracticeRepositoryPort practicePort, OfferRepositoryPort offerPort, StudentRepositoryPort studentPort, UserRepositoryPort userPort, EmailRepositoryPort emailPort) {
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
    public Postulation updateStatus(Long idPostulation, String newStatus, String observations) {

        Postulation postulation = postulationPort.findById(idPostulation);
        if (postulation == null) {
            throw new IllegalArgumentException("La postulación no existe.");
        }
        String estadoAnterior = postulation.getStatusPostulation();
        postulation.setStatusPostulation(newStatus);
        postulation.setDateUdpate(LocalDateTime.now());
        postulation.setObservations(observations);

        Postulation postulationActualizada = postulationPort.savePostulation(postulation);

        PostulationHistory historial = new PostulationHistory();
        historial.setIdPostulation(idPostulation);
        historial.setPreviousStatus(estadoAnterior);
        historial.setNewStatus(newStatus);
        historial.setChangeDate(LocalDateTime.now());
        historyPort.saveHistory(historial);

        if ("ACEPTADA".equalsIgnoreCase(newStatus) && !"ACEPTADA".equalsIgnoreCase(estadoAnterior)) {
            Offer oferta = offerPort.findById(postulation.getIdOffer());
            if (oferta == null) {
                throw new IllegalArgumentException("La oferta asociada no existe.");
            }
            if (oferta.getAvailableSpots() <= 0) {
                throw new IllegalStateException("Esta oferta ya no tiene cupos disponibles.");
            }
            oferta.setAvailableSpots(oferta.getAvailableSpots() - 1);
            if (oferta.getAvailableSpots() <= 0) {
                oferta.setOfferStatus(false);
            }
            offerPort.saveOffer(oferta);

            Practice practica = new Practice();
            practica.setIdPostulation(idPostulation);
            practica.setPracticeStatus("ASIGNADA");
            practicePort.savePractice(practica);
            Student student = studentPort.findByDocumento(postulation.getDocumentNumberStudent());
            if (student != null && student.getIdUser() != null) {
                userPort.findById(student.getIdUser()).ifPresent(user -> {
                    String mensajeCorreo = "¡Felicidades, <b>" + student.getName() + "</b>! Has sido <b>ACEPTADO</b> en la oferta de práctica: <b>"
                            + oferta.getPracticeName() + "</b>.<br><br>"
                            + (observations != null && !observations.isBlank()
                            ? "<b>Observaciones de la empresa:</b> " + observations
                            : "Ingresa a UdecPrac para consultar los detalles de tu vinculación.");

                    emailPort.sendEmailverification(user.getEmailAddres(), mensajeCorreo);
                });
            }
        }
        return postulationActualizada;
    }
}
