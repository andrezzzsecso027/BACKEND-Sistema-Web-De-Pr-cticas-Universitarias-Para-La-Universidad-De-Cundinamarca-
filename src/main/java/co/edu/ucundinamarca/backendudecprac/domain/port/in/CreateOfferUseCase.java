package co.edu.ucundinamarca.backendudecprac.domain.port.in;

import co.edu.ucundinamarca.backendudecprac.domain.model.Company;
import co.edu.ucundinamarca.backendudecprac.domain.model.Offer;

public interface CreateOfferUseCase {
    Offer createOffer(Offer offer);

}
