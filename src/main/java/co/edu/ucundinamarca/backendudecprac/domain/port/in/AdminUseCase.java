package co.edu.ucundinamarca.backendudecprac.domain.port.in;

import co.edu.ucundinamarca.backendudecprac.domain.model.Company;
import java.util.List;
public interface AdminUseCase {
    List<Company> getCompaniesPending();
    void verifyCompany(String nit, boolean isApproved);
}
