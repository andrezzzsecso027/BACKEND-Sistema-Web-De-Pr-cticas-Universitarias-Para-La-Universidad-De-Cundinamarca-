package co.edu.ucundinamarca.backendudecprac.infrastructure.adapter.out.persistence;

import co.edu.ucundinamarca.backendudecprac.domain.model.Company;
import co.edu.ucundinamarca.backendudecprac.domain.model.LegalRepresentative;
import co.edu.ucundinamarca.backendudecprac.domain.port.out.CompanyRepositoryPort;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class JPACompanyRepositoryAdapter implements CompanyRepositoryPort {

    private final JPACompanyRepository jpaCompanyRepository;

    public JPACompanyRepositoryAdapter(JPACompanyRepository jpaCompanyRepository) {
        this.jpaCompanyRepository = jpaCompanyRepository;
    }

    @Override
    public Company saveEmpresa(Company company) {
        CompanyEntity entity = new CompanyEntity();
        entity.setNit(company.getNit());
        entity.setIdUsuario(company.getIdUser());
        entity.setNombreEmpresa(company.getCompanyName());
        entity.setTipoEmpresa(company.getCompanyType());
        entity.setDireccionEmpresa(company.getCompanyAddress());
        entity.setTelefonoEmpresa(company.getCompanyPhoneNumber());
        entity.setRazonSocial(company.getLegalName());
        entity.setDepartamento(company.getDepartment());
        entity.setCiudad(company.getCity());
        entity.setEstadoVerificacion(company.getVerificationStatus());
        legalReprensentiveEmbeddable repEntity = new legalReprensentiveEmbeddable();
        repEntity.setNameRepresentive(company.getLegalRepresentative().getNameRepresentative());
        repEntity.setLastNameRepresentive(company.getLegalRepresentative().getLastNameRepresentative());
        repEntity.setCCrepresentive(company.getLegalRepresentative().getNumberDocument());

        entity.setRepresentanteLegal(repEntity);
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
            company.setNit(entity.getNit());
            company.setCompanyAddress(entity.getDireccionEmpresa());
            company.setCompanyPhoneNumber(entity.getTelefonoEmpresa());
            company.setCompanyType(entity.getTipoEmpresa());
            company.setLegalName(entity.getRazonSocial());
            company.setDepartment(entity.getDepartamento());
            company.setCity(entity.getCiudad());
            company.setVerificationStatus(entity.getEstadoVerificacion());
            if (entity.getRepresentanteLegal() != null) {
                LegalRepresentative representanteDominio = new LegalRepresentative(
                        entity.getRepresentanteLegal().getNameRepresentive(),
                        entity.getRepresentanteLegal().getLastNameRepresentive(),
                        entity.getRepresentanteLegal().getCCrepresentive()
                );
                company.setLegalRepresentative(representanteDominio);
            }
            companies.add(company);
        }

        return companies;

    }

    private Company mapToDomain(CompanyEntity entity) {
        Company company = new Company();
        company.setIdUser(entity.getIdUsuario());
        company.setCompanyName(entity.getNombreEmpresa());
        company.setNit(entity.getNit());
        company.setCompanyAddress(entity.getDireccionEmpresa());
        company.setCompanyPhoneNumber(entity.getTelefonoEmpresa());
        company.setCompanyType(entity.getTipoEmpresa());
        company.setLegalName(entity.getRazonSocial());
        company.setDepartment(entity.getDepartamento());
        company.setCity(entity.getCiudad());
        company.setVerificationStatus(entity.getEstadoVerificacion());

        if (entity.getRepresentanteLegal() != null) {
            LegalRepresentative representante = new LegalRepresentative(
                    entity.getRepresentanteLegal().getNameRepresentive(),
                    entity.getRepresentanteLegal().getLastNameRepresentive(),
                    entity.getRepresentanteLegal().getCCrepresentive()
            );
            company.setLegalRepresentative(representante);
        }
        return company;
    }

    @Override
    public boolean existsByNit(String nit) {
        return jpaCompanyRepository.existsByNit(nit);
    }

    @Override
    public List<Company> findByVerificationStatus(String verificationStatus) {
        List<CompanyEntity> entidades = jpaCompanyRepository.findByEstadoVerificacion(verificationStatus);
        List<Company> companies = new ArrayList<>();
        for (CompanyEntity entity : entidades) {
            companies.add(mapToDomain(entity)); // Usamos el helper
        }
        return companies;
    }

    @Override
    public Optional<Company> findByNit(String nit) {
        Optional<CompanyEntity> entityOptional = jpaCompanyRepository.findByNit(nit);
        if (entityOptional.isPresent()) {
            return Optional.of(mapToDomain(entityOptional.get())); // Usamos el helper
        }
        return Optional.empty();
    }


}
