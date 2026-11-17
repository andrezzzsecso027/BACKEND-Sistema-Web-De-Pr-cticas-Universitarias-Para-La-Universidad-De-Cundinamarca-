package co.edu.ucundinamarca.backendudecprac.infrastructure.adapter.in.web.dtos;

import co.edu.ucundinamarca.backendudecprac.domain.model.Company;
import co.edu.ucundinamarca.backendudecprac.domain.model.LegalRepresentative;

/**
 * Cuerpo de PUT /api/registro/empresas/nit/{nit}/perfil.
 * No expone NIT ni estado de verificación: esos campos no se pueden cambiar desde el perfil.
 */
public class CompanyProfileUpdateRequest {
    private String companyName;
    private String legalName;
    private String companyType;
    private String companyPhoneNumber;
    private String department;
    private String city;
    private String companyAddress;
    private String companyDescription;
    private String logoUrl;
    private String rutPdfUrl;
    private LegalRepresentativeRequest legalRepresentative;

    public Company toDomain() {
        Company company = new Company();
        company.setVerificationStatus(null); // el estado de verificación no se modifica desde el perfil
        company.setCompanyName(companyName);
        company.setLegalName(legalName);
        company.setCompanyType(companyType);
        company.setCompanyPhoneNumber(companyPhoneNumber);
        company.setDepartment(department);
        company.setCity(city);
        company.setCompanyAddress(companyAddress);
        company.setCompanyDescription(companyDescription);
        company.setLogoUrl(logoUrl);
        company.setRutPdfUrl(rutPdfUrl);
        if (legalRepresentative != null) {
            company.setLegalRepresentative(new LegalRepresentative(
                    legalRepresentative.getNameRepresentative(),
                    legalRepresentative.getLastNameRepresentative(),
                    legalRepresentative.getNumberDocument()));
        }
        return company;
    }

    public static class LegalRepresentativeRequest {
        private String nameRepresentative;
        private String lastNameRepresentative;
        private String numberDocument;

        public String getNameRepresentative() { return nameRepresentative; }
        public void setNameRepresentative(String nameRepresentative) { this.nameRepresentative = nameRepresentative; }
        public String getLastNameRepresentative() { return lastNameRepresentative; }
        public void setLastNameRepresentative(String lastNameRepresentative) { this.lastNameRepresentative = lastNameRepresentative; }
        public String getNumberDocument() { return numberDocument; }
        public void setNumberDocument(String numberDocument) { this.numberDocument = numberDocument; }
    }

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }
    public String getLegalName() { return legalName; }
    public void setLegalName(String legalName) { this.legalName = legalName; }
    public String getCompanyType() { return companyType; }
    public void setCompanyType(String companyType) { this.companyType = companyType; }
    public String getCompanyPhoneNumber() { return companyPhoneNumber; }
    public void setCompanyPhoneNumber(String companyPhoneNumber) { this.companyPhoneNumber = companyPhoneNumber; }
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public String getCompanyAddress() { return companyAddress; }
    public void setCompanyAddress(String companyAddress) { this.companyAddress = companyAddress; }
    public String getCompanyDescription() { return companyDescription; }
    public void setCompanyDescription(String companyDescription) { this.companyDescription = companyDescription; }
    public String getLogoUrl() { return logoUrl; }
    public void setLogoUrl(String logoUrl) { this.logoUrl = logoUrl; }
    public String getRutPdfUrl() { return rutPdfUrl; }
    public void setRutPdfUrl(String rutPdfUrl) { this.rutPdfUrl = rutPdfUrl; }
    public LegalRepresentativeRequest getLegalRepresentative() { return legalRepresentative; }
    public void setLegalRepresentative(LegalRepresentativeRequest legalRepresentative) { this.legalRepresentative = legalRepresentative; }
}
