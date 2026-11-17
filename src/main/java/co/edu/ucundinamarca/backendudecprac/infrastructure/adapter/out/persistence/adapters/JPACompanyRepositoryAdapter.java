package co.edu.ucundinamarca.backendudecprac.infrastructure.adapter.out.persistence.adapters;

import co.edu.ucundinamarca.backendudecprac.domain.exceptions.ResourceAlreadyExistsException;
import co.edu.ucundinamarca.backendudecprac.domain.model.status.CompanyVerificationStatus;
import co.edu.ucundinamarca.backendudecprac.domain.model.Company;
import co.edu.ucundinamarca.backendudecprac.domain.model.LegalRepresentative;
import co.edu.ucundinamarca.backendudecprac.domain.port.out.CompanyRepositoryPort;
import co.edu.ucundinamarca.backendudecprac.infrastructure.adapter.out.persistence.entities.CompanyEntity;
import co.edu.ucundinamarca.backendudecprac.infrastructure.adapter.out.persistence.entities.legalReprensentiveEmbeddable;
import co.edu.ucundinamarca.backendudecprac.infrastructure.adapter.out.persistence.jpa.JPACompanyRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class JPACompanyRepositoryAdapter implements CompanyRepositoryPort {

    private final JPACompanyRepository jpaCompanyRepository;

    @PersistenceContext
    private EntityManager entityManager;

    public JPACompanyRepositoryAdapter(JPACompanyRepository jpaCompanyRepository) {
        this.jpaCompanyRepository = jpaCompanyRepository;
    }

    /**
     * INSERT explícito (persist + flush) en lugar de save(): como el NIT es una llave asignada,
     * save() hace merge y, ante dos registros simultáneos del mismo NIT, el segundo SOBRESCRIBÍA
     * la empresa del primero (respondiendo 201 y dejando usuarios huérfanos). Con persist la BD
     * rechaza el duplicado por la llave primaria y se traduce a una excepción de dominio (409);
     * la transacción del caso de uso hace rollback del usuario creado.
     */
    @Override
    public Company createCompany(Company company) {
        try {
            entityManager.persist(toEntity(company));
            entityManager.flush();
        } catch (EntityExistsException | ConstraintViolationException e) {
            throw new ResourceAlreadyExistsException("Ya existe una cuenta con ese nit");
        }
        return company;
    }

    @Override
    public Company saveCompany(Company company) {
        jpaCompanyRepository.save(toEntity(company));
        return company;
    }

    private CompanyEntity toEntity(Company company) {
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
        entity.setEstadoVerificacion(company.getVerificationStatus() == null ? CompanyVerificationStatus.PENDIENTE.name() : company.getVerificationStatus().name());
        entity.setUrlLogo(company.getLogoUrl());
        entity.setUrlPdfNit(company.getRutPdfUrl());
        entity.setDescripcionEmpresa(company.getCompanyDescription());
        if (company.getLegalRepresentative() != null) {
            legalReprensentiveEmbeddable repEntity = new legalReprensentiveEmbeddable();
            repEntity.setNameRepresentive(company.getLegalRepresentative().getNameRepresentative());
            repEntity.setLastNameRepresentive(company.getLegalRepresentative().getLastNameRepresentative());
            repEntity.setCCrepresentive(company.getLegalRepresentative().getNumberDocument());
            entity.setRepresentanteLegal(repEntity);
        }
        return entity;
    }

    @Override
    public List<Company> findAllCompanies() {
        List<CompanyEntity> entidadesEmpresas = jpaCompanyRepository.findAll();
        List<Company> companies = new ArrayList<>();
        for (CompanyEntity entity : entidadesEmpresas) {
            companies.add(mapToDomain(entity));
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
        company.setVerificationStatus(entity.getEstadoVerificacion() == null ? CompanyVerificationStatus.PENDIENTE : CompanyVerificationStatus.from(entity.getEstadoVerificacion()));

        company.setLogoUrl(entity.getUrlLogo());
        company.setRutPdfUrl(entity.getUrlPdfNit());
        company.setCompanyDescription(entity.getDescripcionEmpresa());

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
    public List<Company> findByVerificationStatus(CompanyVerificationStatus verificationStatus) {

        List<Company> companies = new ArrayList<>();
        List<CompanyEntity> entidadesEmpresas = jpaCompanyRepository.findByEstadoVerificacion(verificationStatus.name()); // antes: findAll() ignoraba el filtro
        for (CompanyEntity entity : entidadesEmpresas) {
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
