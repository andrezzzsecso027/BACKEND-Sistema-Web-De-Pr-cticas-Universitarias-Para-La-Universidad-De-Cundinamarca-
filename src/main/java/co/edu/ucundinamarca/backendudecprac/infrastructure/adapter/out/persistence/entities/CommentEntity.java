package co.edu.ucundinamarca.backendudecprac.infrastructure.adapter.out.persistence.entities;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "comentarios")
public class CommentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_comentario", nullable = false, unique = true)
    private Long idComment;

    @Column(name = "id_practica", nullable = false, unique = true)
    private Long idPractice;

    @Column(name = "comentario", nullable = false, columnDefinition = "TEXT")
    private String commentText;

    @Column(name = "estado_validado", nullable = false, length = 20)
    private String validationStatus;

    @Column(name = "calificacion_empresa", nullable = false)
    private Double companyRating;

    @Column(name = "fecha_comentario", nullable = false)
    private LocalDateTime commentDate;

    @Column(name = "fecha_aceptacion_comentario", nullable = true)
    private LocalDateTime acceptanceComment;

    public CommentEntity() {
    }

    public Long getIdComment() {
        return idComment;
    }

    public void setIdComment(Long idComment) {
        this.idComment = idComment;
    }

    public Long getIdPractice() {
        return idPractice;
    }

    public void setIdPractice(Long idPractice) {
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
