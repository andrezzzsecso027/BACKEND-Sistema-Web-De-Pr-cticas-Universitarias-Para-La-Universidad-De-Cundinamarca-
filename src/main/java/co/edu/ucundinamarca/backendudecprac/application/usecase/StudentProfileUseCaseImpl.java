package co.edu.ucundinamarca.backendudecprac.application.usecase;

import co.edu.ucundinamarca.backendudecprac.domain.exceptions.ResourceNotFoundException;
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

    /**
     * Crea el perfil si no existe; si ya existe, actualiza solo los campos enviados (no nulos).
     */
    @Transactional
    @Override
    public StudentProfile saveProfile(StudentProfile profile) {
        if (profile.getDocumentStudent() == null || !studentRepositoryPort.existsByDocumento(profile.getDocumentStudent())) {
            throw new ResourceNotFoundException("El estudiante no existe.");
        }

        StudentProfile existing = profileRepositoryPort.findByDocumentStudent(profile.getDocumentStudent());
        if (existing == null) {
            return profileRepositoryPort.save(profile);
        }

        if (profile.getAboutMe() != null) existing.setAboutMe(profile.getAboutMe());
        if (profile.getSkills() != null) existing.setSkills(profile.getSkills());
        if (profile.getEnglishLevel() != null) existing.setEnglishLevel(profile.getEnglishLevel());
        if (profile.getGithubUrl() != null) existing.setGithubUrl(profile.getGithubUrl());
        if (profile.getLinkLifesheet() != null) existing.setLinkLifesheet(profile.getLinkLifesheet());
        if (profile.getPhotoUrl() != null) existing.setPhotoUrl(profile.getPhotoUrl());
        return profileRepositoryPort.save(existing);
    }

    @Transactional(readOnly = true)
    @Override
    public StudentProfile getProfileByDocument(String documentStudent) {
        return profileRepositoryPort.findByDocumentStudent(documentStudent);
    }
}
