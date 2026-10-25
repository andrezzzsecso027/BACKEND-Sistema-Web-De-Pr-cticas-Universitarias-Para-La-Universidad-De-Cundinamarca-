package co.edu.ucundinamarca.backendudecprac.infrastructure.adapter.out.persistence.jpa;

import co.edu.ucundinamarca.backendudecprac.infrastructure.adapter.out.persistence.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserJPArepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByCorreoElectronico(String correoElectronico);
    boolean existsByCorreoElectronico(String correoElectronico);

    List<UserEntity> findByRolUsuarioIgnoreCase(String rolUsuario);
}
