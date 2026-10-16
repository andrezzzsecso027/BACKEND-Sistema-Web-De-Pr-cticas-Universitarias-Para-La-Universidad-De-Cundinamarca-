package co.edu.ucundinamarca.backendudecprac.application.usecase;

import co.edu.ucundinamarca.backendudecprac.domain.model.Offer;
import co.edu.ucundinamarca.backendudecprac.domain.port.in.ClosedOfferByAdminUseCase;
import co.edu.ucundinamarca.backendudecprac.domain.port.out.OfferRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CloseOfferByAdminUseCaseImpl implements ClosedOfferByAdminUseCase {
    private final OfferRepositoryPort  offerRepositoryPort;

    public CloseOfferByAdminUseCaseImpl(OfferRepositoryPort offerRepositoryPort) {
        this.offerRepositoryPort = offerRepositoryPort;
    }

    @Transactional
    @Override
    public void closeOfferByAdmin(Long idOffer) {
        Offer offer = offerRepositoryPort.findById(idOffer);
        if (offer == null) {
            throw new RuntimeException("Oferta no encontrada con ID: " + idOffer);
        }
        offer.setOfferStatus(false);
        offerRepositoryPort.saveOffer(offer);
    }

}
