package co.edu.ucundinamarca.backendudecprac.domain.model;

public class Company {
    private String NIT;
    private long idUser;
    private String CompanyName;
    private String CompanyType;
    private String CompanyAddres;
    private String CompanyPhoneNumber;


    public Company() {
    }


    public Company(String NIT, Long idUser, String CompanyName, String CompanyType,
                   String CompanyAddres, String CompanyPhoneNumber) {
        this.NIT = NIT;
        this.idUser = idUser;
        this.CompanyName = CompanyName;
        this.CompanyType = CompanyType;
        this.CompanyAddres = CompanyAddres;
        this.CompanyPhoneNumber = CompanyPhoneNumber;
    }

    public String getCompanyAddres() {
        return CompanyAddres;
    }

    public void setCompanyAddres(String companyAddres) {
        this.CompanyAddres = companyAddres;
    }

    public String getNIT() {
        return NIT;
    }

    public void setNIT(String NIT) {
        this.NIT = NIT;
    }

    public long getIdUser() {
        return idUser;
    }

    public void setIdUser(long idUser) {
        this.idUser = idUser;
    }

    public String getCompanyName() {
        return CompanyName;
    }

    public void setCompanyName(String companyName) {
        this.CompanyName = companyName;
    }

    public String getCompanyType() {
        return CompanyType;
    }

    public void setCompanyType(String companyType) {
        this.CompanyType = companyType;
    }

    public String getCompanyPhoneNumber() {
        return CompanyPhoneNumber;
    }

    public void setCompanyPhoneNumber(String companyPhoneNumber) {
        this.CompanyPhoneNumber = companyPhoneNumber;
    }
}
