package co.edu.ucundinamarca.backendudecprac.application.usecase;

import co.edu.ucundinamarca.backendudecprac.domain.model.*;
import co.edu.ucundinamarca.backendudecprac.domain.port.in.CreatePostulationUseCase;
import co.edu.ucundinamarca.backendudecprac.domain.port.in.GetPostulationsUseCase;
import co.edu.ucundinamarca.backendudecprac.domain.port.out.OfferRepositoryPort;
import co.edu.ucundinamarca.backendudecprac.domain.port.out.PostulationRepositoryPort;
import co.edu.ucundinamarca.backendudecprac.domain.port.out.StudentProfileRepositoryPort;
import co.edu.ucundinamarca.backendudecprac.domain.port.out.StudentRepositoryPort;
import co.edu.ucundinamarca.backendudecprac.infrastructure.adapter.in.web.DTOs.PostulationDetailResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PostulationUseCaseImpl implements CreatePostulationUseCase{
    private final PostulationRepositoryPort postulationRepositoryPort;
    private final OfferRepositoryPort  offerRepositoryPort;
    public PostulationUseCaseImpl(PostulationRepositoryPort postulationRepositoryPort, OfferRepositoryPort offerRepositoryPort) {
        this.postulationRepositoryPort = postulationRepositoryPort;
        this.offerRepositoryPort = offerRepositoryPort;
    }

    @Transactional
    @Override
    public Postulation createPostulation(Postulation postulation) {

        boolean yaPostulado = postulationRepositoryPort.existsByStudentAndOffer(
                postulation.getIdOffer(),
                String.valueOf(postulation.getDocumentNumberStudent())
        );
        if (yaPostulado) {
            throw new IllegalStateException("Ya te encuentras postulado a esta oferta.");
        }
        Offer oferta = offerRepositoryPort.findById(postulation.getIdOffer());
        if (oferta == null) {
            throw new IllegalArgumentException("La oferta a la que intentas aplicar no existe.");
        }
        if (!oferta.getOfferStatus() || oferta.getAvailableSpots() <= 0) {
            throw new IllegalStateException("Lo sentimos, esta oferta ya fue cerrada o no tiene cupos disponibles.");
        }
        if (postulationRepositoryPort.hasActivePracticeOrAcceptedPostulation(postulation.getDocumentNumberStudent())) {
            throw new RuntimeException("No puedes postularte a nuevas ofertas porque ya has sido seleccionado en una práctica activa.");
        }
        postulation.setStatusPostulation("POSTULADA"); // Estado inicial
        postulation.setDatePostulation(LocalDateTime.now());
        postulation.setDateUdpate(LocalDateTime.now());
        postulation.setObservations(null);
        return postulationRepositoryPort.savePostulation(postulation);
    }

}
