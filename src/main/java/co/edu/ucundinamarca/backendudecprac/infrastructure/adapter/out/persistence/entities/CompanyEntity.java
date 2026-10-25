package co.edu.ucundinamarca.backendudecprac.infrastructure.adapter.out.persistence.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "empresas")
public class CompanyEntity {

    @Id
    private String nit;
    @Column(name = "id_usuario", nullable = false)
    private Long idUsuario;

    @Column(nullable = false)
    private String nombreEmpresa;

    private String tipoEmpresa;
    private String direccionEmpresa;
    private String telefonoEmpresa;
    private String razonSocial; // legalName
    private String departamento; // department
    private String ciudad;
    @Column(name = "estado_verificacion")
    private String estadoVerificacion = "PENDIENTE";
    @Embedded
    private legalReprensentiveEmbeddable representanteLegal;

    @Column(name = "url_logo", length = 500)
    private String urlLogo;

    @Column(name = "url_pdf_nit", length = 500)
    private String urlPdfNit;

    @Column(name = "descripcion_empresa", length = 1000)
    private String descripcionEmpresa;

    public CompanyEntity() {}

    public Long getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Long idUsuario) {
        this.idUsuario = idUsuario;
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
    public legalReprensentiveEmbeddable getRepresentanteLegal() {
        return representanteLegal;
    }

    public void setRepresentanteLegal(legalReprensentiveEmbeddable representanteLegal) {
        this.representanteLegal = representanteLegal;
    }
    public String getDepartamento() {
        return departamento;
    }

    public void setDepartamento(String departamento) {
        this.departamento = departamento;
    }

    public String getRazonSocial() {
        return razonSocial;
    }

    public void setRazonSocial(String razonSocial) {
        this.razonSocial = razonSocial;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public String getEstadoVerificacion() {
        return estadoVerificacion;
    }

    public void setEstadoVerificacion(String estadoVerificacion) {
        this.estadoVerificacion = estadoVerificacion;
    }
    public String getDescripcionEmpresa() {
        return descripcionEmpresa;
    }

    public void setDescripcionEmpresa(String descripcionEmpresa) {
        this.descripcionEmpresa = descripcionEmpresa;
    }

    public String getUrlLogo() {
        return urlLogo;
    }

    public void setUrlLogo(String urlLogo) {
        this.urlLogo = urlLogo;
    }

    public String getUrlPdfNit() {
        return urlPdfNit;
    }

    public void setUrlPdfNit(String urlPdfNit) {
        this.urlPdfNit = urlPdfNit;
    }


}
