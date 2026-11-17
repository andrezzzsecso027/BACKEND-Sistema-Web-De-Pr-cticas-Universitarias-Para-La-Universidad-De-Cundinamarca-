package co.edu.ucundinamarca.backendudecprac.infrastructure.adapter.out.persistence.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class LegalRepresentativeEmbeddable {

    @Column(name = "nombre_representante", length = 100)
    private String nameRepresentive;
    @Column(name = "apellido_representante", length = 100)
    private String lastNameRepresentive;
    @Column(name = "CC_representante", length = 100)
    private String ccRepresentive;

    public LegalRepresentativeEmbeddable() {
    }

    public LegalRepresentativeEmbeddable(String nameRepresentive, String lastNameRepresentive, String ccRepresentive) {
        this.nameRepresentive = nameRepresentive;
        this.lastNameRepresentive = lastNameRepresentive;
        this.ccRepresentive = ccRepresentive;
    }

    public String getNameRepresentive() {
        return nameRepresentive;
    }

    public void setNameRepresentive(String nameRepresentive) {
        this.nameRepresentive = nameRepresentive;
    }

    public String getLastNameRepresentive() {
        return lastNameRepresentive;
    }

    public void setLastNameRepresentive(String lastNameRepresentive) {
        this.lastNameRepresentive = lastNameRepresentive;
    }

    public String getCcRepresentive() {
        return ccRepresentive;
    }

    public void setCcRepresentive(String ccRepresentive) {
        this.ccRepresentive = ccRepresentive;
    }
}
