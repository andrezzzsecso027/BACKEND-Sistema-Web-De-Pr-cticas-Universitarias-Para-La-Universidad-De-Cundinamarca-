package co.edu.ucundinamarca.backendudecprac.domain.port.in;

import co.edu.ucundinamarca.backendudecprac.domain.model.Postulation;

public interface CreatePostulationUseCase {
    Postulation createPostulation(Postulation postulation);
}
