package co.edu.ucundinamarca.backendudecprac.infrastructure.adapter.in.web.DTOs;

public class CommentRequestDTO {
    private long idPractice;
    private String commentText;
    private Double companyRating;

    public CommentRequestDTO() {
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

    public Double getCompanyRating() {
        return companyRating;
    }

    public void setCompanyRating(Double companyRating) {
        this.companyRating = companyRating;
    }
}
