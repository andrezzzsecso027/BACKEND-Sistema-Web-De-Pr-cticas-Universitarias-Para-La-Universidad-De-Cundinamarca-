package co.edu.ucundinamarca.backendudecprac.infrastructure.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

public interface JPAEmpresaRepository extends JpaRepository<CompanyEntity,String> {

    boolean existsByNit(String nit);
}
