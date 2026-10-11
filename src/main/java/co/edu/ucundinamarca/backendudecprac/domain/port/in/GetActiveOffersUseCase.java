package co.edu.ucundinamarca.backendudecprac.domain.port.in;

import co.edu.ucundinamarca.backendudecprac.domain.model.Offer;

import java.util.List;

public interface GetActiveOffersUseCase {
    List<Offer> getActiveOffers();
}
