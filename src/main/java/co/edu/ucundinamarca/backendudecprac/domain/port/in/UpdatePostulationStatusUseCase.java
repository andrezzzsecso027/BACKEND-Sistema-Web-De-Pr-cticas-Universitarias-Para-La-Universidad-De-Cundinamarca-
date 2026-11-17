package co.edu.ucundinamarca.backendudecprac.domain.port.in;

import co.edu.ucundinamarca.backendudecprac.domain.model.status.PostulationStatus;
import co.edu.ucundinamarca.backendudecprac.domain.model.Postulation;

public interface UpdatePostulationStatusUseCase {
    Postulation updateStatus(Long idPostulation, PostulationStatus newStatus, String observations);
}
