package co.edu.ucundinamarca.backendudecprac.infrastructure.adapter.out.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class legalReprensentiveEmbeddable {

    @Column(name = "nombre_representante", length = 100)
    private String nameRepresentive;
    @Column(name = "apellido_representante", length = 100)
    private String lastNameRepresentive;
    @Column(name = "CC_representante", length = 100)
    private String CCrepresentive;

    public legalReprensentiveEmbeddable() {
    }

    public legalReprensentiveEmbeddable(String nameRepresentive, String lastNameRepresentive, String CCrepresentive) {
        this.nameRepresentive = nameRepresentive;
        this.lastNameRepresentive = lastNameRepresentive;
        this.CCrepresentive = CCrepresentive;
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

    public String getCCrepresentive() {
        return CCrepresentive;
    }

    public void setCCrepresentive(String CCrepresentive) {
        this.CCrepresentive = CCrepresentive;
    }
}
