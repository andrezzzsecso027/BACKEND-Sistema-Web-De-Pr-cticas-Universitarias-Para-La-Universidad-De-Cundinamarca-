package co.edu.ucundinamarca.backendudecprac.infrastructure.adapter.out.persistence.adapters;

import co.edu.ucundinamarca.backendudecprac.domain.model.Practice;
import co.edu.ucundinamarca.backendudecprac.domain.port.out.PracticeRepositoryPort;
import co.edu.ucundinamarca.backendudecprac.infrastructure.adapter.out.persistence.entities.PracticeEntity;
import co.edu.ucundinamarca.backendudecprac.infrastructure.adapter.out.persistence.jpa.JPAPracticeRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class PracticeRepositoryAdapter implements PracticeRepositoryPort {

    private final JPAPracticeRepository jpaRepository;

    public PracticeRepositoryAdapter(JPAPracticeRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Practice savePractice(Practice practice) {
        PracticeEntity entity = new PracticeEntity();

        if (practice.getIdPractice() != null && practice.getIdPractice() != 0) {
            entity.setIdPractice(practice.getIdPractice());
        }
        entity.setIdPostulation(practice.getIdPostulation());
        entity.setPracticeStatus(practice.getPracticeStatus());
        entity.setStartDate(practice.getStartDate());
        entity.setEndDate(practice.getEndDate());
        entity.setCompanyFeedback(practice.getCompanyFeedback());
        entity.setStudentEvaluation(practice.getStudentEvaluation());

        // Guardamos y capturamos la entidad con su nuevo ID
        PracticeEntity guardada = jpaRepository.save(entity);

        // Le pasamos el ID autogenerado al dominio y lo devolvemos
        practice.setIdPractice(guardada.getIdPractice());
        return practice;
    }
    @Override
    public Practice getPracticeByPostulationId(Long idPostulation) {
        Optional<PracticeEntity> entityOpt = jpaRepository.findByIdPostulation(idPostulation);

        if (entityOpt.isEmpty()) {
            return null; // O lanzar una excepción personalizada si prefieres
        }

        PracticeEntity entity = entityOpt.get();
        Practice practice = new Practice();
        practice.setIdPractice(entity.getIdPractice());
        practice.setIdPostulation(entity.getIdPostulation());
        practice.setPracticeStatus(entity.getPracticeStatus());
        practice.setStartDate(entity.getStartDate());
        practice.setEndDate(entity.getEndDate());
        practice.setCompanyFeedback(entity.getCompanyFeedback());
        practice.setStudentEvaluation(entity.getStudentEvaluation());

        return practice;
    }
    @Override
    public Practice findById(Long idPractice) {
        PracticeEntity entity = jpaRepository.findById(idPractice).orElse(null);
        if (entity == null) return null;

        Practice practice = new Practice();
        practice.setIdPractice(entity.getIdPractice());
        practice.setIdPostulation(entity.getIdPostulation());
        practice.setPracticeStatus(entity.getPracticeStatus());
        practice.setStartDate(entity.getStartDate());
        practice.setEndDate(entity.getEndDate());
        practice.setCompanyFeedback(entity.getCompanyFeedback());
        practice.setStudentEvaluation(entity.getStudentEvaluation());
        return practice;
    }
}
