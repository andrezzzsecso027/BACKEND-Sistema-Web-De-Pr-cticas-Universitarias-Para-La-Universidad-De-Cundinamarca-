package co.edu.ucundinamarca.backendudecprac.application.usecase;

import co.edu.ucundinamarca.backendudecprac.domain.model.Company;
import co.edu.ucundinamarca.backendudecprac.domain.model.Offer;
import co.edu.ucundinamarca.backendudecprac.domain.port.in.CreateOfferUseCase;
import co.edu.ucundinamarca.backendudecprac.domain.port.out.CompanyRepositoryPort;
import co.edu.ucundinamarca.backendudecprac.domain.port.out.OfferRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
public class OfferUseCaseImpl implements CreateOfferUseCase {
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
                .orElseThrow(() -> new IllegalArgumentException("La empresa con NIT " + offer.getNitCompanyAssociated() + " no existe."));
        if (!"APROBADA".equalsIgnoreCase(company.getVerificationStatus())) {
            throw new IllegalStateException("No puedes publicar ofertas porque tu cuenta de empresa aún no ha sido aprobada por la Universidad.");
        }
        if (offer.getClosingDate() != null && offer.getClosingDate().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("La fecha de cierre no puede ser en el pasado.");
        }

        if (offer.getAvailableSpots() == null || offer.getAvailableSpots() <= 0) {
            throw new IllegalArgumentException("La oferta debe tener al menos 1 cupo disponible.");
        }
        offer.setPublicationDate(LocalDate.now());
        offer.setOfferStatus(true);
        return offerRepositoryPort.saveOffer(offer);
    }
}
