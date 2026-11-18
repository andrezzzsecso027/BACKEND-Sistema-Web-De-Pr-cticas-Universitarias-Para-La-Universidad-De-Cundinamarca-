package co.edu.ucundinamarca.backendudecprac.application.usecase;

import co.edu.ucundinamarca.backendudecprac.domain.exceptions.BusinessRuleException;
import co.edu.ucundinamarca.backendudecprac.domain.exceptions.ResourceNotFoundException;
import co.edu.ucundinamarca.backendudecprac.domain.model.Comment;
import co.edu.ucundinamarca.backendudecprac.domain.model.Notification;
import co.edu.ucundinamarca.backendudecprac.domain.model.Postulation;
import co.edu.ucundinamarca.backendudecprac.domain.model.Practice;
import co.edu.ucundinamarca.backendudecprac.domain.model.Student;
import co.edu.ucundinamarca.backendudecprac.domain.model.User;
import co.edu.ucundinamarca.backendudecprac.domain.port.in.CommentUseCase;
import co.edu.ucundinamarca.backendudecprac.domain.port.in.CreateNotificationUseCase;
import co.edu.ucundinamarca.backendudecprac.domain.port.out.CommentRepositoryPort;
import co.edu.ucundinamarca.backendudecprac.domain.port.out.PostulationRepositoryPort;
import co.edu.ucundinamarca.backendudecprac.domain.port.out.PracticeRepositoryPort;
import co.edu.ucundinamarca.backendudecprac.domain.port.out.StudentRepositoryPort;
import co.edu.ucundinamarca.backendudecprac.domain.port.out.UserRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CommentUseCaseImpl implements CommentUseCase {

    private static final String PRACTICA_NO_EXISTE = "La práctica asociada no existe.";

    private final CommentRepositoryPort commentPort;
    private final PracticeRepositoryPort practicePort;
    private final CreateNotificationUseCase notificationUseCase;
    private final UserRepositoryPort userPort;
    private final StudentRepositoryPort studentPort;
    private final PostulationRepositoryPort postulationPort;

    public CommentUseCaseImpl(CommentRepositoryPort commentPort, PracticeRepositoryPort practicePort,
                              CreateNotificationUseCase notificationUseCase, UserRepositoryPort userPort,
                              StudentRepositoryPort studentPort, PostulationRepositoryPort postulationPort) {
        this.commentPort = commentPort;
        this.practicePort = practicePort;
        this.notificationUseCase = notificationUseCase;
        this.userPort = userPort;
        this.studentPort = studentPort;
        this.postulationPort = postulationPort;
    }

    @Transactional
    @Override
    public Comment createComment(Comment comment, String studentEmail) {
        Practice practice = practicePort.findById(comment.getIdPractice());
        // Si la práctica no es del estudiante de la sesión se responde igual que si no existiera (404),
        // para no revelar prácticas ajenas.
        if (practice == null || !belongsToStudent(practice, studentEmail)) {
            throw new ResourceNotFoundException(PRACTICA_NO_EXISTE);
        }
        if (!practice.isFinished()) {
            throw new BusinessRuleException("Solo puedes calificar a la empresa cuando tu práctica esté en estado FINALIZADA.");
        }
        // Relación 1:1 en el MER: solo un comentario por práctica
        if (commentPort.existsByIdPractice(comment.getIdPractice())) {
            throw new BusinessRuleException("Ya registraste una calificación para esta práctica.");
        }

        comment.submit(LocalDateTime.now()); // valida la calificación y nace PENDIENTE de moderación
        Comment saved = commentPort.saveComment(comment);

        List<User> administradores = userPort.findByRole("administrador");
        for (User admin : administradores) {
            Notification notification = new Notification();
            notification.setIdUser(admin.getIdUser());
            notification.setTypeNotification("MODERACION_COMENTARIO");
            notification.setMessage("Nuevo comentario pendiente de moderación para la práctica #" + comment.getIdPractice());
            notificationUseCase.createNotification(notification);
        }
        return saved;
    }

    /** La práctica es del estudiante si la postulación que la originó fue hecha con su documento. */
    private boolean belongsToStudent(Practice practice, String studentEmail) {
        String document = userPort.findByCorreoElectronico(studentEmail)
                .flatMap(user -> studentPort.findByIdUser(user.getIdUser()))
                .map(Student::getDocument)
                .orElse(null);
        Postulation postulation = practice.getIdPostulation() == null ? null : postulationPort.findById(practice.getIdPostulation());
        return document != null && postulation != null && document.equals(postulation.getDocumentNumberStudent());
    }

    @Override
    public List<Comment> getApprovedCommentsByCompany(String nitCompany) {
        return commentPort.findApprovedByCompanyNit(nitCompany);
    }
}
