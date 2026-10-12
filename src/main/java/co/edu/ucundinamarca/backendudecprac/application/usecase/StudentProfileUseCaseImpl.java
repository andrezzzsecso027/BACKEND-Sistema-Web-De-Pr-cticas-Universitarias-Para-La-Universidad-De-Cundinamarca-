package co.edu.ucundinamarca.backendudecprac.application.usecase;

import co.edu.ucundinamarca.backendudecprac.domain.model.Student;
import co.edu.ucundinamarca.backendudecprac.domain.model.StudentProfile;
import co.edu.ucundinamarca.backendudecprac.domain.port.in.SaveStudentProfileUseCase;
import co.edu.ucundinamarca.backendudecprac.domain.port.out.StudentProfileRepositoryPort;
import co.edu.ucundinamarca.backendudecprac.domain.port.out.StudentRepositoryPort;
import org.springframework.stereotype.Service;

@Service
public class StudentProfileUseCaseImpl implements SaveStudentProfileUseCase {
    private final StudentProfileRepositoryPort profileRepositoryPort;
    private final StudentRepositoryPort studentRepositoryPort;

    public StudentProfileUseCaseImpl(StudentProfileRepositoryPort profileRepositoryPort, StudentRepositoryPort studentRepositoryPort) {
        this.profileRepositoryPort = profileRepositoryPort;
        this.studentRepositoryPort = studentRepositoryPort;
    }

    @Override
    public StudentProfile saveProfile(StudentProfile profile) {
        Boolean estudiante = studentRepositoryPort.existsByDocumento(profile.getDocumentStudent());
        if (!estudiante) {
            throw new IllegalArgumentException("El estudiante no existe.");
        }
        StudentProfile perfilExistente = profileRepositoryPort.findByDocumentStudent(profile.getDocumentStudent());
        if (perfilExistente != null) {

            perfilExistente.setSkills(profile.getSkills());
            perfilExistente.setEnglishLevel(profile.getEnglishLevel());
            perfilExistente.setLinkLifesheet(profile.getLinkLifesheet());
            perfilExistente.setGithubUrl(profile.getGithubUrl());
            perfilExistente.setAboutMe(profile.getAboutMe());
            return profileRepositoryPort.save(perfilExistente);
        }


        return profileRepositoryPort.save(profile);

    }
}
