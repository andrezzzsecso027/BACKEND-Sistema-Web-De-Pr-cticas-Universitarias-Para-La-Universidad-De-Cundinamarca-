package co.edu.ucundinamarca.backendudecprac.infrastructure.adapter.in.web.DTOs;

import java.math.BigDecimal;
import java.time.LocalDate;

public class OfferRequest {

    private String practiceName;

    private String nitCompanyAssociated;

    private String practiceDescription;

    private String profession;

    private BigDecimal salary;

    private String modality;

    private String location;

    private Integer availableSpots;

    private LocalDate closingDate;

    public String getPracticeName() {
        return practiceName;
    }

    public void setPracticeName(String practiceName) {
        this.practiceName = practiceName;
    }

    public String getNitCompanyAssociated() {
        return nitCompanyAssociated;
    }

    public void setNitCompanyAssociated(String nitCompanyAssociated) {
        this.nitCompanyAssociated = nitCompanyAssociated;
    }

    public String getPracticeDescription() {
        return practiceDescription;
    }

    public void setPracticeDescription(String practiceDescription) {
        this.practiceDescription = practiceDescription;
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

    public BigDecimal getSalary() {
        return salary;
    }

    public void setSalary(BigDecimal salary) {
        this.salary = salary;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
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
