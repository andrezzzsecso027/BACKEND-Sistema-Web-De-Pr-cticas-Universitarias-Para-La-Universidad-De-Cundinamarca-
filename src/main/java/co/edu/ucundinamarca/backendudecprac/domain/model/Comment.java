package co.edu.ucundinamarca.backendudecprac.domain.model;

import co.edu.ucundinamarca.backendudecprac.domain.exceptions.InvalidStateTransitionException;
import co.edu.ucundinamarca.backendudecprac.domain.model.status.CommentStatus;

import java.time.LocalDateTime;

public class Comment {
    private long idComment;
    private long idPractice;
    private String commentText;
    private CommentStatus validationStatus;
    private Double companyRating;
    private LocalDateTime commentDate;
    private LocalDateTime acceptanceComment;

    public Comment() {
        // Constructor vacío requerido por Jackson y por los mapeadores de persistencia
    }

    // ------------------------- Comportamiento de dominio -------------------------

    /** Cada comentario nace PENDIENTE de moderación por el administrador. */
    public void submit(LocalDateTime now) {
        this.validationStatus = CommentStatus.PENDIENTE;
        this.commentDate = now;
    }

    /** Solo se puede moderar un comentario PENDIENTE. */
    public void moderate(boolean approved, LocalDateTime now) {
        CommentStatus target = approved ? CommentStatus.APROBADO : CommentStatus.RECHAZADO;
        if (validationStatus == null || !validationStatus.canTransitionTo(target)) {
            throw new InvalidStateTransitionException("el comentario", validationStatus, target);
        }
        this.validationStatus = target;
        if (approved) {
            this.acceptanceComment = now;
        }
    }

    // ------------------------- Getters / setters -------------------------

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

    public CommentStatus getValidationStatus() {
        return validationStatus;
    }

    public void setValidationStatus(CommentStatus validationStatus) {
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
