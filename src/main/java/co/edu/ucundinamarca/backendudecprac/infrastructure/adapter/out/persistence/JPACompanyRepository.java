package co.edu.ucundinamarca.backendudecprac.infrastructure.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

public interface JPACompanyRepository extends JpaRepository<CompanyEntity,String> {

    boolean existsByNit(String nit);
}
