package co.edu.ucundinamarca.backendudecprac.infrastructure.adapter.out.persistence.JPA;

import co.edu.ucundinamarca.backendudecprac.infrastructure.adapter.out.persistence.Entities.NotificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationJPArepository extends JpaRepository<NotificationEntity, Long> {
}
