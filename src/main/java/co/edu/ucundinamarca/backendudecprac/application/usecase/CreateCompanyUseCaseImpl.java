package co.edu.ucundinamarca.backendudecprac.application.usecase;

import co.edu.ucundinamarca.backendudecprac.domain.ResourceAlreadyExistsException;
import co.edu.ucundinamarca.backendudecprac.domain.model.Company;
import co.edu.ucundinamarca.backendudecprac.domain.model.User;
import co.edu.ucundinamarca.backendudecprac.domain.port.in.createCompanyUseCase;
import co.edu.ucundinamarca.backendudecprac.domain.port.out.CompanyRepositoryPort;
import co.edu.ucundinamarca.backendudecprac.domain.port.out.UserRepositoryPort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CreateCompanyUseCaseImpl implements createCompanyUseCase {

    private final CompanyRepositoryPort empresarepositoryPort;
    private final UserRepositoryPort usuariorepositoryport;
    private final PasswordEncoder passwordEncoder;
    public CreateCompanyUseCaseImpl(CompanyRepositoryPort empresarepositoryPort, UserRepositoryPort usuariorepositoryport, PasswordEncoder passwordEncoder) {
        this.empresarepositoryPort = empresarepositoryPort;
        this.usuariorepositoryport = usuariorepositoryport;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Company createCompany(User user, Company company) {
        if(usuariorepositoryport.existsByCorreoElectronico(user.getEmailAddres())) {
            throw new ResourceAlreadyExistsException("Ya existe una cuenta con ese correo");
        }
        if(empresarepositoryPort.existsByNit(company.getNit())){
            throw new ResourceAlreadyExistsException("Ya existe una cuenta con ese nit");
        }
        String JWtpassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(JWtpassword);
        user.setUserStatus(false);
        user.setCreationDate(LocalDateTime.now());
        User userGuardado = usuariorepositoryport.saveUser(user);
        company.setIdUser(userGuardado.getIdUser());
        return empresarepositoryPort.saveEmpresa(company);
    }

    @Override
    public List<Company> findAllCompanies() {
        return empresarepositoryPort.findAllCompanies();
    }


}
