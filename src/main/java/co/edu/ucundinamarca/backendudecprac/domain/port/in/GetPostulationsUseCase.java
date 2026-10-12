package co.edu.ucundinamarca.backendudecprac.domain.port.in;

import co.edu.ucundinamarca.backendudecprac.domain.model.Postulation;
import co.edu.ucundinamarca.backendudecprac.infrastructure.adapter.in.web.DTOs.PostulationDetailResponse;

import java.util.List;

public interface GetPostulationsUseCase {
    List<PostulationDetailResponse> getPostulationsByOfferId(Long idOffer);
}
