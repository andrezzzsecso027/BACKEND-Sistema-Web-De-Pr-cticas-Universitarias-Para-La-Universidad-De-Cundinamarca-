package co.edu.ucundinamarca.backendudecprac.domain.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class Postulation {
    private Long idPostulation;
    private Long idOffer;
    private String documentNumberStudent;
    private String statusPostulation;
    private LocalDateTime datePostulation;
    private LocalDateTime dateUdpate;
    private String observations;

    public Postulation() {}

    public Long getIdPostulation() {
        return idPostulation;
    }

    public void setIdPostulation(Long idPostulation) {
        this.idPostulation = idPostulation;
    }

    public Long getIdOffer() {
        return idOffer;
    }

    public void setIdOffer(Long idOffer) {
        this.idOffer = idOffer;
    }

    public String getDocumentNumberStudent() {
        return documentNumberStudent;
    }

    public void setDocumentNumberStudent(String documentNumberStudent) {
        this.documentNumberStudent = documentNumberStudent;
    }

    public String getStatusPostulation() {
        return statusPostulation;
    }

    public void setStatusPostulation(String statusPostulation) {
        this.statusPostulation = statusPostulation;
    }

    public LocalDateTime getDatePostulation() {
        return datePostulation;
    }

    public void setDatePostulation(LocalDateTime datePostulation) {
        this.datePostulation = datePostulation;
    }

    public LocalDateTime getDateUdpate() {
        return dateUdpate;
    }

    public void setDateUdpate(LocalDateTime dateUdpate) {
        this.dateUdpate = dateUdpate;
    }

    public String getObservations() {
        return observations;
    }

    public void setObservations(String observations) {
        this.observations = observations;
    }
}
