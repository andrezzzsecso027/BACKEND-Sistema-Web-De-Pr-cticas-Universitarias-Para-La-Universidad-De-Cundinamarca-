package co.edu.ucundinamarca.backendudecprac.infrastructure.adapter.out.persistence.entities;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name="practicas")
public class PracticeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="ID_practica")
    private Long idPractice;

    @Column(name="ID_postulacion", nullable = false, unique = true) // Unique porque es 1:1 según tu diagrama
    private Long idPostulation;

    @Column(name="estado_practica", nullable = false)
    private String practiceStatus;

    @Column(name="fecha_inicio")
    private LocalDate startDate;

    @Column(name="fecha_fin")
    private LocalDate endDate;

    @Column(name="Retroalimentacion_empresa", columnDefinition = "TEXT")
    private String companyFeedback;

    @Column(name="evaluacion_estudiante", columnDefinition = "TEXT")
    private String studentEvaluation;

    public Long getIdPractice() {
        return idPractice;
    }

    public void setIdPractice(Long idPractice) {
        this.idPractice = idPractice;
    }

    public String getPracticeStatus() {
        return practiceStatus;
    }

    public void setPracticeStatus(String practiceStatus) {
        this.practiceStatus = practiceStatus;
    }

    public Long getIdPostulation() {
        return idPostulation;
    }

    public void setIdPostulation(Long idPostulation) {
        this.idPostulation = idPostulation;
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
