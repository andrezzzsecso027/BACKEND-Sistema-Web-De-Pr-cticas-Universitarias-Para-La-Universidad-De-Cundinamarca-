package co.edu.ucundinamarca.backendudecprac.domain.port.in;

import co.edu.ucundinamarca.backendudecprac.domain.model.Company;
import co.edu.ucundinamarca.backendudecprac.domain.model.Offer;
import co.edu.ucundinamarca.backendudecprac.domain.model.Student;

import java.util.List;
public interface AdminUseCase {
    List<Student> getAllStudents();
    List<Company> getAllCompanies();
    List<Offer> getAllOffers();
}
