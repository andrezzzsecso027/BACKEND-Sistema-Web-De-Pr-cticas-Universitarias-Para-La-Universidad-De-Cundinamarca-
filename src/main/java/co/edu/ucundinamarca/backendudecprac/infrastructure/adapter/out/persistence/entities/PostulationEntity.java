package co.edu.ucundinamarca.backendudecprac.infrastructure.adapter.out.persistence.entities;


import jakarta.persistence.*;


import java.time.LocalDateTime;

@Entity
@Table(name="Postulaciones")
public class PostulationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id_postulacion",nullable = false,unique = true)
    private Long idPostulation;
    @Column(name="id_oferta",nullable = false)
    private Long idOffer;
    @Column(name="CC_estudiante",nullable = false)
    private String numberDocumentStudent;
    @Column(name = "estado_postulacion",nullable = false)
    private String statusPostulation;
    @Column(name = "fecha_postulacion",nullable = false)
    private LocalDateTime dateOfPostulation;
    @Column(name = "fecha_actualizacion",nullable = false)
    private LocalDateTime dateOfActualizacion;
    @Column(name = "observaciones",nullable = true)
    private String observations;

    public Long getIdPostulation() {
        return idPostulation;
    }

    public void setIdPostulation(Long idPostulation) {
        this.idPostulation = idPostulation;
    }

    public Long getIdOffer() {
        return idOffer;
    }

    public void setIdOffer(Long idOffer) {
        this.idOffer = idOffer;
    }

    public String getNumberDocumentStudent() {
        return numberDocumentStudent;
    }

    public void setNumberDocumentStudent(String numberDocumentStudent) {
        this.numberDocumentStudent = numberDocumentStudent;
    }

    public String getStatusPostulation() {
        return statusPostulation;
    }

    public void setStatusPostulation(String statusPostulation) {
        this.statusPostulation = statusPostulation;
    }

    public LocalDateTime getDateOfPostulation() {
        return dateOfPostulation;
    }

    public void setDateOfPostulation(LocalDateTime dateOfPostulation) {
        this.dateOfPostulation = dateOfPostulation;
    }

    public LocalDateTime getDateOfActualizacion() {
        return dateOfActualizacion;
    }

    public void setDateOfActualizacion(LocalDateTime dateOfActualizacion) {
        this.dateOfActualizacion = dateOfActualizacion;
    }

    public String getObservations() {
        return observations;
    }

    public void setObservations(String observations) {
        this.observations = observations;
    }
}
