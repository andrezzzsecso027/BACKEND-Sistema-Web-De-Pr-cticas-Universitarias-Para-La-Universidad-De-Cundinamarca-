package co.edu.ucundinamarca.backendudecprac.domain.model;

import co.edu.ucundinamarca.backendudecprac.domain.model.status.PostulationStatus;

import java.time.LocalDateTime;

public class PostulationHistory {
    private Long idHistory;
    private Long idPostulation;
    private PostulationStatus previousStatus;
    private PostulationStatus newStatus;
    private LocalDateTime changeDate;

    public PostulationHistory() {
        // Constructor vacío requerido por Jackson y por los mapeadores de persistencia
    }

    public Long getIdHistory() {
        return idHistory;
    }

    public void setIdHistory(Long idHistory) {
        this.idHistory = idHistory;
    }

    public Long getIdPostulation() {
        return idPostulation;
    }

    public void setIdPostulation(Long idPostulation) {
        this.idPostulation = idPostulation;
    }

    public PostulationStatus getPreviousStatus() {
        return previousStatus;
    }

    public void setPreviousStatus(PostulationStatus previousStatus) {
        this.previousStatus = previousStatus;
    }

    public PostulationStatus getNewStatus() {
        return newStatus;
    }

    public void setNewStatus(PostulationStatus newStatus) {
        this.newStatus = newStatus;
    }

    public LocalDateTime getChangeDate() {
        return changeDate;
    }

    public void setChangeDate(LocalDateTime changeDate) {
        this.changeDate = changeDate;
    }
}
