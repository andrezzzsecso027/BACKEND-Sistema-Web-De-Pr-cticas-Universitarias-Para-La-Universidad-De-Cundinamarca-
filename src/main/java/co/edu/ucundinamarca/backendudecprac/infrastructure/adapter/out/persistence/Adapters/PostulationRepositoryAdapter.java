package co.edu.ucundinamarca.backendudecprac.infrastructure.adapter.out.persistence.Adapters;

import co.edu.ucundinamarca.backendudecprac.domain.model.Postulation;
import co.edu.ucundinamarca.backendudecprac.domain.port.out.PostulationRepositoryPort;
import co.edu.ucundinamarca.backendudecprac.infrastructure.adapter.out.persistence.Entities.PostulationEntity;
import co.edu.ucundinamarca.backendudecprac.infrastructure.adapter.out.persistence.JPA.JPApostulationRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class PostulationRepositoryAdapter implements PostulationRepositoryPort {
    private final JPApostulationRepository jpaRepository;

    public PostulationRepositoryAdapter(JPApostulationRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Postulation savePostulation(Postulation postulation) {
        PostulationEntity entity = new PostulationEntity();

        if (postulation.getIdPostulation() !=null && postulation.getIdPostulation() != 0) {
            entity.setIdPostulation(postulation.getIdPostulation());
        }
        entity.setIdOffer(postulation.getIdOffer());
        entity.setNumberDocumentStudent(String.valueOf(postulation.getDocumentNumberStudent()));
        entity.setStatusPostulation(postulation.getStatusPostulation());
        entity.setDateOfPostulation(postulation.getDatePostulation());
        entity.setDateOfActualizacion(postulation.getDateUdpate());
        entity.setObservations(postulation.getObservations());

        PostulationEntity guardado = jpaRepository.save(entity);
        postulation.setIdPostulation(guardado.getIdPostulation());
        return postulation;
    }

    @Override
    public boolean existsByStudentAndOffer(Long idOffer, String documentStudent) {
        return jpaRepository.existsByIdOfferAndNumberDocumentStudent(idOffer, documentStudent);
    }

    @Override
    public List<Postulation> getPostulationsByOfferId(Long idOffer) {
        List<PostulationEntity> entidades = jpaRepository.findByIdOffer(idOffer);

        return entidades.stream().map(entity -> {
            Postulation p = new Postulation();
            p.setIdPostulation(entity.getIdPostulation());
            p.setIdOffer(entity.getIdOffer());
            p.setDocumentNumberStudent(entity.getNumberDocumentStudent());
            p.setStatusPostulation(entity.getStatusPostulation());
            p.setDatePostulation(entity.getDateOfPostulation());
            p.setDateUdpate(entity.getDateOfActualizacion());
            p.setObservations(entity.getObservations());
            return p;
        }).collect(Collectors.toList());
    }

    @Override
    public Postulation findById(Long idPostulation) {
        PostulationEntity entity = jpaRepository.findById(idPostulation).orElse(null);
        if (entity == null) return null;

        Postulation postulation = new Postulation();
        postulation.setIdPostulation(entity.getIdPostulation());
        postulation.setIdOffer(entity.getIdOffer());
        postulation.setDocumentNumberStudent(entity.getNumberDocumentStudent());
        postulation.setStatusPostulation(entity.getStatusPostulation());
        postulation.setDatePostulation(entity.getDateOfPostulation());
        postulation.setDateUdpate(entity.getDateOfActualizacion());
        postulation.setObservations(entity.getObservations());
        return postulation;
    }
}

