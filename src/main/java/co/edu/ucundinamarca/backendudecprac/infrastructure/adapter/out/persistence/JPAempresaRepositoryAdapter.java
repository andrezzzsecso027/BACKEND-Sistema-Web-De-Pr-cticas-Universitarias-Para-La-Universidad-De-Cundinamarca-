package co.edu.ucundinamarca.backendudecprac.infrastructure.adapter.out.persistence;

import co.edu.ucundinamarca.backendudecprac.domain.model.Empresa;
import co.edu.ucundinamarca.backendudecprac.domain.port.out.empresaRepositoryPort;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
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
        List<empresaEntity> entidadesEmpresas = jpaEmpresaRepository.findAll();
        List<Empresa> empresas = new ArrayList<>();

        for (empresaEntity entity : entidadesEmpresas) {
            Empresa empresa = new Empresa();
            empresa.setIdUsuario(entity.getIdUsuario());
            empresa.setNombreEmpresa(entity.getNombreEmpresa());
            empresa.setNIT(entity.getNit());
            empresa.setDireccionEmpresa(entity.getDireccionEmpresa());
            empresa.setTelefonoEmpresa(entity.getTelefonoEmpresa());
            empresa.setTipoEmpresa(entity.getTipoEmpresa());
            empresas.add(empresa);
        }

        return empresas;

    }
}
