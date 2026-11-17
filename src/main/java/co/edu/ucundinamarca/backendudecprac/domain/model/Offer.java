package co.edu.ucundinamarca.backendudecprac.domain.model;

import co.edu.ucundinamarca.backendudecprac.domain.exceptions.BusinessRuleException;
import co.edu.ucundinamarca.backendudecprac.domain.exceptions.InvalidInputException;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Offer {
    private Long idOffer;
    private String practiceName;
    private String nitCompanyAssociated;
    private String practiceDescription;
    private String profession;
    private BigDecimal salary;
    private String modality;
    private String location;
    private Boolean offerStatus=true;
    private LocalDate publicationDate;
    private Integer availableSpots;
    private LocalDate closingDate;

    public Offer() {}

    // ------------------------- Comportamiento de dominio -------------------------

    /** Publica la oferta validando cupos y fecha de cierre. */
    public void publish(LocalDate today) {
        if (availableSpots == null || availableSpots <= 0) {
            throw new InvalidInputException("La oferta debe tener al menos 1 cupo disponible.");
        }
        if (closingDate != null && closingDate.isBefore(today)) {
            throw new InvalidInputException("La fecha de cierre no puede ser en el pasado.");
        }
        this.publicationDate = today;
        this.offerStatus = true;
    }

    /** Un estudiante solo puede postularse a una oferta abierta y con cupos. */
    public boolean isOpenForApplications() {
        return Boolean.TRUE.equals(offerStatus) && availableSpots != null && availableSpots > 0;
    }

    /** Ocupa un cupo al aceptar a un estudiante; la oferta se cierra sola al llegar a 0. */
    public void reserveSpot() {
        if (availableSpots == null || availableSpots <= 0) {
            throw new BusinessRuleException("Esta oferta ya no tiene cupos disponibles.");
        }
        this.availableSpots--;
        if (availableSpots == 0) {
            this.offerStatus = false;
        }
    }

    public void close() {
        this.offerStatus = false;
    }

    // ------------------------- Getters / setters -------------------------

    public BigDecimal getSalary() {
        return salary;
    }

    public void setSalary(BigDecimal salary) {
        this.salary = salary;
    }

    public Long getIdOffer() {
        return idOffer;
    }

    public void setIdOffer(Long idOffer) {
        this.idOffer = idOffer;
    }

    public String getPracticeName() {
        return practiceName;
    }

    public void setPracticeName(String practiceName) {
        this.practiceName = practiceName;
    }

    public String getPracticeDescription() {
        return practiceDescription;
    }

    public void setPracticeDescription(String practiceDescription) {
        this.practiceDescription = practiceDescription;
    }

    public String getNitCompanyAssociated() {
        return nitCompanyAssociated;
    }

    public void setNitCompanyAssociated(String nitCompanyAssociated) {
        this.nitCompanyAssociated = nitCompanyAssociated;
    }

    public String getProfession() {
        return profession;
    }

    public void setProfession(String profession) {
        this.profession = profession;
    }

    public String getModality() {
        return modality;
    }

    public void setModality(String modality) {
        this.modality = modality;
    }

    public Boolean getOfferStatus() {
        return offerStatus;
    }

    public void setOfferStatus(Boolean offerStatus) {
        this.offerStatus = offerStatus;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public LocalDate getPublicationDate() {
        return publicationDate;
    }

    public void setPublicationDate(LocalDate publicationDate) {
        this.publicationDate = publicationDate;
    }

    public Integer getAvailableSpots() {
        return availableSpots;
    }

    public void setAvailableSpots(Integer availableSpots) {
        this.availableSpots = availableSpots;
    }

    public LocalDate getClosingDate() {
        return closingDate;
    }

    public void setClosingDate(LocalDate closingDate) {
        this.closingDate = closingDate;
    }
}
