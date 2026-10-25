package co.edu.ucundinamarca.backendudecprac.infrastructure.adapter.out.persistence.jpa;

import co.edu.ucundinamarca.backendudecprac.infrastructure.adapter.out.persistence.entities.NotificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationJPArepository extends JpaRepository<NotificationEntity, Long> {
}
