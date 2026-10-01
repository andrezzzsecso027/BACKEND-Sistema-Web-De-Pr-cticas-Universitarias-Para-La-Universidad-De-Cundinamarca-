package co.edu.ucundinamarca.backendudecprac.infrastructure.adapter.out.persistence;

import co.edu.ucundinamarca.backendudecprac.domain.model.Empresa;
import co.edu.ucundinamarca.backendudecprac.domain.model.Estudiante;
import co.edu.ucundinamarca.backendudecprac.domain.model.Usuario;
import co.edu.ucundinamarca.backendudecprac.domain.port.out.empresaRepositoryPort;
import co.edu.ucundinamarca.backendudecprac.domain.port.out.usuarioRepositoryPort;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class usuarioRepositoryAdapater implements usuarioRepositoryPort {

    private final usuarioJPArepository usuariojparepository;

    public usuarioRepositoryAdapater(usuarioJPArepository usuariojparepository, EstudianteJpaRepository jpaRepository, JPAEmpresaRepository jpaEmpresaRepository) {
        this.usuariojparepository = usuariojparepository;
    }

    @Override
    public Usuario saveUsuario(Usuario usuario) {
        usuarioEntity entity = new usuarioEntity();
        entity.setCorreoElectronico(usuario.getCorreoElectronico());
        entity.setContrasenia(usuario.getContrasenia());
        entity.setRolUsuario(usuario.getRolUsuario());
        entity.setEstado(usuario.isEstadoUsuario());

        usuarioEntity guardado = usuariojparepository.save(entity);
        usuario.setIdUsuario(guardado.getIdUsario());
        return usuario;
    }
    @Override
    public boolean existsByCorreoElectronico(String correoElectronico) {
        return usuariojparepository.existsByCorreoElectronico(correoElectronico);
    }
    @Override
    public List<Usuario> findAllUsuarios() {
        return List.of();
    }
}
