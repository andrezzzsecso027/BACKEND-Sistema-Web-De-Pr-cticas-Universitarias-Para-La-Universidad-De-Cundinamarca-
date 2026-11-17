package co.edu.ucundinamarca.backendudecprac.application.usecase;

import co.edu.ucundinamarca.backendudecprac.domain.exceptions.ResourceNotFoundException;
import co.edu.ucundinamarca.backendudecprac.domain.model.Company;
import co.edu.ucundinamarca.backendudecprac.domain.model.status.CompanyVerificationStatus;
import co.edu.ucundinamarca.backendudecprac.domain.model.LegalRepresentative;
import co.edu.ucundinamarca.backendudecprac.domain.model.StudentProfile;
import co.edu.ucundinamarca.backendudecprac.domain.port.out.CompanyRepositoryPort;
import co.edu.ucundinamarca.backendudecprac.domain.port.out.StudentProfileRepositoryPort;
import co.edu.ucundinamarca.backendudecprac.domain.port.out.StudentRepositoryPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Cubre la lógica de "actualizar solo los campos enviados", que antes vivía en los
 * controladores y ahora está en los casos de uso.
 */
@ExtendWith(MockitoExtension.class)
class ProfileUpdateUseCasesTest {

    @Mock private StudentProfileRepositoryPort profileRepositoryPort;
    @Mock private StudentRepositoryPort studentRepositoryPort;
    @Mock private CompanyRepositoryPort companyRepositoryPort;

    @InjectMocks private StudentProfileUseCaseImpl studentProfileUseCase;
    @InjectMocks private UpdateCompanyProfileUseCaseImpl updateCompanyProfileUseCase;

    @Test
    @DisplayName("Perfil existente: solo se actualizan los campos enviados, el resto se conserva")
    void actualizaPerfilExistenteSinBorrarCampos() {
        StudentProfile existente = new StudentProfile();
        existente.setIdProfile(7L);
        existente.setDocumentStudent("1001");
        existente.setSkills("Java");
        existente.setGithubUrl("https://github.com/viejo");

        StudentProfile cambios = new StudentProfile();
        cambios.setDocumentStudent("1001");
        cambios.setSkills("Java, Angular"); // solo cambia skills

        when(studentRepositoryPort.existsByDocumento("1001")).thenReturn(true);
        when(profileRepositoryPort.findByDocumentStudent("1001")).thenReturn(existente);
        when(profileRepositoryPort.save(any())).thenAnswer(i -> i.getArgument(0));

        StudentProfile resultado = studentProfileUseCase.saveProfile(cambios);

        assertEquals(7L, resultado.getIdProfile());
        assertEquals("Java, Angular", resultado.getSkills());
        assertEquals("https://github.com/viejo", resultado.getGithubUrl(), "no debe borrar campos no enviados");
    }

    @Test
    @DisplayName("Perfil nuevo: se guarda tal cual")
    void creaPerfilNuevo() {
        StudentProfile nuevo = new StudentProfile();
        nuevo.setDocumentStudent("2002");
        nuevo.setSkills("Python");

        when(studentRepositoryPort.existsByDocumento("2002")).thenReturn(true);
        when(profileRepositoryPort.findByDocumentStudent("2002")).thenReturn(null);
        when(profileRepositoryPort.save(any())).thenAnswer(i -> i.getArgument(0));

        assertSame(nuevo, studentProfileUseCase.saveProfile(nuevo));
    }

    @Test
    @DisplayName("Empresa: actualiza campos enviados y representante legal, sin tocar NIT ni estado de verificación")
    void actualizaPerfilEmpresa() {
        Company existente = new Company();
        existente.setNit("900-1");
        existente.setVerificationStatus(CompanyVerificationStatus.APROBADA);
        existente.setCompanyName("Vieja SAS");
        existente.setCity("Fusagasugá");
        existente.setLegalRepresentative(new LegalRepresentative("Ana", "Pérez", "123"));

        Company cambios = new Company();
        cambios.setVerificationStatus(null);
        cambios.setCompanyName("Nueva SAS");
        cambios.setLegalRepresentative(new LegalRepresentative("Luis", null, null));

        when(companyRepositoryPort.findByNit("900-1")).thenReturn(Optional.of(existente));
        when(companyRepositoryPort.saveCompany(any())).thenAnswer(i -> i.getArgument(0));

        Company resultado = updateCompanyProfileUseCase.updateProfile("900-1", cambios);

        assertEquals("Nueva SAS", resultado.getCompanyName());
        assertEquals("Fusagasugá", resultado.getCity());
        assertEquals("900-1", resultado.getNit());
        assertEquals(CompanyVerificationStatus.APROBADA, resultado.getVerificationStatus());
        assertEquals("Luis", resultado.getLegalRepresentative().getNameRepresentative());
        assertEquals("Pérez", resultado.getLegalRepresentative().getLastNameRepresentative());
    }

    @Test
    @DisplayName("Empresa inexistente: lanza ResourceNotFoundException (el controlador responde 404)")
    void empresaInexistente() {
        when(companyRepositoryPort.findByNit("000")).thenReturn(Optional.empty());
        Company cambios = new Company();
        assertThrows(ResourceNotFoundException.class, () -> updateCompanyProfileUseCase.updateProfile("000", cambios));
        verify(companyRepositoryPort, never()).saveCompany(any());
    }
}
