package co.edu.ucundinamarca.backendudecprac.application.usecase;

import co.edu.ucundinamarca.backendudecprac.domain.ResourceAlreadyExistsException;
import co.edu.ucundinamarca.backendudecprac.domain.model.Estudiante;
import co.edu.ucundinamarca.backendudecprac.domain.model.Usuario;
import co.edu.ucundinamarca.backendudecprac.domain.port.in.createEmpresaUseCase;
import co.edu.ucundinamarca.backendudecprac.domain.port.in.createEstudianteUseCase;
import co.edu.ucundinamarca.backendudecprac.domain.port.out.EstudianteRepositoryPort;
import co.edu.ucundinamarca.backendudecprac.domain.port.out.usuarioRepositoryPort;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class EstudianteUseCaseUseCaseImpl implements createEstudianteUseCase {

    private final EstudianteRepositoryPort estudianteRepositoryPort;
    private final usuarioRepositoryPort usuariorepositoryport;

    public EstudianteUseCaseUseCaseImpl(EstudianteRepositoryPort estudianteRepositoryPort, usuarioRepositoryPort usuariorepositoryport) {
        this.estudianteRepositoryPort = estudianteRepositoryPort;
        this.usuariorepositoryport = usuariorepositoryport;
    }


    @Override
    public Estudiante createEstudiante(Usuario usuario, Estudiante estudiante) {
        if(usuariorepositoryport.existsByCorreoElectronico(usuario.getCorreoElectronico())) {
            throw new ResourceAlreadyExistsException("Ya existe una cuenta con ese correo");
        }
        if(estudianteRepositoryPort.existsByDocumento(estudiante.getDocumento())){
            throw new ResourceAlreadyExistsException("Ya existe una cuenta con ese documento");
        }
        Usuario usuarioGuardado = usuariorepositoryport.saveUsuario(usuario);
        estudiante.setIdUsuario(usuarioGuardado.getIdUsuario());
        return estudianteRepositoryPort.saveEstudiante(estudiante);
    }

    @Override
    public List<Estudiante> listarTodos() {
        return estudianteRepositoryPort.listarTodos();
    }

}