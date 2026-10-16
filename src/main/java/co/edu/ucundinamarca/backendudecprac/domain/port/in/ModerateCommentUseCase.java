package co.edu.ucundinamarca.backendudecprac.domain.port.in;

import co.edu.ucundinamarca.backendudecprac.domain.model.Comment;

import java.util.List;

public interface ModerateCommentUseCase {
    List<Comment> getPendingCommentsForAdmin();
    Comment moderateComment(Long idComment, boolean isApproved);
}
