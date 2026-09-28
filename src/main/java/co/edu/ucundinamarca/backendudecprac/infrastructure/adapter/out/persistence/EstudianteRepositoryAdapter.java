package co.edu.ucundinamarca.backendudecprac.infrastructure.adapter.out.persistence;

import co.edu.ucundinamarca.backendudecprac.domain.model.Estudiante;
import co.edu.ucundinamarca.backendudecprac.domain.model.Usuario;
import co.edu.ucundinamarca.backendudecprac.domain.port.out.EstudianteRepositoryPort;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class EstudianteRepositoryAdapter implements EstudianteRepositoryPort {

    private final EstudianteJpaRepository jpaRepository;

    public EstudianteRepositoryAdapter(EstudianteJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }


    @Override
    public Estudiante saveEstudiante(Estudiante estudiante) {
        EstudianteEntity entity = new EstudianteEntity();
        entity.setDocumento(estudiante.getDocumento());
        entity.setIdUsuario(estudiante.getIdUsuario());
        entity.setNombres(estudiante.getNombres());
        entity.setApellidos(estudiante.getApellidos());
        entity.setDireccion(estudiante.getDireccion());
        entity.setTelefono(estudiante.getTelefono());
        entity.setSede(estudiante.getSede());
        entity.setProgramaAcademico(estudiante.getProgramaAcademico());
        jpaRepository.save(entity);
        return estudiante;
    }


    @Override
    public List<Estudiante> listarTodos() {
        List<EstudianteEntity> entidades = jpaRepository.findAll();
        List<Estudiante> estudiantes = new ArrayList<>();

        for (EstudianteEntity entity : entidades) {
            Estudiante est = new Estudiante();
            est.setIdUsuario(entity.getIdUsuario());
            est.setDocumento(entity.getDocumento());
            est.setNombres(entity.getNombres());
            est.setApellidos(entity.getApellidos());
            est.setDireccion(entity.getDireccion());
            est.setTelefono(entity.getTelefono());
            est.setSede(entity.getSede());
            est.setProgramaAcademico(entity.getProgramaAcademico());
            estudiantes.add(est);
        }
        return estudiantes;
    }

    /*
    private Estudiante toDomain(EstudianteEntity entity) {
        return new Estudiante(entity.getId(), entity.getCodigo(), entity.getNombres(),
                entity.getApellidos(), entity.getCorreo(), entity.getCarrera(), entity.getSemestre());
    }

     */
}