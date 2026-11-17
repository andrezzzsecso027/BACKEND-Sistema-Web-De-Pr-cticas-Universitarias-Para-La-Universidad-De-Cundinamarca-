package co.edu.ucundinamarca.backendudecprac.domain.port.in;

import co.edu.ucundinamarca.backendudecprac.domain.model.ContractDocument;

public interface SaveContractDocumentUseCase {
    ContractDocument saveOrUpdateContract(ContractDocument document);
}
