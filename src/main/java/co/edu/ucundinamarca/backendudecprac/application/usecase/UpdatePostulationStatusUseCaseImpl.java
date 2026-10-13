package co.edu.ucundinamarca.backendudecprac.application.usecase;

import co.edu.ucundinamarca.backendudecprac.domain.model.Postulation;
import co.edu.ucundinamarca.backendudecprac.domain.model.PostulationHistory;
import co.edu.ucundinamarca.backendudecprac.domain.model.Practice;
import co.edu.ucundinamarca.backendudecprac.domain.port.in.UpdatePostulationStatusUseCase;
import co.edu.ucundinamarca.backendudecprac.domain.port.out.PostulationHistoryRepositoryPort;
import co.edu.ucundinamarca.backendudecprac.domain.port.out.PostulationRepositoryPort;
import co.edu.ucundinamarca.backendudecprac.domain.port.out.PracticeRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class UpdatePostulationStatusUseCaseImpl implements UpdatePostulationStatusUseCase {
    private final PostulationRepositoryPort postulationPort;
    private final PostulationHistoryRepositoryPort historyPort;
    private final PracticeRepositoryPort practicePort;

    public UpdatePostulationStatusUseCaseImpl(PostulationRepositoryPort postulationPort, PostulationHistoryRepositoryPort historyPort, PracticeRepositoryPort practicePort) {
        this.postulationPort = postulationPort;
        this.historyPort = historyPort;
        this.practicePort = practicePort;
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
            Practice practica = new Practice();
            practica.setIdPostulation(idPostulation);
            practica.setPracticeStatus("ASIGNADA");

            practicePort.savePractice(practica);
        }
        return postulationActualizada;
    }
}
