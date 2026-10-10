package co.edu.ucundinamarca.backendudecprac.domain.port.out;

import co.edu.ucundinamarca.backendudecprac.domain.model.Notification;

public interface NotificationRepositoryPort {
    Notification sendNotification(Notification notification);
}
