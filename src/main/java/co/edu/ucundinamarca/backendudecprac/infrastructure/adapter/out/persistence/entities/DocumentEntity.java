package co.edu.ucundinamarca.backendudecprac.infrastructure.adapter.out.persistence.entities;



import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "documentos")
public class DocumentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_documento")
    private Long idDocument;

    @Column(name = "id_postulacion", nullable = false, unique = true)
    private Long idPostulation;

    @Column(name = "nit_empresa", nullable = false)
    private String nitCompany;

    @Column(name = "cc_estudiante", nullable = false)
    private String documentStudent;

    @Column(name = "nombre_estudiante")
    private String studentFullName;

    @Column(name = "nombre_oferta")
    private String offerName;

    @Column(name = "nombre_archivo")
    private String fileName;

    @Column(name = "ruta_archivo", length = 500)
    private String fileUrl;

    @Column(name = "comentarios_empresa", columnDefinition = "TEXT")
    private String companyComments;

    @Column(name = "estado_seguimiento")
    private String trackingStatus;

    @Column(name = "fecha_subida", nullable = false)
    private LocalDateTime uploadDate;

    // Getters y Setters
    public Long getIdDocument() { return idDocument; }
    public void setIdDocument(Long idDocument) { this.idDocument = idDocument; }
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
    public LocalDateTime getUploadDate() { return uploadDate; }
    public void setUploadDate(LocalDateTime uploadDate) { this.uploadDate = uploadDate; }
}
