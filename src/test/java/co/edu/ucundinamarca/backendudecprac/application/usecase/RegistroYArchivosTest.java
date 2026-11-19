package co.edu.ucundinamarca.backendudecprac.application.usecase;

import co.edu.ucundinamarca.backendudecprac.domain.exceptions.InvalidInputException;
import co.edu.ucundinamarca.backendudecprac.domain.exceptions.ResourceAlreadyExistsException;
import co.edu.ucundinamarca.backendudecprac.domain.model.Company;
import co.edu.ucundinamarca.backendudecprac.domain.model.Student;
import co.edu.ucundinamarca.backendudecprac.domain.model.User;
import co.edu.ucundinamarca.backendudecprac.domain.port.out.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Registro de empresas (certificado NIT obligatorio), correo institucional de estudiantes
 * y validación de archivos subidos.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class RegistroYArchivosTest {

    @Mock private CompanyRepositoryPort companyRepositoryPort;
    @Mock private UserRepositoryPort userRepositoryPort;
    @Mock private StudentRepositoryPort studentRepositoryPort;
    @Mock private PasswordEncoderPort passwordEncoder;
    @Mock private EmailSenderPort emailSenderPort;
    @Mock private FileStoragePort fileStoragePort;

    @InjectMocks private CreateCompanyUseCaseImpl createCompany;
    @InjectMocks private StudentUseCaseUseCaseImpl createStudent;
    @InjectMocks private UploadFileUseCaseImpl uploadFile;

    @Nested
    @DisplayName("Registro de empresa")
    class RegistroEmpresa {

        private Company empresa(String rutPdfUrl) {
            Company c = new Company();
            c.setNit("900452318-4");
            c.setRutPdfUrl(rutPdfUrl);
            return c;
        }

        @Test
        @DisplayName("Sin certificado RUT/NIT en PDF se rechaza y no se guarda nada")
        void sinCertificadoSeRechaza() {
            User usuario = new User();
            Company sinPdf = empresa(null);
            Company pdfVacio = empresa("  ");
            Company conImagen = empresa("https://res.cloudinary.com/demo/logo.png");
            assertThrows(InvalidInputException.class, () -> createCompany.createCompany(usuario, sinPdf));
            assertThrows(InvalidInputException.class, () -> createCompany.createCompany(usuario, pdfVacio));
            assertThrows(InvalidInputException.class, () -> createCompany.createCompany(usuario, conImagen));
            verify(userRepositoryPort, never()).saveUser(any());
            verify(companyRepositoryPort, never()).createCompany(any());
        }

        @Test
        @DisplayName("Con certificado se registra pendiente de aprobación y guarda la URL del PDF")
        void conCertificadoSeRegistra() {
            User user = new User();
            user.setEmailAddres("talento@empresa.co");
            user.setPassword("secreta1");
            when(passwordEncoder.encode(anyString())).thenReturn("hash");
            when(userRepositoryPort.saveUser(any())).thenAnswer(i -> { User u = i.getArgument(0); u.setIdUser(9); return u; });
            when(companyRepositoryPort.createCompany(any())).thenAnswer(i -> i.getArgument(0));

            Company creada = createCompany.createCompany(user, empresa("https://res.cloudinary.com/demo/udecprac/empresas_nit/rut_ab12.pdf"));

            assertEquals("https://res.cloudinary.com/demo/udecprac/empresas_nit/rut_ab12.pdf", creada.getRutPdfUrl());
            assertEquals(9L, creada.getIdUser());
            assertFalse(user.isUserStatus(), "la cuenta queda deshabilitada hasta que la Universidad la apruebe");
            verify(emailSenderPort).sendEmail(eq("talento@empresa.co"), contains("Recibimos tu solicitud"));
        }

        @Test
        @DisplayName("Si el correo de solicitud recibida falla, el registro igual queda hecho")
        void correoFallidoNoImpideElRegistro() {
            User user = new User();
            user.setEmailAddres("sin-correo@empresa.co");
            user.setPassword("secreta1");
            when(passwordEncoder.encode(anyString())).thenReturn("hash");
            when(userRepositoryPort.saveUser(any())).thenAnswer(i -> { User u = i.getArgument(0); u.setIdUser(10); return u; });
            when(companyRepositoryPort.createCompany(any())).thenAnswer(i -> i.getArgument(0));
            doThrow(new RuntimeException("Brevo caído")).when(emailSenderPort).sendEmail(anyString(), anyString());

            Company creada = assertDoesNotThrow(() ->
                    createCompany.createCompany(user, empresa("https://res.cloudinary.com/demo/udecprac/empresas_nit/rut_cd34.pdf")));
            assertEquals(10L, creada.getIdUser());
        }

        @Test
        @DisplayName("Si un registro simultáneo ganó la carrera por el NIT, se responde conflicto (no se sobrescribe)")
        void nitDuplicadoConcurrenteEsConflicto() {
            User user = new User();
            user.setEmailAddres("otra@empresa.co");
            user.setPassword("secreta1");
            when(passwordEncoder.encode(anyString())).thenReturn("hash");
            when(userRepositoryPort.saveUser(any())).thenAnswer(i -> i.getArgument(0));
            // existsByNit dijo "no existe", pero al insertar la BD ya lo tiene (otro hilo hizo commit antes)
            when(companyRepositoryPort.createCompany(any()))
                    .thenThrow(new ResourceAlreadyExistsException("Ya existe una cuenta con ese nit"));

            Company empresa = empresa("https://res.cloudinary.com/demo/udecprac/empresas_nit/rut_ab12.pdf");
            assertThrows(ResourceAlreadyExistsException.class, () -> createCompany.createCompany(user, empresa));
            verify(companyRepositoryPort, never()).saveCompany(any());
        }
    }

    @Nested
    @DisplayName("Correo institucional del estudiante")
    class CorreoEstudiante {

        @ParameterizedTest
        @ValueSource(strings = {"ana@gmail.com", "@ucundinamarca.edu.co", "ana@ucundinamarca.edu.co.co",
                "ana@mail.ucundinamarca.edu.co", "ana perez@ucundinamarca.edu.co", ""})
        @DisplayName("Rechaza correos que no son del dominio institucional")
        void rechazaOtrosDominios(String correo) {
            User user = new User();
            user.setEmailAddres(correo);
            Student estudiante = new Student();
            assertThrows(InvalidInputException.class, () -> createStudent.createStudent(user, estudiante));
            verify(userRepositoryPort, never()).saveUser(any());
        }

        @Test
        @DisplayName("Acepta el dominio institucional sin importar mayúsculas y lo guarda normalizado")
        void normalizaCorreo() {
            User user = new User();
            user.setEmailAddres("  Ana.Perez@UCundinamarca.edu.co ");
            user.setPassword("secreta1");
            Student student = new Student();
            student.setDocument("1069748521");
            when(passwordEncoder.encode(anyString())).thenReturn("hash");
            when(userRepositoryPort.saveUser(any())).thenAnswer(i -> i.getArgument(0));
            when(studentRepositoryPort.saveStudent(any())).thenAnswer(i -> i.getArgument(0));

            createStudent.createStudent(user, student);

            ArgumentCaptor<String> destino = ArgumentCaptor.forClass(String.class);
            verify(emailSenderPort).sendEmail(destino.capture(), anyString());
            assertEquals("ana.perez@ucundinamarca.edu.co", destino.getValue());
            assertEquals("ana.perez@ucundinamarca.edu.co", user.getEmailAddres());
        }
    }

    @Nested
    @DisplayName("Subida de archivos")
    class SubidaArchivos {

        private final byte[] pdf = "%PDF-1.7\n...".getBytes(StandardCharsets.US_ASCII);
        private final byte[] png = {(byte) 0x89, 'P', 'N', 'G', 0x0D, 0x0A};

        @Test
        @DisplayName("Sube un PDF real del NIT y conserva la extensión .pdf")
        void subePdf() {
            when(fileStoragePort.uploadFile(any(), anyString(), anyString())).thenReturn("https://res.cloudinary.com/x/rut.pdf");
            assertNotNull(uploadFile.uploadFile(pdf, "RUT empresa.pdf", "empresas_nit"));
            ArgumentCaptor<String> publicId = ArgumentCaptor.forClass(String.class);
            verify(fileStoragePort).uploadFile(any(), publicId.capture(), eq("empresas_nit"));
            assertTrue(publicId.getValue().startsWith("RUT_empresa_") && publicId.getValue().endsWith(".pdf"));
        }

        @Test
        @DisplayName("Rechaza un archivo que dice ser PDF pero no lo es")
        void rechazaPdfFalso() {
            assertThrows(InvalidInputException.class, () -> uploadFile.uploadFile(png, "rut.pdf", "empresas_nit"));
            assertThrows(InvalidInputException.class, () -> uploadFile.uploadFile(pdf, "rut.docx", "empresas_nit"));
            verifyNoInteractions(fileStoragePort);
        }

        @Test
        @DisplayName("Las carpetas de imagen solo aceptan imágenes y no se aceptan carpetas desconocidas")
        void validaCarpetaYTipo() {
            when(fileStoragePort.uploadFile(any(), anyString(), anyString())).thenReturn("https://res.cloudinary.com/x/logo");
            assertNotNull(uploadFile.uploadFile(png, "logo.png", "empresas_logos"));
            assertThrows(InvalidInputException.class, () -> uploadFile.uploadFile(pdf, "logo.pdf", "empresas_logos"));
            assertThrows(InvalidInputException.class, () -> uploadFile.uploadFile(pdf, "rut.pdf", "general"));
            assertThrows(InvalidInputException.class, () -> uploadFile.uploadFile(pdf, "rut.pdf", "../otra"));
        }

        @Test
        @DisplayName("Rechaza archivos de más de 10 MB")
        void rechazaArchivoGrande() {
            byte[] grande = new byte[(int) UploadFileUseCaseImpl.TAMANO_MAXIMO_BYTES + 1];
            System.arraycopy(pdf, 0, grande, 0, pdf.length);
            assertThrows(InvalidInputException.class, () -> uploadFile.uploadFile(grande, "rut.pdf", "empresas_nit"));
        }
    }
}
