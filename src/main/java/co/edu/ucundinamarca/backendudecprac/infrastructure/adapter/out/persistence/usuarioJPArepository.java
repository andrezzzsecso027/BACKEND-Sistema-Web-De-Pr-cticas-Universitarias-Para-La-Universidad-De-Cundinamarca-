package co.edu.ucundinamarca.backendudecprac.infrastructure.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

public interface usuarioJPArepository extends JpaRepository<usuarioEntity, Long> {
}
