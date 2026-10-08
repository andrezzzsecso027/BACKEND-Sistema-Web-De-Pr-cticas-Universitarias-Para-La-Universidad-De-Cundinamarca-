package co.edu.ucundinamarca.backendudecprac.domain.model;

public class LegalRepresentative {
    private String nameRepresentative;
    private String lastNameRepresentative;
    private String numberDocument;

    public LegalRepresentative(){

    }
    public LegalRepresentative(String nameRepresentative, String lastNameRepresentative, String numberDocument) {
        this.nameRepresentative = nameRepresentative;
        this.lastNameRepresentative = lastNameRepresentative;
        this.numberDocument = numberDocument;
    }

    public String getNameRepresentative() {
        return nameRepresentative;
    }

    public void setNameRepresentative(String nameRepresentative) {
        this.nameRepresentative = nameRepresentative;
    }

    public String getLastNameRepresentative() {
        return lastNameRepresentative;
    }

    public void setLastNameRepresentative(String lastNameRepresentative) {
        this.lastNameRepresentative = lastNameRepresentative;
    }

    public String getNumberDocument() {
        return numberDocument;
    }

    public void setNumberDocument(String numberDocument) {
        this.numberDocument = numberDocument;
    }

}
