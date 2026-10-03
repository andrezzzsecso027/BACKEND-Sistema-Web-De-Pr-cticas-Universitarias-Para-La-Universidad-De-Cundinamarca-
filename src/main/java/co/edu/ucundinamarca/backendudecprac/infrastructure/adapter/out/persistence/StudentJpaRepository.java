package co.edu.ucundinamarca.backendudecprac.infrastructure.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentJpaRepository extends JpaRepository<StudentEntity, String> {
    boolean existsByDocumento(String documento);
}