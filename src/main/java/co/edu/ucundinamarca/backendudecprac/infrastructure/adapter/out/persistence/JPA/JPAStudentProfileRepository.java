package co.edu.ucundinamarca.backendudecprac.infrastructure.adapter.out.persistence.JPA;

import co.edu.ucundinamarca.backendudecprac.domain.model.StudentProfile;
import co.edu.ucundinamarca.backendudecprac.infrastructure.adapter.out.persistence.Entities.StudentProfileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface JPAStudentProfileRepository extends JpaRepository<StudentProfileEntity, Long> {
    @Query("SELECT p FROM StudentProfileEntity p WHERE p.student.documento = :documento")
    StudentProfileEntity buscarPorCedulaEstudiante(@Param("documento") String documento);
}
