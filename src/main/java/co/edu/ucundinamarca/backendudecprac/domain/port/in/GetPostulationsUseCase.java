package co.edu.ucundinamarca.backendudecprac.domain.port.in;

import co.edu.ucundinamarca.backendudecprac.domain.model.Postulation;

import java.util.List;

public interface GetPostulationsUseCase {
    List<Postulation> getPostulationsByOfferId(Long idOffer);
}
