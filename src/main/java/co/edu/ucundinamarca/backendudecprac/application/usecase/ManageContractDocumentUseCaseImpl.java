package co.edu.ucundinamarca.backendudecprac.application.usecase;


import co.edu.ucundinamarca.backendudecprac.domain.model.status.ContractTrackingStatus;
import co.edu.ucundinamarca.backendudecprac.domain.exceptions.InvalidInputException;
import co.edu.ucundinamarca.backendudecprac.domain.model.ContractDocument;
import co.edu.ucundinamarca.backendudecprac.domain.port.in.FindContractDocumentUseCase;
import co.edu.ucundinamarca.backendudecprac.domain.port.in.SaveContractDocumentUseCase;
import co.edu.ucundinamarca.backendudecprac.domain.port.out.ContractDocumentRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ManageContractDocumentUseCaseImpl implements SaveContractDocumentUseCase, FindContractDocumentUseCase {
    private final ContractDocumentRepositoryPort repositoryPort;

    public ManageContractDocumentUseCaseImpl(ContractDocumentRepositoryPort repositoryPort) {
        this.repositoryPort = repositoryPort;
    }

    @Transactional
    @Override
    public ContractDocument saveOrUpdateContract(ContractDocument document) {
        if (document == null || document.getIdPostulation() == null) {
            throw new InvalidInputException("El ID de la postulación es obligatorio.");
        }
        if (document.getNitCompany() == null || document.getNitCompany().isBlank()) {
            throw new InvalidInputException("El NIT de la empresa es obligatorio.");
        }

        Optional<ContractDocument> existenteOpt = repositoryPort.findByPostulationId(document.getIdPostulation());

        if (existenteOpt.isPresent()) {
            ContractDocument existente = existenteOpt.get();
            aplicarCambios(existente, document);
            existente.setUploadDate(LocalDateTime.now());
            return repositoryPort.saveContractDocument(existente);
        }

        if (document.getTrackingStatus() == null) {
            document.setTrackingStatus(document.getFileUrl() != null ? ContractTrackingStatus.CONTRATO_FIRMADO : ContractTrackingStatus.EN_CURSO);
        }
        document.setUploadDate(LocalDateTime.now());
        return repositoryPort.saveContractDocument(document);
    }

    /** Actualiza solo lo que llega informado; el resto del contrato existente se conserva. */
    private static void aplicarCambios(ContractDocument existente, ContractDocument cambios) {
        if (tieneTexto(cambios.getFileUrl())) {
            existente.setFileUrl(cambios.getFileUrl());
        }
        if (tieneTexto(cambios.getFileName())) {
            existente.setFileName(cambios.getFileName());
        }
        if (cambios.getCompanyComments() != null) {
            existente.setCompanyComments(cambios.getCompanyComments());
        }
        if (cambios.getTrackingStatus() != null) {
            existente.setTrackingStatus(cambios.getTrackingStatus());
        }
    }

    private static boolean tieneTexto(String valor) {
        return valor != null && !valor.isBlank();
    }

    @Override
    public List<ContractDocument> getContractsByCompanyNit(String nitCompany) {
        return repositoryPort.findByNitCompany(nitCompany);
    }

    @Override
    public Optional<ContractDocument> getContractByPostulationId(Long idPostulation) {
        return repositoryPort.findByPostulationId(idPostulation);
    }

    @Override
    public List<ContractDocument> getAllContracts() {
        return repositoryPort.findAllContracts();
    }
}
