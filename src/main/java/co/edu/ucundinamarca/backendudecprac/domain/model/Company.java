package co.edu.ucundinamarca.backendudecprac.domain.model;

import co.edu.ucundinamarca.backendudecprac.domain.exceptions.BusinessRuleException;
import co.edu.ucundinamarca.backendudecprac.domain.exceptions.InvalidStateTransitionException;
import co.edu.ucundinamarca.backendudecprac.domain.model.status.CompanyVerificationStatus;

public class Company {
    private String nit;
    private Long idUser;
    private String legalName;
    private String department;
    private String city;
    private String companyName;
    private String companyType;
    private String companyAddress;
    private String companyPhoneNumber;
    private CompanyVerificationStatus verificationStatus = CompanyVerificationStatus.PENDIENTE;
    private LegalRepresentative legalRepresentative;
    private String logoUrl;
    private String rutPdfUrl;
    private String companyDescription;

    public Company() {
    }

    public Company(String nit, Long idUser, String legalName, String department, String city, String companyName, String companyType, String companyAddress, String companyPhoneNumber, CompanyVerificationStatus verificationStatus, LegalRepresentative legalRepresentative) {
        this.nit = nit;
        this.idUser = idUser;
        this.legalName = legalName;
        this.department = department;
        this.city = city;
        this.companyName = companyName;
        this.companyType = companyType;
        this.companyAddress = companyAddress;
        this.companyPhoneNumber = companyPhoneNumber;
        this.verificationStatus = verificationStatus;
        this.legalRepresentative = legalRepresentative;
    }

    // ------------------------- Comportamiento de dominio -------------------------

    /**
     * La Universidad solo puede aprobar una empresa que haya cargado su certificado RUT/NIT en PDF
     * (antes se aprobaban únicamente con los datos del formulario).
     */
    public void approve() {
        if (!hasCertificadoNit()) {
            throw new BusinessRuleException(
                    "No se puede aprobar la empresa: no ha cargado el certificado RUT / NIT en PDF.");
        }
        changeVerification(CompanyVerificationStatus.APROBADA);
    }

    /** true si la empresa adjuntó un certificado NIT en PDF accesible por https. */
    public boolean hasCertificadoNit() {
        if (rutPdfUrl == null || rutPdfUrl.isBlank()) return false;
        String url = rutPdfUrl.trim().toLowerCase(java.util.Locale.ROOT);
        return url.startsWith("https://") && url.endsWith(".pdf");
    }

    /** Rechaza una solicitud pendiente o inhabilita una empresa ya aprobada. */
    public void reject() {
        changeVerification(CompanyVerificationStatus.RECHAZADA);
    }

    /** Solo una empresa aprobada por la Universidad puede publicar ofertas. */
    public boolean isApproved() {
        return verificationStatus == CompanyVerificationStatus.APROBADA;
    }

    private void changeVerification(CompanyVerificationStatus target) {
        CompanyVerificationStatus current = verificationStatus == null ? CompanyVerificationStatus.PENDIENTE : verificationStatus;
        if (!current.canTransitionTo(target)) {
            throw new InvalidStateTransitionException("la empresa", current, target);
        }
        this.verificationStatus = target;
    }

    // ------------------------- Getters / setters -------------------------

    public CompanyVerificationStatus getVerificationStatus() {
        return verificationStatus;
    }

    public void setVerificationStatus(CompanyVerificationStatus verificationStatus) {
        this.verificationStatus = verificationStatus;
    }

    public String getRutPdfUrl() {
        return rutPdfUrl;
    }

    public void setRutPdfUrl(String rutPdfUrl) {
        this.rutPdfUrl = rutPdfUrl;
    }

    public String getCompanyDescription() {
        return companyDescription;
    }

    public void setCompanyDescription(String companyDescription) {
        this.companyDescription = companyDescription;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public LegalRepresentative getLegalRepresentative() {
        return legalRepresentative;
    }

    public void setLegalRepresentative(LegalRepresentative legalRepresentative) {
        this.legalRepresentative = legalRepresentative;
    }

    public String getCompanyPhoneNumber() {
        return companyPhoneNumber;
    }

    public void setCompanyPhoneNumber(String companyPhoneNumber) {
        this.companyPhoneNumber = companyPhoneNumber;
    }

    public String getCompanyAddress() {
        return companyAddress;
    }

    public void setCompanyAddress(String companyAddress) {
        this.companyAddress = companyAddress;
    }

    public String getCompanyType() {
        return companyType;
    }

    public void setCompanyType(String companyType) {
        this.companyType = companyType;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
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

    public Long getIdUser() {
        return idUser;
    }

    public void setIdUser(Long idUser) {
        this.idUser = idUser;
    }

    public String getNit() {
        return nit;
    }

    public void setNit(String nit) {
        this.nit = nit;
    }



}
