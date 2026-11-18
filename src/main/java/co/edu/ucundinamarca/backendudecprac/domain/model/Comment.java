package co.edu.ucundinamarca.backendudecprac.domain.model;

import co.edu.ucundinamarca.backendudecprac.domain.exceptions.InvalidInputException;
import co.edu.ucundinamarca.backendudecprac.domain.exceptions.InvalidStateTransitionException;
import co.edu.ucundinamarca.backendudecprac.domain.model.status.CommentStatus;

import java.time.LocalDateTime;

public class Comment {
    public static final int MIN_RATING = 1;
    public static final int MAX_RATING = 5;
    public static final int MAX_TEXT_LENGTH = 1000;

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

    /**
     * Cada comentario nace PENDIENTE de moderación por el administrador.
     * La calificación es obligatoria (1 a 5 estrellas) y el texto no puede ir vacío.
     */
    public void submit(LocalDateTime now) {
        if (companyRating == null || companyRating < MIN_RATING || companyRating > MAX_RATING) {
            throw new InvalidInputException("La calificación debe estar entre " + MIN_RATING + " y " + MAX_RATING + " estrellas.");
        }
        if (commentText == null || commentText.isBlank()) {
            throw new InvalidInputException("Escribe un comentario sobre tu experiencia en la empresa.");
        }
        if (commentText.length() > MAX_TEXT_LENGTH) {
            throw new InvalidInputException("El comentario no puede superar los " + MAX_TEXT_LENGTH + " caracteres.");
        }
        this.commentText = commentText.trim();
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
