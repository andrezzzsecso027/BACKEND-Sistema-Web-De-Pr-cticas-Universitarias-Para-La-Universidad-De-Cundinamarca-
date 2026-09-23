package co.edu.ucundinamarca.backendudecprac.infrastructure.adapter.out.persistence;

import co.edu.ucundinamarca.backendudecprac.domain.model.Estudiante;
import co.edu.ucundinamarca.backendudecprac.domain.port.EstudianteRepositoryPort;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class EstudianteRepositoryAdapter implements EstudianteRepositoryPort {

    private final EstudianteJpaRepository jpaRepository;

    public EstudianteRepositoryAdapter(EstudianteJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Estudiante guardar(Estudiante estudiante) {
        EstudianteEntity entity = new EstudianteEntity();
        entity.setCodigo(estudiante.getCodigo());
        entity.setNombres(estudiante.getNombres());
        entity.setApellidos(estudiante.getApellidos());
        entity.setCorreo(estudiante.getCorreo());
        entity.setCarrera(estudiante.getCarrera());
        entity.setSemestre(estudiante.getSemestre());
        EstudianteEntity guardado = jpaRepository.save(entity);
        return toDomain(guardado);
    }

    @Override
    public List<Estudiante> listarTodos() {
        return jpaRepository.findAll().stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    private Estudiante toDomain(EstudianteEntity entity) {
        return new Estudiante(entity.getId(), entity.getCodigo(), entity.getNombres(),
                entity.getApellidos(), entity.getCorreo(), entity.getCarrera(), entity.getSemestre());
    }
}