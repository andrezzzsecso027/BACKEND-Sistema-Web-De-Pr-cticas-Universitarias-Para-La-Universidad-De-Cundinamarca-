package co.edu.ucundinamarca.backendudecprac.infrastructure.adapter.out.persistence.JPA;

import co.edu.ucundinamarca.backendudecprac.infrastructure.adapter.out.persistence.Entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserJPArepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByCorreoElectronico(String correoElectronico);
    boolean existsByCorreoElectronico(String correoElectronico);
}
