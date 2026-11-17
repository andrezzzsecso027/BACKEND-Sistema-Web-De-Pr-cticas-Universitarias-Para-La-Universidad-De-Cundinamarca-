package co.edu.ucundinamarca.backendudecprac.application.usecase;

import co.edu.ucundinamarca.backendudecprac.domain.exceptions.BusinessRuleException;
import co.edu.ucundinamarca.backendudecprac.domain.exceptions.ResourceNotFoundException;
import co.edu.ucundinamarca.backendudecprac.domain.model.Offer;
import co.edu.ucundinamarca.backendudecprac.domain.model.Postulation;
import co.edu.ucundinamarca.backendudecprac.domain.port.in.CreatePostulationUseCase;
import co.edu.ucundinamarca.backendudecprac.domain.port.out.OfferRepositoryPort;
import co.edu.ucundinamarca.backendudecprac.domain.port.out.PostulationRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class PostulationUseCaseImpl implements CreatePostulationUseCase {

    private final PostulationRepositoryPort postulationRepositoryPort;
    private final OfferRepositoryPort offerRepositoryPort;

    public PostulationUseCaseImpl(PostulationRepositoryPort postulationRepositoryPort, OfferRepositoryPort offerRepositoryPort) {
        this.postulationRepositoryPort = postulationRepositoryPort;
        this.offerRepositoryPort = offerRepositoryPort;
    }

    @Transactional
    @Override
    public Postulation createPostulation(Postulation postulation) {
        if (postulationRepositoryPort.existsByStudentAndOffer(postulation.getIdOffer(), postulation.getDocumentNumberStudent())) {
            throw new BusinessRuleException("Ya te encuentras postulado a esta oferta.");
        }

        Offer oferta = offerRepositoryPort.findById(postulation.getIdOffer());
        if (oferta == null) {
            throw new ResourceNotFoundException("La oferta a la que intentas aplicar no existe.");
        }
        if (!oferta.isOpenForApplications()) {
            throw new BusinessRuleException("Lo sentimos, esta oferta ya fue cerrada o no tiene cupos disponibles.");
        }
        if (postulationRepositoryPort.hasActivePracticeOrAcceptedPostulation(postulation.getDocumentNumberStudent())) {
            throw new BusinessRuleException("No puedes postularte a nuevas ofertas porque ya has sido seleccionado en una práctica activa.");
        }

        postulation.submit(LocalDateTime.now()); // nace en POSTULADA
        return postulationRepositoryPort.savePostulation(postulation);
    }
}
