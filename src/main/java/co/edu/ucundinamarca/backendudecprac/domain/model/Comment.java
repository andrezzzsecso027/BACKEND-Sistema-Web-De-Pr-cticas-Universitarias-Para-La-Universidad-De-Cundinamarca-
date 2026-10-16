package co.edu.ucundinamarca.backendudecprac.domain.model;

import java.time.LocalDateTime;

public class Comment {
    private long idComment;
    private long idPractice;
    private String commentText;
    private String validationStatus;
    private Double companyRating;
    private LocalDateTime commentDate;
    private LocalDateTime acceptanceComment;

    public Comment() {}

    public long getIdComment() {
        return idComment;
    }

    public void setIdComment(long idComment) {
        this.idComment = idComment;
    }

    public long getIdPractice() {
        return idPractice;
    }

    public void setIdPractice(long idPractice) {
        this.idPractice = idPractice;
    }

    public String getCommentText() {
        return commentText;
    }

    public void setCommentText(String commentText) {
        this.commentText = commentText;
    }

    public String getValidationStatus() {
        return validationStatus;
    }

    public void setValidationStatus(String validationStatus) {
        this.validationStatus = validationStatus;
    }

    public Double getCompanyRating() {
        return companyRating;
    }

    public void setCompanyRating(Double companyRating) {
        this.companyRating = companyRating;
    }

    public LocalDateTime getCommentDate() {
        return commentDate;
    }

    public void setCommentDate(LocalDateTime commentDate) {
        this.commentDate = commentDate;
    }

    public LocalDateTime getAcceptanceComment() {
        return acceptanceComment;
    }

    public void setAcceptanceComment(LocalDateTime acceptanceComment) {
        this.acceptanceComment = acceptanceComment;
    }
}
