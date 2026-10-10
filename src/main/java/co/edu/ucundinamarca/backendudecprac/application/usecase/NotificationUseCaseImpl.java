package co.edu.ucundinamarca.backendudecprac.application.usecase;

import co.edu.ucundinamarca.backendudecprac.domain.model.Notification;
import co.edu.ucundinamarca.backendudecprac.domain.port.in.CreateNotificationUseCase;
import co.edu.ucundinamarca.backendudecprac.domain.port.out.NotificationRepositoryPort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class NotificationUseCaseImpl implements CreateNotificationUseCase {
    private final NotificationRepositoryPort notificationRepositoryPort;

    public NotificationUseCaseImpl(NotificationRepositoryPort notificationRepositoryPort) {
        this.notificationRepositoryPort = notificationRepositoryPort;
    }


    @Override
    public Notification createNotification(Notification notification) {
        // Asignamos la fecha exacta en la que se crea la notificación
        notification.setDateSended(LocalDateTime.now());

        // Delegamos el guardado al puerto de salida
        return notificationRepositoryPort.sendNotification(notification);
    }
}
