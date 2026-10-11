package co.edu.ucundinamarca.backendudecprac.domain.port.out;
import co.edu.ucundinamarca.backendudecprac.domain.model.Offer;

public interface OfferRepositoryPort {
    Offer saveOffer(Offer offer);
}
