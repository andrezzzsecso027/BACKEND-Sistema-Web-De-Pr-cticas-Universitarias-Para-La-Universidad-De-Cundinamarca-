package co.edu.ucundinamarca.backendudecprac.domain.port.in;

import java.util.Map;

public interface AiRecommendationOfferUseCase {
    Map<String, Object> recommendOffersForStudent(String studentDocument);
}
