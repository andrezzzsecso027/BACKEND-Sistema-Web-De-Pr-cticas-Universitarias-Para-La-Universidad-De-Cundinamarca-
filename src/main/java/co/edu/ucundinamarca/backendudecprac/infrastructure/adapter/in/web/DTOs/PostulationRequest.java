package co.edu.ucundinamarca.backendudecprac.infrastructure.adapter.in.web.DTOs;

public class PostulationRequest {
    private Long idOffer;
    private String documentNumberStudent;

    public Long getIdOffer() {
        return idOffer;
    }

    public void setIdOffer(Long idOffer) {
        this.idOffer = idOffer;
    }

    public String getDocumentNumberStudent() {
        return documentNumberStudent;
    }

    public void setDocumentNumberStudent(String documentNumberStudent) {
        this.documentNumberStudent = documentNumberStudent;
    }
}
