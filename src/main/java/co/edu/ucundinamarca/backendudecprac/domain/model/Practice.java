package co.edu.ucundinamarca.backendudecprac.domain.model;

import java.time.LocalDate;

public class Practice {
    private Long idPractice;
    private Long idPostulation;
    private String practiceStatus;
    private LocalDate startDate;
    private LocalDate endDate;
    private String companyFeedback;
    private String studentEvaluation;

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

    public String getPracticeStatus() {
        return practiceStatus;
    }

    public void setPracticeStatus(String practiceStatus) {
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
