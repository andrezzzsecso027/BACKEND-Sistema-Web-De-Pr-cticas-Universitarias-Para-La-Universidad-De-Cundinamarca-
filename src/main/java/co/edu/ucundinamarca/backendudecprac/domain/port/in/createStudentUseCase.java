package co.edu.ucundinamarca.backendudecprac.domain.port.in;

import co.edu.ucundinamarca.backendudecprac.domain.model.Student;
import co.edu.ucundinamarca.backendudecprac.domain.model.User;

import java.util.List;

public interface createStudentUseCase {
    Student createStudent(User user, Student student);
    List<Student> FindAllStudents();
}
