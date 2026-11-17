package co.edu.ucundinamarca.backendudecprac.application.usecase;

import co.edu.ucundinamarca.backendudecprac.domain.exceptions.ResourceNotFoundException;
import co.edu.ucundinamarca.backendudecprac.domain.model.Company;
import co.edu.ucundinamarca.backendudecprac.domain.model.Offer;
import co.edu.ucundinamarca.backendudecprac.domain.model.Student;
import co.edu.ucundinamarca.backendudecprac.domain.model.StudentPostulationSummary;
import co.edu.ucundinamarca.backendudecprac.domain.model.User;
import co.edu.ucundinamarca.backendudecprac.domain.port.in.StudentSelfServiceUseCase;
import co.edu.ucundinamarca.backendudecprac.domain.port.out.CompanyRepositoryPort;
import co.edu.ucundinamarca.backendudecprac.domain.port.out.OfferRepositoryPort;
import co.edu.ucundinamarca.backendudecprac.domain.port.out.PostulationRepositoryPort;
import co.edu.ucundinamarca.backendudecprac.domain.port.out.StudentRepositoryPort;
import co.edu.ucundinamarca.backendudecprac.domain.port.out.UserRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class StudentSelfServiceUseCaseImpl implements StudentSelfServiceUseCase {

    private final UserRepositoryPort userRepositoryPort;
    private final StudentRepositoryPort studentRepositoryPort;
    private final PostulationRepositoryPort postulationRepositoryPort;
    private final OfferRepositoryPort offerRepositoryPort;
    private final CompanyRepositoryPort companyRepositoryPort;

    public StudentSelfServiceUseCaseImpl(UserRepositoryPort userRepositoryPort,
                                         StudentRepositoryPort studentRepositoryPort,
                                         PostulationRepositoryPort postulationRepositoryPort,
                                         OfferRepositoryPort offerRepositoryPort,
                                         CompanyRepositoryPort companyRepositoryPort) {
        this.userRepositoryPort = userRepositoryPort;
        this.studentRepositoryPort = studentRepositoryPort;
        this.postulationRepositoryPort = postulationRepositoryPort;
        this.offerRepositoryPort = offerRepositoryPort;
        this.companyRepositoryPort = companyRepositoryPort;
    }

    @Override
    @Transactional(readOnly = true)
    public Student getMyStudent(String email) {
        User user = userRepositoryPort.findByCorreoElectronico(email)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró la cuenta de la sesión."));
        return studentRepositoryPort.findByIdUser(user.getIdUser())
                .orElseThrow(() -> new ResourceNotFoundException("La cuenta no tiene un registro de estudiante asociado."));
    }

    @Override
    @Transactional(readOnly = true)
    public List<StudentPostulationSummary> getMyPostulations(String email) {
        Student student = getMyStudent(email);
        Map<Long, Offer> ofertas = new HashMap<>();
        Map<String, String> empresas = new HashMap<>();

        return postulationRepositoryPort.findByStudentDocument(student.getDocument()).stream().map(p -> {
            Offer oferta = ofertas.computeIfAbsent(p.getIdOffer(), offerRepositoryPort::findById);
            String empresa = oferta == null ? null : empresas.computeIfAbsent(oferta.getNitCompanyAssociated(),
                    nit -> companyRepositoryPort.findByNit(nit).map(Company::getCompanyName).orElse(null));
            return new StudentPostulationSummary(
                    p.getIdPostulation(),
                    p.getIdOffer(),
                    oferta != null ? oferta.getPracticeName() : "Oferta #" + p.getIdOffer(),
                    oferta != null ? oferta.getProfession() : null,
                    oferta != null ? oferta.getModality() : null,
                    oferta != null ? oferta.getLocation() : null,
                    empresa,
                    p.getStatusPostulation(),
                    p.getDatePostulation(),
                    p.getDateUdpate(),
                    p.getObservations()
            );
        }).toList();
    }
}
