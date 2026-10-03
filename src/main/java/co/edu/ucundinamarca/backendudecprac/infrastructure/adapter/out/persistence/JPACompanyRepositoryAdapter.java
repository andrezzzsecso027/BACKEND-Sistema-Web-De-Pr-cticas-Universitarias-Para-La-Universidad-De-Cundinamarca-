package co.edu.ucundinamarca.backendudecprac.infrastructure.adapter.out.persistence;

import co.edu.ucundinamarca.backendudecprac.domain.model.Company;
import co.edu.ucundinamarca.backendudecprac.domain.port.out.CompanyRepositoryPort;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class JPACompanyRepositoryAdapter implements CompanyRepositoryPort {

    private final JPACompanyRepository jpaCompanyRepository;

    public JPACompanyRepositoryAdapter(JPACompanyRepository jpaCompanyRepository) {
        this.jpaCompanyRepository = jpaCompanyRepository;
    }

    @Override
    public Company saveEmpresa(Company company) {
        CompanyEntity entity = new CompanyEntity();
        entity.setNit(company.getNIT());
        entity.setIdUsuario(company.getIdUser());
        entity.setNombreEmpresa(company.getCompanyName());
        entity.setTipoEmpresa(company.getCompanyType());
        entity.setDireccionEmpresa(company.getCompanyAddres());
        entity.setTelefonoEmpresa(company.getCompanyPhoneNumber());
        jpaCompanyRepository.save(entity);
        return company;
    }

    @Override
    public List<Company> findAllCompanies() {
        List<CompanyEntity> entidadesEmpresas = jpaCompanyRepository.findAll();
        List<Company> companies = new ArrayList<>();

        for (CompanyEntity entity : entidadesEmpresas) {
            Company company = new Company();
            company.setIdUser(entity.getIdUsuario());
            company.setCompanyName(entity.getNombreEmpresa());
            company.setNIT(entity.getNit());
            company.setCompanyAddres(entity.getDireccionEmpresa());
            company.setCompanyPhoneNumber(entity.getTelefonoEmpresa());
            company.setCompanyType(entity.getTipoEmpresa());
            companies.add(company);
        }

        return companies;

    }

    @Override
    public boolean existsByNit(String nit) {
        return jpaCompanyRepository.existsByNit(nit);
    }


}
