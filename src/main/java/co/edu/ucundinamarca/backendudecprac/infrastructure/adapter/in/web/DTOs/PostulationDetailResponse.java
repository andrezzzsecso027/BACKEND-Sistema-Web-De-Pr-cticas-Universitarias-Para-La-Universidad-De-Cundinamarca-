package co.edu.ucundinamarca.backendudecprac.infrastructure.adapter.in.web.DTOs;

import java.time.LocalDateTime;

public class PostulationDetailResponse {
    private Long idPostulation;
    private LocalDateTime datePostulation;
    private String statusPostulation;


    private String documentStudent;
    private String studentFullName;
    private String studentEmail;


    private String skills;
    private String englishLevel;
    private String lifesheet;
    private String githubUrl;

    public Long getIdPostulation() {
        return idPostulation;
    }

    public void setIdPostulation(Long idPostulation) {
        this.idPostulation = idPostulation;
    }

    public LocalDateTime getDatePostulation() {
        return datePostulation;
    }

    public void setDatePostulation(LocalDateTime datePostulation) {
        this.datePostulation = datePostulation;
    }

    public String getStatusPostulation() {
        return statusPostulation;
    }

    public void setStatusPostulation(String statusPostulation) {
        this.statusPostulation = statusPostulation;
    }

    public String getDocumentStudent() {
        return documentStudent;
    }

    public void setDocumentStudent(String documentStudent) {
        this.documentStudent = documentStudent;
    }

    public String getStudentFullName() {
        return studentFullName;
    }

    public void setStudentFullName(String studentFullName) {
        this.studentFullName = studentFullName;
    }

    public String getStudentEmail() {
        return studentEmail;
    }

    public void setStudentEmail(String studentEmail) {
        this.studentEmail = studentEmail;
    }

    public String getSkills() {
        return skills;
    }

    public void setSkills(String skills) {
        this.skills = skills;
    }

    public String getEnglishLevel() {
        return englishLevel;
    }

    public void setEnglishLevel(String englishLevel) {
        this.englishLevel = englishLevel;
    }

    public String getLifesheet() {
        return lifesheet;
    }

    public void setLifesheet(String lifesheet) {
        this.lifesheet = lifesheet;
    }

    public String getGithubUrl() {
        return githubUrl;
    }

    public void setGithubUrl(String githubUrl) {
        this.githubUrl = githubUrl;
    }
}
