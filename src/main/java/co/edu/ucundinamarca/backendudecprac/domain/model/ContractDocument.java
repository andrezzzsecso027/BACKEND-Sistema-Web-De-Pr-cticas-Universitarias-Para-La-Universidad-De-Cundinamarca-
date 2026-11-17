package co.edu.ucundinamarca.backendudecprac.domain.model;

import co.edu.ucundinamarca.backendudecprac.domain.model.status.ContractTrackingStatus;

import java.time.LocalDateTime;

public class ContractDocument {
    private Long idDocument;
    private Long idPostulation;
    private String nitCompany;
    private String documentStudent;
    private String studentFullName;
    private String offerName;
    private String fileName;
    private String fileUrl;
    private String companyComments;
    private ContractTrackingStatus trackingStatus;
    private LocalDateTime uploadDate;

    public Long getIdDocument() {
        return idDocument;
    }

    public void setIdDocument(Long idDocument) {
        this.idDocument = idDocument;
    }

    public Long getIdPostulation() {
        return idPostulation;
    }

    public void setIdPostulation(Long idPostulation) {
        this.idPostulation = idPostulation;
    }

    public String getNitCompany() {
        return nitCompany;
    }

    public void setNitCompany(String nitCompany) {
        this.nitCompany = nitCompany;
    }

    public String getDocumentStudent() {
        return documentStudent;
    }

    public void setDocumentStudent(String documentStudent) {
        this.documentStudent = documentStudent;
    }

    public String getOfferName() {
        return offerName;
    }

    public void setOfferName(String offerName) {
        this.offerName = offerName;
    }

    public String getStudentFullName() {
        return studentFullName;
    }

    public void setStudentFullName(String studentFullName) {
        this.studentFullName = studentFullName;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public String getCompanyComments() {
        return companyComments;
    }

    public void setCompanyComments(String companyComments) {
        this.companyComments = companyComments;
    }

    public ContractTrackingStatus getTrackingStatus() {
        return trackingStatus;
    }

    public void setTrackingStatus(ContractTrackingStatus trackingStatus) {
        this.trackingStatus = trackingStatus;
    }

    public LocalDateTime getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(LocalDateTime uploadDate) {
        this.uploadDate = uploadDate;
    }

}
