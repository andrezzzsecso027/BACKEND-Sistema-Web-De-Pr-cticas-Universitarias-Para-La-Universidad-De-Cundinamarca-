package co.edu.ucundinamarca.backendudecprac.domain.port.out;
import co.edu.ucundinamarca.backendudecprac.domain.model.Offer;

import java.util.List;

public interface OfferRepositoryPort {
    Offer saveOffer(Offer offer);
    List<Offer> getActiveOffers();
    Offer findById(Long idOffer);
    List<Offer> findAllOffers();
}
