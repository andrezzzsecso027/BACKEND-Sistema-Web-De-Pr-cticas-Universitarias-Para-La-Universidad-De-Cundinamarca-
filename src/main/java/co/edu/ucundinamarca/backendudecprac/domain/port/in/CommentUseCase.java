package co.edu.ucundinamarca.backendudecprac.domain.port.in;

import co.edu.ucundinamarca.backendudecprac.domain.model.Comment;

import java.util.List;

public interface CommentUseCase {
    /**
     * El estudiante de la sesión califica a la empresa de una de SUS prácticas finalizadas.
     * @param studentEmail correo del token JWT; se usa para comprobar que la práctica le pertenece.
     */
    Comment createComment(Comment comment, String studentEmail);

    List<Comment> getApprovedCommentsByCompany(String nitCompany);
}
