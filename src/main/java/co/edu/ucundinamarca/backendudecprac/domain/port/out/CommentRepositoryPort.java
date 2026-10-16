package co.edu.ucundinamarca.backendudecprac.domain.port.out;

import co.edu.ucundinamarca.backendudecprac.domain.model.Comment;

import java.util.List;
import java.util.Optional;

public interface CommentRepositoryPort {
    Comment saveComment(Comment comment);
    Optional<Comment> findById(Long idComment);
    boolean existsByIdPractice(Long idPractice);
    List<Comment> findByValidationStatus(String status);
    List<Comment> findApprovedByCompanyNit(String nitCompany);
}
