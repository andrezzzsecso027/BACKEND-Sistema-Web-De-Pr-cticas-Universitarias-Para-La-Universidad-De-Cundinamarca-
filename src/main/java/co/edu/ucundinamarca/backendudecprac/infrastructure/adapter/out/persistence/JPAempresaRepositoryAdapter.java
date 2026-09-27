package co.edu.ucundinamarca.backendudecprac.infrastructure.adapter.out.persistence;

import co.edu.ucundinamarca.backendudecprac.domain.model.Empresa;
import co.edu.ucundinamarca.backendudecprac.domain.port.out.empresaRepositoryPort;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class JPAempresaRepositoryAdapter implements empresaRepositoryPort {

    private final JPAEmpresaRepository jpaEmpresaRepository;

    public JPAempresaRepositoryAdapter(JPAEmpresaRepository jpaEmpresaRepository) {
        this.jpaEmpresaRepository = jpaEmpresaRepository;
    }

    @Override
    public Empresa saveEmpresa(Empresa empresa) {
        empresaEntity entity = new empresaEntity();
        entity.setNit(empresa.getNIT());
        entity.setIdUsuario(empresa.getIdUsuario());
        entity.setNombreEmpresa(empresa.getNombreEmpresa());
        entity.setTipoEmpresa(empresa.getTipoEmpresa());
        entity.setDireccionEmpresa(empresa.getDireccionEmpresa());
        entity.setTelefonoEmpresa(empresa.getTelefonoEmpresa());
        jpaEmpresaRepository.save(entity);
        return empresa;
    }

    @Override
    public List<Empresa> findAllEmpresas() {
        return List.of();
    }
}
