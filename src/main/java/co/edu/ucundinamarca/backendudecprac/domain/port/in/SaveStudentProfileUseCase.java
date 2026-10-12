package co.edu.ucundinamarca.backendudecprac.domain.port.in;

import co.edu.ucundinamarca.backendudecprac.domain.model.StudentProfile;

public interface SaveStudentProfileUseCase {
    StudentProfile saveProfile(StudentProfile studentProfile);
}
