package co.edu.ucundinamarca.backendudecprac.application.usecase;

import co.edu.ucundinamarca.backendudecprac.domain.model.*;
import co.edu.ucundinamarca.backendudecprac.domain.port.in.AdminUseCase;
import co.edu.ucundinamarca.backendudecprac.domain.port.in.CreateNotificationUseCase;
import co.edu.ucundinamarca.backendudecprac.domain.port.out.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AdminUseCaseImpl implements AdminUseCase {
    private final CompanyRepositoryPort companyRepositoryPort;
    private final OfferRepositoryPort offerRepositoryPort;
    private final StudentRepositoryPort studentRepositoryPort;

    public AdminUseCaseImpl(CompanyRepositoryPort companyRepositoryPort, OfferRepositoryPort offerRepositoryPort, StudentRepositoryPort studentRepositoryPort) {
        this.companyRepositoryPort = companyRepositoryPort;
        this.offerRepositoryPort = offerRepositoryPort;
        this.studentRepositoryPort = studentRepositoryPort;
    }


    @Override
    public List<Student> getAllStudents() {
        return studentRepositoryPort.findAllStudents();
    }

    @Override
    public List<Company> getAllCompanies() {
        return companyRepositoryPort.findAllCompanies();
    }

    @Override
    public List<Offer> getAllOffers() {
        return offerRepositoryPort.findAllOffers();
    }



}
