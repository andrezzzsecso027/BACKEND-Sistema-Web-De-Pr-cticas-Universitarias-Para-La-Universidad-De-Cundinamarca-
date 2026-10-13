package co.edu.ucundinamarca.backendudecprac.infrastructure.adapter.out.persistence.Entities;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name="Historial_Postulacion")
public class PostulationHistoryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="ID_historial")
    private Long idHistory;

    @Column(name="ID_postulacion", nullable = false)
    private Long idPostulation;

    @Column(name="estado_anterior", nullable = false)
    private String previousStatus;

    @Column(name="estado_nuevo", nullable = false)
    private String newStatus;

    @Column(name="fecha_cambio", nullable = false)
    private LocalDateTime changeDate;

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

    public String getPreviousStatus() {
        return previousStatus;
    }

    public void setPreviousStatus(String previousStatus) {
        this.previousStatus = previousStatus;
    }

    public String getNewStatus() {
        return newStatus;
    }

    public void setNewStatus(String newStatus) {
        this.newStatus = newStatus;
    }

    public LocalDateTime getChangeDate() {
        return changeDate;
    }

    public void setChangeDate(LocalDateTime changeDate) {
        this.changeDate = changeDate;
    }
}
