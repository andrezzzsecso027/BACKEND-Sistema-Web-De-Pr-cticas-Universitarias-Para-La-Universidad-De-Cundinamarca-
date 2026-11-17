package co.edu.ucundinamarca.backendudecprac.application.usecase;

import co.edu.ucundinamarca.backendudecprac.domain.exceptions.ResourceNotFoundException;
import co.edu.ucundinamarca.backendudecprac.domain.model.Comment;
import co.edu.ucundinamarca.backendudecprac.domain.model.status.CommentStatus;
import co.edu.ucundinamarca.backendudecprac.domain.port.in.ModerateCommentUseCase;
import co.edu.ucundinamarca.backendudecprac.domain.port.out.CommentRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ModerateCommentUseCaseImpl implements ModerateCommentUseCase {

    private final CommentRepositoryPort commentPort;

    public ModerateCommentUseCaseImpl(CommentRepositoryPort commentPort) {
        this.commentPort = commentPort;
    }

    @Override
    public List<Comment> getPendingCommentsForAdmin() {
        return commentPort.findByValidationStatus(CommentStatus.PENDIENTE);
    }

    @Transactional
    @Override
    public Comment moderateComment(Long idComment, boolean isApproved) {
        Comment comment = commentPort.findById(idComment)
                .orElseThrow(() -> new ResourceNotFoundException("Comentario no encontrado."));
        comment.moderate(isApproved, LocalDateTime.now()); // solo se modera un comentario PENDIENTE
        return commentPort.saveComment(comment);
    }
}
