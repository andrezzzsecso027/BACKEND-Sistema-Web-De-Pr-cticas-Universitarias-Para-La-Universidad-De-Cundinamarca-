package co.edu.ucundinamarca.backendudecprac.application.usecase;

import co.edu.ucundinamarca.backendudecprac.domain.model.Comment;
import co.edu.ucundinamarca.backendudecprac.domain.model.Notification;
import co.edu.ucundinamarca.backendudecprac.domain.model.Practice;
import co.edu.ucundinamarca.backendudecprac.domain.model.User;
import co.edu.ucundinamarca.backendudecprac.domain.port.in.CommentUseCase;
import co.edu.ucundinamarca.backendudecprac.domain.port.in.CreateNotificationUseCase;
import co.edu.ucundinamarca.backendudecprac.domain.port.out.CommentRepositoryPort;
import co.edu.ucundinamarca.backendudecprac.domain.port.out.PracticeRepositoryPort;
import co.edu.ucundinamarca.backendudecprac.domain.port.out.UserRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CommentUseCaseImpl implements CommentUseCase {
    private final CommentRepositoryPort commentPort;
    private final PracticeRepositoryPort practicePort;
    private final CreateNotificationUseCase notificationUseCase;
    private final UserRepositoryPort userPort;
    public CommentUseCaseImpl(CommentRepositoryPort commentPort, PracticeRepositoryPort practicePort, CreateNotificationUseCase notificationUseCase, UserRepositoryPort userPort) {
        this.commentPort = commentPort;
        this.practicePort = practicePort;
        this.notificationUseCase = notificationUseCase;
        this.userPort = userPort;
    }

    @Transactional
    @Override
    public Comment createComment(Comment comment) {
        Practice practice = practicePort.findById(comment.getIdPractice());

        if (practice == null) {
            throw new IllegalArgumentException("La práctica asociada no existe.");
        }

        // Criterio 1: Validar que la práctica esté FINALIZADA
        if (!"FINALIZADA".equalsIgnoreCase(practice.getPracticeStatus())) {
            throw new IllegalStateException("Solo puedes calificar a la empresa cuando tu práctica esté en estado FINALIZADA.");
        }
        // Relación 1:1 en el MER: Solo un comentario por práctica
        if (commentPort.existsByIdPractice(comment.getIdPractice())) {
            throw new IllegalStateException("Ya registraste una calificación para esta práctica.");
        }

        // Criterio 2: Nace en estado 'PENDIENTE'
        comment.setValidationStatus("PENDIENTE");
        comment.setCommentDate(LocalDateTime.now());
        Comment saved = commentPort.saveComment(comment);

        // Criterio 3: Notificar al administrador
        List<User> administradores = userPort.findByRole("administrador"); // O "ADMIN", según como lo guardes en BD
        for (User admin : administradores) {
            Notification notification = new Notification();
            notification.setIdUser(admin.getIdUser()); // <-- Toma el ID real de la BD
            notification.setTypeNotification("MODERACION_COMENTARIO");
            notification.setMessage("Nuevo comentario pendiente de moderación para la práctica #" + comment.getIdPractice());
            notificationUseCase.createNotification(notification);
        }

        return saved;
    }

    @Override
    public List<Comment> getApprovedCommentsByCompany(String nitCompany) {
        return commentPort.findApprovedByCompanyNit(nitCompany);
    }


}
