package co.edu.ucundinamarca.backendudecprac.domain.port.in;

import co.edu.ucundinamarca.backendudecprac.domain.model.Notification;

public interface CreateNotificationUseCase {
    Notification createNotification(Notification notification);
}
