package co.edu.ucundinamarca.backendudecprac.infrastructure.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface JPACompanyRepository extends JpaRepository<CompanyEntity,String> {

    boolean existsByNit(String nit);
    Optional<CompanyEntity> findByNit(String nit);
    List<CompanyEntity> findByEstadoVerificacion(String estadoVerificacion);
}
