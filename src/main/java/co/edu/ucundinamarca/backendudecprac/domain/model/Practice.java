package co.edu.ucundinamarca.backendudecprac.domain.model;

import co.edu.ucundinamarca.backendudecprac.domain.exceptions.InvalidInputException;
import co.edu.ucundinamarca.backendudecprac.domain.exceptions.InvalidStateTransitionException;
import co.edu.ucundinamarca.backendudecprac.domain.model.status.PracticeStatus;

import java.time.LocalDate;

public class Practice {
    private Long idPractice;
    private Long idPostulation;
    private PracticeStatus practiceStatus;
    private LocalDate startDate;
    private LocalDate endDate;
    private String companyFeedback;
    private String studentEvaluation;

    // ------------------------- Comportamiento de dominio -------------------------

    /** Al aceptar una postulación, la práctica queda EN_CURSO. */
    public static Practice startFor(Long idPostulation) {
        Practice practice = new Practice();
        practice.idPostulation = idPostulation;
        practice.practiceStatus = PracticeStatus.EN_CURSO;
        return practice;
    }

    /** Actualiza fechas y/o estado; los valores nulos no se modifican. */
    public void update(LocalDate newStart, LocalDate newEnd, PracticeStatus newStatus) {
        LocalDate start = newStart != null ? newStart : startDate;
        LocalDate end = newEnd != null ? newEnd : endDate;
        if (start != null && end != null && end.isBefore(start)) {
            throw new InvalidInputException("La fecha de fin no puede ser anterior a la fecha de inicio.");
        }
        if (newStatus != null && newStatus != practiceStatus) {
            if (practiceStatus == null || !practiceStatus.canTransitionTo(newStatus)) {
                throw new InvalidStateTransitionException("la práctica", practiceStatus, newStatus);
            }
            this.practiceStatus = newStatus;
        }
        this.startDate = start;
        this.endDate = end;
    }

    /** Solo se puede calificar a la empresa cuando la práctica terminó. */
    public boolean isFinished() {
        return practiceStatus == PracticeStatus.FINALIZADA;
    }

    // ------------------------- Getters / setters -------------------------

    public Long getIdPractice() {
        return idPractice;
    }

    public void setIdPractice(Long idPractice) {
        this.idPractice = idPractice;
    }

    public Long getIdPostulation() {
        return idPostulation;
    }

    public void setIdPostulation(Long idPostulation) {
        this.idPostulation = idPostulation;
    }

    public PracticeStatus getPracticeStatus() {
        return practiceStatus;
    }

    public void setPracticeStatus(PracticeStatus practiceStatus) {
        this.practiceStatus = practiceStatus;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public String getCompanyFeedback() {
        return companyFeedback;
    }

    public void setCompanyFeedback(String companyFeedback) {
        this.companyFeedback = companyFeedback;
    }

    public String getStudentEvaluation() {
        return studentEvaluation;
    }

    public void setStudentEvaluation(String studentEvaluation) {
        this.studentEvaluation = studentEvaluation;
    }
}
