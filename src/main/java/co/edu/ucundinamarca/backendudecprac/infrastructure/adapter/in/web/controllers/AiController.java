package co.edu.ucundinamarca.backendudecprac.infrastructure.adapter.in.web.controllers;


import co.edu.ucundinamarca.backendudecprac.domain.port.in.AiRecommendationCandidatesUseCase;
import co.edu.ucundinamarca.backendudecprac.domain.port.in.AiRecommendationOfferUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/ia")
public class AiController {
    private final AiRecommendationOfferUseCase aiRecommendationOfferUseCase;
    private final AiRecommendationCandidatesUseCase aiRecommendationCandidatesUseCase;

    public AiController(AiRecommendationOfferUseCase aiRecommendationOfferUseCase, AiRecommendationCandidatesUseCase aiRecommendationCandidatesUseCase) {
        this.aiRecommendationOfferUseCase = aiRecommendationOfferUseCase;
        this.aiRecommendationCandidatesUseCase = aiRecommendationCandidatesUseCase;
    }

    @GetMapping("/recomendar-ofertas/{documento}")
    public ResponseEntity<Object> recomendarOfertasEstudiante(@PathVariable String documento) {
        return ResponseEntity.ok(aiRecommendationOfferUseCase.recommendOffersForStudent(documento));
    }

    @GetMapping("/ranking-postulados/{idOffer}")
    public ResponseEntity<Object> generarRankingPostulados(@PathVariable Long idOffer) {
        try {
            return ResponseEntity.ok(aiRecommendationCandidatesUseCase.rankCandidatesForOffer(idOffer));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }
}
