package co.edu.ucundinamarca.backendudecprac.domain.port.out;

import co.edu.ucundinamarca.backendudecprac.domain.model.ContractDocument;

import java.util.List;
import java.util.Optional;

public interface ContractDocumentRepositoryPort {
    ContractDocument saveContractDocument(ContractDocument document);
    Optional<ContractDocument> findByPostulationId(Long idPostulation);
    List<ContractDocument> findByNitCompany(String nitCompany);
    List<ContractDocument> findAllContracts();
}
