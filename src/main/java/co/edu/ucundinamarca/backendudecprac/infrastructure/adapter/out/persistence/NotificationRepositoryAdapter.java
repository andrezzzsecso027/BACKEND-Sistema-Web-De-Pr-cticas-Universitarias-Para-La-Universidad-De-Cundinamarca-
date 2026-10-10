package co.edu.ucundinamarca.backendudecprac.infrastructure.adapter.out.persistence;

import co.edu.ucundinamarca.backendudecprac.domain.model.Notification;
import co.edu.ucundinamarca.backendudecprac.domain.port.out.NotificationRepositoryPort;
import org.springframework.stereotype.Component;

@Component
public class NotificationRepositoryAdapter implements NotificationRepositoryPort {
    private final NotificationJPArepository notificationJPArepository;

    public NotificationRepositoryAdapter(NotificationJPArepository notificationJPArepository) {
        this.notificationJPArepository = notificationJPArepository;
    }

    @Override
    public Notification sendNotification(Notification notification) {
        NotificationEntity entity = new NotificationEntity();
        entity.setIdUsuario(notification.getIdUser());
        entity.setTipoNotificacion(notification.getTypeNotification());
        entity.setMensaje(notification.getMessage());
        entity.setFechaEnvio(notification.getDateSended());

        NotificationEntity guardado = notificationJPArepository.save(entity);
        guardado.setIdNotificacion(guardado.getIdNotificacion());
        return notification;
    }
}
