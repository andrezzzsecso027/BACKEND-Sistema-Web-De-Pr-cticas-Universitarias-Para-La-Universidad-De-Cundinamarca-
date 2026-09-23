package co.edu.ucundinamarca.backendudecprac.application.usecase;

import co.edu.ucundinamarca.backendudecprac.domain.model.Estudiante;
import co.edu.ucundinamarca.backendudecprac.domain.port.EstudianteRepositoryPort;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class EstudianteUseCase {

    private final EstudianteRepositoryPort repositoryPort;

    public EstudianteUseCase(EstudianteRepositoryPort repositoryPort) {
        this.repositoryPort = repositoryPort;
    }

    public Estudiante crear(Estudiante estudiante) {
        return repositoryPort.guardar(estudiante);
    }

    public List<Estudiante> listar() {
        return repositoryPort.listarTodos();
    }
}