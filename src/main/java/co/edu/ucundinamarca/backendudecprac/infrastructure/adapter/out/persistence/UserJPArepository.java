package co.edu.ucundinamarca.backendudecprac.infrastructure.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface usuarioJPArepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByCorreoElectronico(String correoElectronico);
    boolean existsByCorreoElectronico(String correoElectronico);
}
