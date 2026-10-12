package co.edu.ucundinamarca.backendudecprac.domain.port.out;

import co.edu.ucundinamarca.backendudecprac.domain.model.StudentProfile;

public interface StudentProfileRepositoryPort {
    StudentProfile save(StudentProfile profile);
    StudentProfile findByDocumentStudent(String documentStudent);
}
