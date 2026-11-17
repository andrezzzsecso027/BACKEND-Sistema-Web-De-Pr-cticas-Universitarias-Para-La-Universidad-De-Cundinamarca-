package co.edu.ucundinamarca.backendudecprac.application.usecase;

import co.edu.ucundinamarca.backendudecprac.domain.exceptions.BusinessRuleException;
import co.edu.ucundinamarca.backendudecprac.domain.exceptions.ResourceNotFoundException;
import co.edu.ucundinamarca.backendudecprac.domain.model.Company;
import co.edu.ucundinamarca.backendudecprac.domain.model.Offer;
import co.edu.ucundinamarca.backendudecprac.domain.port.in.CreateOfferUseCase;
import co.edu.ucundinamarca.backendudecprac.domain.port.in.GetActiveOffersUseCase;
import co.edu.ucundinamarca.backendudecprac.domain.port.out.CompanyRepositoryPort;
import co.edu.ucundinamarca.backendudecprac.domain.port.out.OfferRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class OfferUseCaseImpl implements CreateOfferUseCase, GetActiveOffersUseCase {

    private final OfferRepositoryPort offerRepositoryPort;
    private final CompanyRepositoryPort companyRepositoryPort;

    public OfferUseCaseImpl(OfferRepositoryPort offerRepositoryPort, CompanyRepositoryPort companyRepositoryPort) {
        this.offerRepositoryPort = offerRepositoryPort;
        this.companyRepositoryPort = companyRepositoryPort;
    }

    @Transactional
    @Override
    public Offer createOffer(Offer offer) {
        Company company = companyRepositoryPort.findByNit(offer.getNitCompanyAssociated())
                .orElseThrow(() -> new ResourceNotFoundException("La empresa con NIT " + offer.getNitCompanyAssociated() + " no existe."));

        if (!company.isApproved()) {
            throw new BusinessRuleException("No puedes publicar ofertas porque tu cuenta de empresa aún no ha sido aprobada por la Universidad.");
        }

        offer.publish(LocalDate.now()); // valida cupos y fecha de cierre
        return offerRepositoryPort.saveOffer(offer);
    }

    @Override
    public List<Offer> getActiveOffers() {
        return offerRepositoryPort.getActiveOffers();
    }
}
