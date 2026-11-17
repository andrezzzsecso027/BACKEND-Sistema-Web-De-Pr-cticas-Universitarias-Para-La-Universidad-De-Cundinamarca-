package co.edu.ucundinamarca.backendudecprac.infrastructure.adapter.in.web.dtos;

import co.edu.ucundinamarca.backendudecprac.domain.model.ContractDocument;
import co.edu.ucundinamarca.backendudecprac.domain.model.status.ContractTrackingStatus;

/**
 * Cuerpo de POST /api/documentos/seguimiento.
 * No acepta idDocument ni uploadDate: los asigna el caso de uso.
 */
public class ContractDocumentRequest {
    private Long idPostulation;
    private String nitCompany;
    private String documentStudent;
    private String studentFullName;
    private String offerName;
    private String fileName;
    private String fileUrl;
    private String companyComments;
    private String trackingStatus;

    public ContractDocument toDomain() {
        ContractDocument document = new ContractDocument();
        document.setIdPostulation(idPostulation);
        document.setNitCompany(nitCompany);
        document.setDocumentStudent(documentStudent);
        document.setStudentFullName(studentFullName);
        document.setOfferName(offerName);
        document.setFileName(fileName);
        document.setFileUrl(fileUrl);
        document.setCompanyComments(companyComments);
        // Texto -> enum; vacío significa "que el caso de uso decida el estado inicial"
        document.setTrackingStatus(trackingStatus == null || trackingStatus.isBlank()
                ? null : ContractTrackingStatus.from(trackingStatus));
        return document;
    }

    public Long getIdPostulation() { return idPostulation; }
    public void setIdPostulation(Long idPostulation) { this.idPostulation = idPostulation; }
    public String getNitCompany() { return nitCompany; }
    public void setNitCompany(String nitCompany) { this.nitCompany = nitCompany; }
    public String getDocumentStudent() { return documentStudent; }
    public void setDocumentStudent(String documentStudent) { this.documentStudent = documentStudent; }
    public String getStudentFullName() { return studentFullName; }
    public void setStudentFullName(String studentFullName) { this.studentFullName = studentFullName; }
    public String getOfferName() { return offerName; }
    public void setOfferName(String offerName) { this.offerName = offerName; }
    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }
    public String getFileUrl() { return fileUrl; }
    public void setFileUrl(String fileUrl) { this.fileUrl = fileUrl; }
    public String getCompanyComments() { return companyComments; }
    public void setCompanyComments(String companyComments) { this.companyComments = companyComments; }
    public String getTrackingStatus() { return trackingStatus; }
    public void setTrackingStatus(String trackingStatus) { this.trackingStatus = trackingStatus; }
}
