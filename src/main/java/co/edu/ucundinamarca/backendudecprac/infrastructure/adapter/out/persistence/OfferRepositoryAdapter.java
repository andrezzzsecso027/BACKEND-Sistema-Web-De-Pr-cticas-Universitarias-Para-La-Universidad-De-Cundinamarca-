package co.edu.ucundinamarca.backendudecprac.infrastructure.adapter.out.persistence;

import co.edu.ucundinamarca.backendudecprac.domain.model.Offer;
import co.edu.ucundinamarca.backendudecprac.domain.port.out.OfferRepositoryPort;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OfferRepositoryAdapter implements OfferRepositoryPort {
    private final OfferJPArepository offerJPArepository;

    public OfferRepositoryAdapter(OfferJPArepository offerJPArepository) {
        this.offerJPArepository = offerJPArepository;
    }

    @Override
    public Offer saveOffer(Offer offer) {
        OfferEntity entity = new OfferEntity();

        if (offer.getIdOffer() != null && offer.getIdOffer() != 0) {
            entity.setIdOffer(offer.getIdOffer());
        }
        entity.setNameOffer(offer.getPracticeName());
        entity.setNitCompany(offer.getNitCompanyAssociated());
        entity.setDescriptionOffer(offer.getPracticeDescription());
        entity.setProfesionOffer(offer.getProfession());
        entity.setSalaryOffer(offer.getSalary());
        entity.setModalityOffer(offer.getModality());
        entity.setUbicationOffer(offer.getLocation());
        entity.setOfferStatus(offer.getOfferStatus());
        entity.setPublicationDate(offer.getPublicationDate());
        entity.setNumberOfCups(offer.getAvailableSpots());
        entity.setClosingDate(offer.getClosingDate());
        OfferEntity guardado = offerJPArepository.save(entity);
        offer.setOfferStatus(offer.getOfferStatus());
        offer.setIdOffer(guardado.getIdOffer());
        return offer;
    }


}
