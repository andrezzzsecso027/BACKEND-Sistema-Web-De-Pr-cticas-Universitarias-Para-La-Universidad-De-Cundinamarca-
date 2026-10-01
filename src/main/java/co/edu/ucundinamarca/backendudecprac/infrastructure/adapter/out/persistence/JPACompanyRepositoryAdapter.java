package co.edu.ucundinamarca.backendudecprac.infrastructure.adapter.out.persistence;

import co.edu.ucundinamarca.backendudecprac.domain.model.Company;
import co.edu.ucundinamarca.backendudecprac.domain.port.out.CompanyRepositoryPort;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class JPAempresaRepositoryAdapter implements CompanyRepositoryPort {

    private final JPAEmpresaRepository jpaEmpresaRepository;

    public JPAempresaRepositoryAdapter(JPAEmpresaRepository jpaEmpresaRepository) {
        this.jpaEmpresaRepository = jpaEmpresaRepository;
    }

    @Override
    public Company saveEmpresa(Company company) {
        CompanyEntity entity = new CompanyEntity();
        entity.setNit(company.getNIT());
        entity.setIdUsuario(company.getIdUser());
        entity.setNombreEmpresa(company.getNombreEmpresa());
        entity.setTipoEmpresa(company.getTipoEmpresa());
        entity.setDireccionEmpresa(company.getDireccionEmpresa());
        entity.setTelefonoEmpresa(company.getTelefonoEmpresa());
        jpaEmpresaRepository.save(entity);
        return company;
    }

    @Override
    public List<Company> findAllEmpresas() {
        List<CompanyEntity> entidadesEmpresas = jpaEmpresaRepository.findAll();
        List<Company> companies = new ArrayList<>();

        for (CompanyEntity entity : entidadesEmpresas) {
            Company company = new Company();
            company.setIdUser(entity.getIdUsuario());
            company.setNombreEmpresa(entity.getNombreEmpresa());
            company.setNIT(entity.getNit());
            company.setDireccionEmpresa(entity.getDireccionEmpresa());
            company.setTelefonoEmpresa(entity.getTelefonoEmpresa());
            company.setTipoEmpresa(entity.getTipoEmpresa());
            companies.add(company);
        }

        return companies;

    }

    @Override
    public boolean existsByNit(String nit) {
        return jpaEmpresaRepository.existsByNit(nit);
    }


}
