package co.edu.ucundinamarca.backendudecprac.infrastructure.adapter.out.persistence.Adapters;

import co.edu.ucundinamarca.backendudecprac.domain.model.Offer;
import co.edu.ucundinamarca.backendudecprac.domain.port.out.OfferRepositoryPort;
import co.edu.ucundinamarca.backendudecprac.infrastructure.adapter.out.persistence.Entities.OfferEntity;
import co.edu.ucundinamarca.backendudecprac.infrastructure.adapter.out.persistence.JPA.OfferJPArepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

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

    @Override
    public List<Offer> getActiveOffers() {
        List<OfferEntity> entidades = offerJPArepository.findByOfferStatusTrue();
        return entidades.stream().map(entity -> {
            Offer offer = new Offer();
            offer.setIdOffer(entity.getIdOffer());
            offer.setPracticeName(entity.getNameOffer());
            offer.setNitCompanyAssociated(entity.getNitCompany());
            offer.setPracticeDescription(entity.getDescriptionOffer());
            offer.setProfession(entity.getProfesionOffer());
            offer.setSalary(entity.getSalaryOffer());
            offer.setModality(entity.getModalityOffer());
            offer.setLocation(entity.getUbicationOffer());
            offer.setOfferStatus(entity.getOfferStatus());
            offer.setPublicationDate(entity.getPublicationDate());
            offer.setAvailableSpots(entity.getNumberOfCups());
            offer.setClosingDate(entity.getClosingDate());
            return offer;
        }).collect(Collectors.toList());

    }


}
