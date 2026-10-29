package co.edu.ucundinamarca.backendudecprac.application.usecase;


import co.edu.ucundinamarca.backendudecprac.domain.model.StudentProfile;
import co.edu.ucundinamarca.backendudecprac.domain.port.in.SaveStudentProfileUseCase;
import co.edu.ucundinamarca.backendudecprac.domain.port.in.StudentProfileUseCase;
import co.edu.ucundinamarca.backendudecprac.domain.port.out.StudentProfileRepositoryPort;
import co.edu.ucundinamarca.backendudecprac.domain.port.out.StudentRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StudentProfileUseCaseImpl implements SaveStudentProfileUseCase, StudentProfileUseCase {
    private final StudentProfileRepositoryPort profileRepositoryPort;
    private final StudentRepositoryPort studentRepositoryPort;

    public StudentProfileUseCaseImpl(StudentProfileRepositoryPort profileRepositoryPort, StudentRepositoryPort studentRepositoryPort) {
        this.profileRepositoryPort = profileRepositoryPort;
        this.studentRepositoryPort = studentRepositoryPort;
    }

    @Transactional
    @Override
    public StudentProfile saveProfile(StudentProfile profile) {
        if (profile.getDocumentStudent() == null || !studentRepositoryPort.existsByDocumento(profile.getDocumentStudent())) {
            throw new IllegalArgumentException("El estudiante no existe.");
        }
        return profileRepositoryPort.save(profile);

    }
    @Transactional


    @Override
    public StudentProfile getProfileByDocument(String documentStudent) {
        return profileRepositoryPort.findByDocumentStudent(documentStudent);
    }
}
