package co.edu.ucundinamarca.backendudecprac.infrastructure.adapter.out.persistence.adapters;


import co.edu.ucundinamarca.backendudecprac.domain.model.status.ContractTrackingStatus;
import co.edu.ucundinamarca.backendudecprac.domain.model.ContractDocument;
import co.edu.ucundinamarca.backendudecprac.domain.port.out.ContractDocumentRepositoryPort;
import co.edu.ucundinamarca.backendudecprac.infrastructure.adapter.out.persistence.entities.DocumentEntity;
import co.edu.ucundinamarca.backendudecprac.infrastructure.adapter.out.persistence.jpa.DocumentJPArepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class ContractDocumentRepositoryAdapter implements ContractDocumentRepositoryPort {


    private final DocumentJPArepository jpaRepository;

    public ContractDocumentRepositoryAdapter(DocumentJPArepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public ContractDocument saveContractDocument(ContractDocument doc) {
        DocumentEntity entity = toEntity(doc);
        return toDomain(jpaRepository.save(entity));
    }

    @Override
    public Optional<ContractDocument> findByPostulationId(Long idPostulation) {
        return jpaRepository.findByIdPostulation(idPostulation).map(this::toDomain);
    }

    @Override
    public List<ContractDocument> findByNitCompany(String nitCompany) {
        return jpaRepository.findByNitCompany(nitCompany).stream().map(this::toDomain).toList();
    }

    @Override
    public List<ContractDocument> findAllContracts() {
        return jpaRepository.findAll().stream().map(this::toDomain).toList();
    }

    private DocumentEntity toEntity(ContractDocument d) {
        DocumentEntity e = new DocumentEntity();
        e.setIdDocument(d.getIdDocument());
        e.setIdPostulation(d.getIdPostulation());
        e.setNitCompany(d.getNitCompany());
        e.setDocumentStudent(d.getDocumentStudent());
        e.setStudentFullName(d.getStudentFullName());
        e.setOfferName(d.getOfferName());
        e.setFileName(d.getFileName());
        e.setFileUrl(d.getFileUrl());
        e.setCompanyComments(d.getCompanyComments());
        e.setTrackingStatus(d.getTrackingStatus() == null ? null : d.getTrackingStatus().name());
        e.setUploadDate(d.getUploadDate());
        return e;
    }

    private ContractDocument toDomain(DocumentEntity e) {
        ContractDocument d = new ContractDocument();
        d.setIdDocument(e.getIdDocument());
        d.setIdPostulation(e.getIdPostulation());
        d.setNitCompany(e.getNitCompany());
        d.setDocumentStudent(e.getDocumentStudent());
        d.setStudentFullName(e.getStudentFullName());
        d.setOfferName(e.getOfferName());
        d.setFileName(e.getFileName());
        d.setFileUrl(e.getFileUrl());
        d.setCompanyComments(e.getCompanyComments());
        d.setTrackingStatus(e.getTrackingStatus() == null ? null : ContractTrackingStatus.from(e.getTrackingStatus()));
        d.setUploadDate(e.getUploadDate());
        return d;
    }
}
