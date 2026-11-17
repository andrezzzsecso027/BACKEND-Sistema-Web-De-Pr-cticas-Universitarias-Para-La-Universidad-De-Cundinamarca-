package co.edu.ucundinamarca.backendudecprac.infrastructure.adapter.out.persistence.adapters;

import co.edu.ucundinamarca.backendudecprac.domain.model.status.CommentStatus;
import co.edu.ucundinamarca.backendudecprac.domain.model.Comment;
import co.edu.ucundinamarca.backendudecprac.domain.port.out.CommentRepositoryPort;
import co.edu.ucundinamarca.backendudecprac.infrastructure.adapter.out.persistence.entities.CommentEntity;
import co.edu.ucundinamarca.backendudecprac.infrastructure.adapter.out.persistence.jpa.CommentJPArepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class CommentRepositoryAdapter implements CommentRepositoryPort {
    private final CommentJPArepository commentJPArepository;

    public CommentRepositoryAdapter(CommentJPArepository commentJPArepository) {
        this.commentJPArepository = commentJPArepository;
    }


    @Override
    public Comment saveComment(Comment comment) {
        CommentEntity entity;
        if (comment.getIdComment() != 0) {
            entity = commentJPArepository.findById(comment.getIdComment())
                    .orElse(new CommentEntity());
        } else {
            entity = new CommentEntity();
        }

        entity.setIdPractice(comment.getIdPractice());
        entity.setCommentText(comment.getCommentText());
        entity.setValidationStatus(comment.getValidationStatus() == null ? null : comment.getValidationStatus().name());
        entity.setCompanyRating(comment.getCompanyRating());
        entity.setCommentDate(comment.getCommentDate());
        entity.setAcceptanceComment(comment.getAcceptanceComment());

        CommentEntity saved = commentJPArepository.save(entity);
        return mapToDomain(saved);
    }

    @Override
    public Optional<Comment> findById(Long idComment) {
        return commentJPArepository.findById(idComment).map(this::mapToDomain);
    }

    @Override
    public boolean existsByIdPractice(Long idPractice) {
        return commentJPArepository.existsByIdPractice(idPractice);
    }

    @Override
    public List<Comment> findByValidationStatus(CommentStatus status) {
        return commentJPArepository.findByValidationStatusIgnoreCase(status.name())
                .stream()
                .map(this::mapToDomain).toList();
    }

    @Override
    public List<Comment> findApprovedByCompanyNit(String nitCompany) {
        return commentJPArepository.findApprovedCommentsByCompanyNit(nitCompany)
                .stream()
                .map(this::mapToDomain).toList();
    }

    private Comment mapToDomain(CommentEntity entity) {
        Comment comment = new Comment();
        comment.setIdComment(entity.getIdComment());
        comment.setIdPractice(entity.getIdPractice());
        comment.setCommentText(entity.getCommentText());
        comment.setValidationStatus(entity.getValidationStatus() == null ? null : CommentStatus.from(entity.getValidationStatus()));
        comment.setCompanyRating(entity.getCompanyRating());
        comment.setCommentDate(entity.getCommentDate());
        comment.setAcceptanceComment(entity.getAcceptanceComment());
        return comment;
    }
}
