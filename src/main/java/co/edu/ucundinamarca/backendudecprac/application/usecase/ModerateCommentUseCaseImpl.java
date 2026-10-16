package co.edu.ucundinamarca.backendudecprac.application.usecase;

import co.edu.ucundinamarca.backendudecprac.domain.model.Comment;
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
        return commentPort.findByValidationStatus("PENDIENTE");
    }

    @Transactional
    @Override
    public Comment moderateComment(Long idComment, boolean isApproved) {
        Comment comment = commentPort.findById(idComment)
                .orElseThrow(() -> new IllegalArgumentException("Comentario no encontrado."));

        comment.setValidationStatus(isApproved ? "APROBADO" : "RECHAZADO");
        if (isApproved) {
            comment.setAcceptanceComment(LocalDateTime.now());
        }
        return commentPort.saveComment(comment);
    }
}
