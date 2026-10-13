package co.edu.ucundinamarca.backendudecprac.domain.port.out;

import co.edu.ucundinamarca.backendudecprac.domain.model.Postulation;

import java.util.List;

public interface PostulationRepositoryPort {
    Postulation savePostulation(Postulation postulation);
    boolean existsByStudentAndOffer(Long idOffer, String documentStudent);
    List<Postulation> getPostulationsByOfferId(Long idOffer);
    Postulation findById(Long idPostulation);
}
