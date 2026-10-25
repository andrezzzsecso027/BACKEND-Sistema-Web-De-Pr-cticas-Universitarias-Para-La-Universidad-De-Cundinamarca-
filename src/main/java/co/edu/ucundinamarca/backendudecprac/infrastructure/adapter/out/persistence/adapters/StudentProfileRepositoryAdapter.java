package co.edu.ucundinamarca.backendudecprac.infrastructure.adapter.out.persistence.adapters;

import co.edu.ucundinamarca.backendudecprac.domain.model.StudentProfile;
import co.edu.ucundinamarca.backendudecprac.domain.port.out.StudentProfileRepositoryPort;
import co.edu.ucundinamarca.backendudecprac.infrastructure.adapter.out.persistence.entities.StudentEntity;
import co.edu.ucundinamarca.backendudecprac.infrastructure.adapter.out.persistence.entities.StudentProfileEntity;
import co.edu.ucundinamarca.backendudecprac.infrastructure.adapter.out.persistence.jpa.JPAStudentProfileRepository;
import org.springframework.stereotype.Component;

@Component
public class StudentProfileRepositoryAdapter implements StudentProfileRepositoryPort {
    private final JPAStudentProfileRepository jpaRepository;

    public StudentProfileRepositoryAdapter(JPAStudentProfileRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public StudentProfile save(StudentProfile profile) {
        StudentProfileEntity entity = jpaRepository.buscarPorCedulaEstudiante(profile.getDocumentStudent());
        if (entity == null) {
            entity = new StudentProfileEntity();
            // ¡CRÍTICO! Asigna aquí la cédula (usa el setter que tengas en StudentProfileEntity, ej. setCedulaEstudiante o setDocumentStudent):
            StudentEntity studentRef = new StudentEntity();
            studentRef.setDocumento(profile.getDocumentStudent());
            entity.setStudent(studentRef);
        }
        entity.setSkills(profile.getSkills());
        entity.setEnglishLevel(profile.getEnglishLevel());
        entity.setLinkLifesheet(profile.getLinkLifesheet());
        entity.setGithubUrl(profile.getGithubUrl());
        entity.setAboutMe(profile.getAboutMe());
        entity.setPhotoUrl(profile.getPhotoUrl());

        StudentProfileEntity saved = jpaRepository.save(entity);
        profile.setIdProfile(saved.getIdProfile());
        return profile;
    }

    @Override
    public StudentProfile findByDocumentStudent(String documentStudent) {
        StudentProfileEntity entity = jpaRepository.buscarPorCedulaEstudiante(documentStudent);


        if (entity == null) {
            return null;
        }

        StudentProfile profile = new StudentProfile();
        profile.setIdProfile(entity.getIdProfile());
        profile.setDocumentStudent(
                entity.getStudent() != null ? entity.getStudent().getDocumento() : documentStudent
        );
        profile.setSkills(entity.getSkills());
        profile.setEnglishLevel(entity.getEnglishLevel());
        profile.setLinkLifesheet(entity.getLinkLifesheet());
        profile.setGithubUrl(entity.getGithubUrl());
        profile.setAboutMe(entity.getAboutMe());
        profile.setPhotoUrl(entity.getPhotoUrl());
        return profile;
    }
}
