package co.edu.ucundinamarca.backendudecprac.domain.port.out;

import co.edu.ucundinamarca.backendudecprac.domain.model.Student;
import java.util.List;

public interface StudentRepositoryPort {
    Student saveStudent(Student student);
    List<Student> findAllStudents();

    boolean existsByDocumento(String documento);
}
