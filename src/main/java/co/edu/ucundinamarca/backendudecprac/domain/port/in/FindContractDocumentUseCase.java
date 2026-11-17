package co.edu.ucundinamarca.backendudecprac.domain.port.in;

import co.edu.ucundinamarca.backendudecprac.domain.model.ContractDocument;
import java.util.List;
import java.util.Optional;
public interface FindContractDocumentUseCase {
    List<ContractDocument> getContractsByCompanyNit(String nitCompany);
    Optional<ContractDocument> getContractByPostulationId(Long idPostulation);
    List<ContractDocument> getAllContracts();
}
