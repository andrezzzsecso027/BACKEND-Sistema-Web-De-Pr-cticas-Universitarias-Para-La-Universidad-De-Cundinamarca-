package co.edu.ucundinamarca.backendudecprac.domain.port.in;

import co.edu.ucundinamarca.backendudecprac.domain.model.PostulationDetail;

import java.util.List;

public interface GetPostulationsUseCase {
    List<PostulationDetail> getPostulationsByOfferId(Long idOffer);
}
