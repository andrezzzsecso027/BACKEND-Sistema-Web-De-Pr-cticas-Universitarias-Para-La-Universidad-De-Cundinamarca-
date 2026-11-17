package co.edu.ucundinamarca.backendudecprac.domain.model;

import co.edu.ucundinamarca.backendudecprac.domain.exceptions.InvalidInputException;
import co.edu.ucundinamarca.backendudecprac.domain.exceptions.InvalidStateTransitionException;
import co.edu.ucundinamarca.backendudecprac.domain.model.status.PostulationStatus;

import java.time.LocalDateTime;

public class Postulation {
    private Long idPostulation;
    private Long idOffer;
    private String documentNumberStudent;
    private PostulationStatus statusPostulation;
    private LocalDateTime datePostulation;
    private LocalDateTime dateUdpate;
    private String observations;

    public Postulation() {}

    // ------------------------- Comportamiento de dominio -------------------------

    /** Una postulación nueva siempre nace en POSTULADA (en revisión por la empresa). */
    public void submit(LocalDateTime now) {
        if (idOffer == null || documentNumberStudent == null || documentNumberStudent.isBlank()) {
            throw new InvalidInputException("La oferta y el documento del estudiante son obligatorios.");
        }
        this.statusPostulation = PostulationStatus.POSTULADA;
        this.datePostulation = now;
        this.dateUdpate = now;
        this.observations = null;
    }

    /**
     * Cambia el estado respetando el ciclo de vida (solo POSTULADA -> ACEPTADA | RECHAZADA).
     * @return el estado anterior, para registrarlo en el historial.
     */
    public PostulationStatus changeStatus(PostulationStatus newStatus, String observations, LocalDateTime now) {
        if (newStatus == null) {
            throw new InvalidInputException("El nuevo estado de la postulación es obligatorio.");
        }
        if (statusPostulation == null || !statusPostulation.canTransitionTo(newStatus)) {
            throw new InvalidStateTransitionException("la postulación", statusPostulation, newStatus);
        }
        PostulationStatus previous = this.statusPostulation;
        this.statusPostulation = newStatus;
        this.observations = observations;
        this.dateUdpate = now;
        return previous;
    }

    public boolean isAccepted() {
        return statusPostulation == PostulationStatus.ACEPTADA;
    }

    // ------------------------- Getters / setters -------------------------

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

    public PostulationStatus getStatusPostulation() {
        return statusPostulation;
    }

    public void setStatusPostulation(PostulationStatus statusPostulation) {
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
