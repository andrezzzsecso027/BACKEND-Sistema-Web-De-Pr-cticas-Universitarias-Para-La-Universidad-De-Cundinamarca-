package co.edu.ucundinamarca.backendudecprac.infrastructure.adapter.out.persistence.Adapters;

import co.edu.ucundinamarca.backendudecprac.domain.model.StudentProfile;
import co.edu.ucundinamarca.backendudecprac.domain.port.out.StudentProfileRepositoryPort;
import co.edu.ucundinamarca.backendudecprac.infrastructure.adapter.out.persistence.Entities.StudentProfileEntity;
import co.edu.ucundinamarca.backendudecprac.infrastructure.adapter.out.persistence.JPA.JPAStudentProfileRepository;
import org.springframework.stereotype.Component;

@Component
public class StudentProfileRepositoryAdapter implements StudentProfileRepositoryPort {
    private final JPAStudentProfileRepository jpaRepository;

    public StudentProfileRepositoryAdapter(JPAStudentProfileRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public StudentProfile save(StudentProfile profile) {
        return null;
    }

    @Override
    public StudentProfile findByDocumentStudent(String documentStudent) {
        StudentProfileEntity entity = jpaRepository.buscarPorCedulaEstudiante(documentStudent);


        if (entity == null) {
            return null;
        }

        StudentProfile profile = new StudentProfile();
        profile.setIdProfile(entity.getIdProfile());
        profile.setSkills(entity.getSkills());
        profile.setEnglishLevel(entity.getEnglishLevel());
        profile.setLinkLifesheet(entity.getLinkLifesheet());
        profile.setGithubUrl(entity.getGithubUrl());
        profile.setAboutMe(entity.getAboutMe());

        return profile;
    }
}
