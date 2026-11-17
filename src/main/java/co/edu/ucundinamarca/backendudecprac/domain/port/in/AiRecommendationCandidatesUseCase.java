package co.edu.ucundinamarca.backendudecprac.domain.port.in;

import java.util.Map;

public interface AiRecommendationCandidatesUseCase {
    Map<String, Object> rankCandidatesForOffer(Long idOffer);
}
