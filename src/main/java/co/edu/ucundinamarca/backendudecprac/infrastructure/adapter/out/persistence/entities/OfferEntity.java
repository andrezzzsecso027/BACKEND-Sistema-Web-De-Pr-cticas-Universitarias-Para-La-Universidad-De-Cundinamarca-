package co.edu.ucundinamarca.backendudecprac.infrastructure.adapter.out.persistence.entities;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name="ofertas")
public class OfferEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idOffer;

    @Column(name="nombre_Offer", nullable=false)
    private String nameOffer;

    @Column(name="nit_empresa",nullable = false)
    private String nitCompany;

    @Column(name="descripcion_oferta",nullable = false)
    private String descriptionOffer;

    @Column(name="profesion",nullable = false)
    private String profesionOffer;

    @Column(name="salario",nullable = false)
    private BigDecimal salaryOffer;

    @Column(name="modalidad",nullable = false)
    private String modalityOffer;

    @Column(name="ubicacion",nullable = false)
    private String ubicationOffer;

    @Column(name="estado_oferta",nullable = false)
    private Boolean offerStatus;

    @Column(name="fecha_publicacion",nullable = false)
    private LocalDate publicationDate;

    @Column(name="cupos",nullable = false)
    private Integer numberOfCups;

    @Column(name="fecha_cierre",nullable = false)
    private LocalDate closingDate;

    public Long getIdOffer() {
        return idOffer;
    }

    public void setIdOffer(Long idOffer) {
        this.idOffer = idOffer;
    }

    public String getNameOffer() {
        return nameOffer;
    }

    public void setNameOffer(String nameOffer) {
        this.nameOffer = nameOffer;
    }

    public String getNitCompany() {
        return nitCompany;
    }

    public void setNitCompany(String nitCompany) {
        this.nitCompany = nitCompany;
    }

    public String getDescriptionOffer() {
        return descriptionOffer;
    }

    public void setDescriptionOffer(String descriptionOffer) {
        this.descriptionOffer = descriptionOffer;
    }

    public String getProfesionOffer() {
        return profesionOffer;
    }

    public void setProfesionOffer(String profesionOffer) {
        this.profesionOffer = profesionOffer;
    }

    public BigDecimal getSalaryOffer() {
        return salaryOffer;
    }

    public void setSalaryOffer(BigDecimal salaryOffer) {
        this.salaryOffer = salaryOffer;
    }

    public String getModalityOffer() {
        return modalityOffer;
    }

    public void setModalityOffer(String modalityOffer) {
        this.modalityOffer = modalityOffer;
    }

    public String getUbicationOffer() {
        return ubicationOffer;
    }

    public void setUbicationOffer(String ubicationOffer) {
        this.ubicationOffer = ubicationOffer;
    }

    public Boolean getOfferStatus() {
        return offerStatus;
    }

    public void setOfferStatus(Boolean offerStatus) {
        this.offerStatus = offerStatus;
    }

    public LocalDate getPublicationDate() {
        return publicationDate;
    }

    public void setPublicationDate(LocalDate publicationDate) {
        this.publicationDate = publicationDate;
    }

    public Integer getNumberOfCups() {
        return numberOfCups;
    }

    public void setNumberOfCups(Integer numberOfCups) {
        this.numberOfCups = numberOfCups;
    }

    public LocalDate getClosingDate() {
        return closingDate;
    }

    public void setClosingDate(LocalDate closingDate) {
        this.closingDate = closingDate;
    }
}
