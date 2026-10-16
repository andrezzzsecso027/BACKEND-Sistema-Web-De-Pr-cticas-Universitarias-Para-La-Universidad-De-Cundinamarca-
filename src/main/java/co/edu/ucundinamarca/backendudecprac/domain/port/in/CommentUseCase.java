package co.edu.ucundinamarca.backendudecprac.domain.port.in;

import co.edu.ucundinamarca.backendudecprac.domain.model.Comment;

import java.util.List;

public interface CommentUseCase {
    Comment createComment(Comment comment);
    List<Comment> getApprovedCommentsByCompany(String nitCompany);
}
