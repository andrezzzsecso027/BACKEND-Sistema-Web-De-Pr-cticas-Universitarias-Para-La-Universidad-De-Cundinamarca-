package co.edu.ucundinamarca.backendudecprac.domain.port.out;

import co.edu.ucundinamarca.backendudecprac.domain.model.Usuario;

import java.util.List;

public interface usuarioRepositoryPort {
    Usuario saveUsuario(Usuario usuario);
    List<Usuario> findAllUsuarios();
}
