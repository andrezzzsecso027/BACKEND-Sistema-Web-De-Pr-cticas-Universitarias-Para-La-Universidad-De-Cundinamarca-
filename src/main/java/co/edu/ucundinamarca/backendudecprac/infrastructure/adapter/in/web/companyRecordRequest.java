package co.edu.ucundinamarca.backendudecprac.infrastructure.adapter.in.web;

public class companyRecordRequest {
    private String nit;
    private String nombreEmpresa;
    private String tipoEmpresa;
    private String direccionEmpresa;
    private String telefonoEmpresa;
    private String correo;
    private String contrasenia;
    private String nameRepresentive;
    private  String lastNameRepresentive;
    private String numberDocument;
    private String legalName;
    private String department;
    private String city;

    public String getNumberDocument() {
        return numberDocument;
    }

    public void setNumberDocument(String numberDocument) {
        this.numberDocument = numberDocument;
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
    public String getNit() {
        return nit;
    }

    public void setNit(String nit) {
        this.nit = nit;
    }

    public String getNombreEmpresa() {
        return nombreEmpresa;
    }

    public void setNombreEmpresa(String nombreEmpresa) {
        this.nombreEmpresa = nombreEmpresa;
    }

    public String getTipoEmpresa() {
        return tipoEmpresa;
    }

    public void setTipoEmpresa(String tipoEmpresa) {
        this.tipoEmpresa = tipoEmpresa;
    }

    public String getDireccionEmpresa() {
        return direccionEmpresa;
    }

    public void setDireccionEmpresa(String direccionEmpresa) {
        this.direccionEmpresa = direccionEmpresa;
    }

    public String getTelefonoEmpresa() {
        return telefonoEmpresa;
    }

    public void setTelefonoEmpresa(String telefonoEmpresa) {
        this.telefonoEmpresa = telefonoEmpresa;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getContrasenia() {
        return contrasenia;
    }
    public void setContrasenia(String contrasenia) {
        this.contrasenia = contrasenia;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getLegalName() {
        return legalName;
    }

    public void setLegalName(String legalName) {
        this.legalName = legalName;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

}
