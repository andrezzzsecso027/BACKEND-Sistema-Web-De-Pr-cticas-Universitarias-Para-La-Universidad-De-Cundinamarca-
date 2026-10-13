package co.edu.ucundinamarca.backendudecprac.infrastructure.adapter.in.web.DTOs;

import java.time.LocalDate;

public class UpdatePracticeRequest {
    private LocalDate startDate;
    private LocalDate endDate;

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

    private String practiceStatus;
}
