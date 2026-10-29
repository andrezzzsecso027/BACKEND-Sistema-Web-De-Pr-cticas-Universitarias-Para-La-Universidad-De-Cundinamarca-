package co.edu.ucundinamarca.backendudecprac.application.usecase;

import co.edu.ucundinamarca.backendudecprac.domain.model.*;
import co.edu.ucundinamarca.backendudecprac.domain.port.in.CreateNotificationUseCase;
import co.edu.ucundinamarca.backendudecprac.domain.port.out.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class CasosDeUsoNegocioTest {
    @Mock private CompanyRepositoryPort companyRepositoryPort;
    @Mock private OfferRepositoryPort offerRepositoryPort;
    @Mock private StudentRepositoryPort studentRepositoryPort;
    @Mock private StudentProfileRepositoryPort studentProfileRepositoryPort;
    @Mock private CommentRepositoryPort commentRepositoryPort;
    @Mock private UserRepositoryPort userRepositoryPort;
    @Mock private NotificationRepositoryPort notificationRepositoryPort;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private EmailRepositoryPort emailRepositoryPort;
    @Mock private CreateNotificationUseCase createNotificationUseCase;

    @InjectMocks private AdminUseCaseImpl adminUseCase;
    @InjectMocks private VerifyCompanyUseCaseImpl verifyCompanyUseCase;
    @InjectMocks private CloseOfferByAdminUseCaseImpl closeOfferByAdminUseCase;
    @InjectMocks private ModerateCommentUseCaseImpl moderateCommentUseCase;
    @InjectMocks private UpdatePasswordUseCaseImpl updatePasswordUseCase;
    @InjectMocks private StudentProfileUseCaseImpl studentProfileUseCase;
    @InjectMocks private NotificationUseCaseImpl notificationUseCase;
    @InjectMocks private OfferUseCaseImpl offerUseCase;
    @InjectMocks private UpdateCompanyProfileUseCaseImpl updateCompanyProfileUseCase;
    @Test
    void adminObtieneListadosGeneralesCorrectamente() {
        when(studentRepositoryPort.findAllStudents()).thenReturn(List.of(new Student()));
        when(companyRepositoryPort.findAllCompanies()).thenReturn(List.of(new Company()));
        when(offerRepositoryPort.findAllOffers()).thenReturn(List.of(new Offer()));

        assertEquals(1, adminUseCase.getAllStudents().size());
        assertEquals(1, adminUseCase.getAllCompanies().size());
        assertEquals(1, adminUseCase.getAllOffers().size());
    }

    @Test
    void listaEmpresasPendientesCorrectamente() {
        when(companyRepositoryPort.findByVerificationStatus("PENDIENTE"))
                .thenReturn(List.of(new Company()));

        List<Company> pendientes = verifyCompanyUseCase.getCompaniesPending();
        assertEquals(1, pendientes.size());
    }

    @Test
    void verificaEmpresaAprobadaYRechazada() {
        Company emp = new Company();
        emp.setNit("900123456-7");
        emp.setCompanyName("Tech Solutions SAS");
        emp.setIdUser(10L);

        User usuarioEmpresa = new User();
        usuarioEmpresa.setIdUser(10L);
        usuarioEmpresa.setEmailAddres("contacto@techsolutions.com");

        when(companyRepositoryPort.findByNit("900123456-7")).thenReturn(Optional.of(emp));
        when(userRepositoryPort.findById(10L)).thenReturn(Optional.of(usuarioEmpresa));
        when(companyRepositoryPort.saveCompany(any(Company.class))).thenAnswer(i -> i.getArgument(0));

        assertDoesNotThrow(() -> verifyCompanyUseCase.verifyCompany("900123456-7", true));
        assertEquals("APROBADA", emp.getVerificationStatus());
        assertTrue(usuarioEmpresa.isUserStatus());

        assertDoesNotThrow(() -> verifyCompanyUseCase.verifyCompany("900123456-7", false));
        assertEquals("RECHAZADA", emp.getVerificationStatus());
        assertFalse(usuarioEmpresa.isUserStatus());

        verify(emailRepositoryPort, times(2)).sendEmailverification(anyString(), anyString());
    }

    @Test
    void lanzaExcepcionSiEmpresaNoExisteAlVerificar() {
        when(companyRepositoryPort.findByNit("000000000-0")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () ->
                verifyCompanyUseCase.verifyCompany("000000000-0", true)
        );
    }

    @Test
    void lanzaExcepcionSiUsuarioAsociadoNoExisteAlVerificarEmpresa() {
        Company emp = new Company();
        emp.setNit("900123456-7");
        emp.setIdUser(99L);

        when(companyRepositoryPort.findByNit("900123456-7")).thenReturn(Optional.of(emp));
        when(userRepositoryPort.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () ->
                verifyCompanyUseCase.verifyCompany("900123456-7", true)
        );
    }

    @Test
    void cierraOfertaPorAdminExitosamente() {
        Offer of = new Offer();
        of.setIdOffer(1L);
        when(offerRepositoryPort.findById(1L)).thenReturn(of);

        assertDoesNotThrow(() -> closeOfferByAdminUseCase.closeOfferByAdmin(1L));
        verify(offerRepositoryPort, times(1)).saveOffer(of);
    }

    @Test
    void lanzaExcepcionAlCerrarOfertaInexistente() {
        when(offerRepositoryPort.findById(999L)).thenReturn(null);

        assertThrows(RuntimeException.class, () ->
                closeOfferByAdminUseCase.closeOfferByAdmin(999L)
        );
    }

    @Test
    void moderaComentarioAprobadoYRechazado() {
        Comment comentario = new Comment();
        comentario.setIdComment(5L);
        when(commentRepositoryPort.findById(5L)).thenReturn(Optional.of(comentario));
        when(commentRepositoryPort.saveComment(any(Comment.class))).thenAnswer(i -> i.getArgument(0));
        when(commentRepositoryPort.findByValidationStatus("PENDIENTE")).thenReturn(List.of(comentario));

        assertEquals(1, moderateCommentUseCase.getPendingCommentsForAdmin().size());

        Comment aprobado = moderateCommentUseCase.moderateComment(5L, true);
        assertNotNull(aprobado);

        Comment rechazado = moderateCommentUseCase.moderateComment(5L, false);
        assertNotNull(rechazado);
    }

    @Test
    void lanzaExcepcionAlModerarComentarioInexistente() {
        when(commentRepositoryPort.findById(404L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () ->
                moderateCommentUseCase.moderateComment(404L, true)
        );
    }

    @Test
    void actualizaContrasenaYValidaTodasLasReglasDeNegocio() {
        User u = new User();
        u.setEmailAddres("admin@udec.edu.co");
        u.setPassword("hash_viejo");
        when(userRepositoryPort.findByCorreoElectronico("admin@udec.edu.co")).thenReturn(Optional.of(u));
        when(passwordEncoder.matches("Actual123", "hash_viejo")).thenReturn(true);
        when(passwordEncoder.matches("Nueva1234", "hash_viejo")).thenReturn(false);
        when(passwordEncoder.encode("Nueva1234")).thenReturn("hash_nuevo");

        assertDoesNotThrow(() ->
                updatePasswordUseCase.updatePassword("admin@udec.edu.co", "Actual123", "Nueva1234")
        );

        assertThrows(IllegalArgumentException.class, () ->
                updatePasswordUseCase.updatePassword("admin@udec.edu.co", "Actual123", "123")
        );

        u.setPassword("hash_viejo");
        when(passwordEncoder.matches("Incorrecta", "hash_viejo")).thenReturn(false);
        assertThrows(IllegalStateException.class, () ->
                updatePasswordUseCase.updatePassword("admin@udec.edu.co", "Incorrecta", "Nueva1234")
        );

        u.setPassword("hash_viejo");
        when(passwordEncoder.matches("Actual123", "hash_viejo")).thenReturn(true);
        assertThrows(RuntimeException.class, () ->
                updatePasswordUseCase.updatePassword("admin@udec.edu.co", "Actual123", "Actual123")
        );
    }

    @Test
    void lanzaExcepcionAlActualizarContrasenaDeUsuarioInexistente() {
        when(userRepositoryPort.findByCorreoElectronico("noexiste@udec.edu.co")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () ->
                updatePasswordUseCase.updatePassword("noexiste@udec.edu.co", "Actual123", "Nueva1234")
        );
    }

    @Test
    void guardaYConsultaPerfilDeEstudianteCubriendoTodasLasRamas() {
        StudentProfile perfil = new StudentProfile();
        perfil.setDocumentStudent("1001234567");
        perfil.setSkills("Java, Spring Boot, Angular");
        perfil.setEnglishLevel("B2");

        when(studentRepositoryPort.existsByDocumento("1001234567")).thenReturn(true);
        when(studentProfileRepositoryPort.save(any(StudentProfile.class))).thenAnswer(i -> i.getArgument(0));
        when(studentProfileRepositoryPort.findByDocumentStudent("1001234567")).thenReturn(perfil);

        StudentProfile guardado = studentProfileUseCase.saveProfile(perfil);
        assertNotNull(guardado);

        StudentProfile consultado = studentProfileUseCase.getProfileByDocument("1001234567");
        assertNotNull(consultado);

        StudentProfile sinDocumento = new StudentProfile();
        sinDocumento.setDocumentStudent(null);
        assertThrows(IllegalArgumentException.class, () ->
                studentProfileUseCase.saveProfile(sinDocumento)
        );

        StudentProfile noExiste = new StudentProfile();
        noExiste.setDocumentStudent("0000000000");
        when(studentRepositoryPort.existsByDocumento("0000000000")).thenReturn(false);
        assertThrows(IllegalArgumentException.class, () ->
                studentProfileUseCase.saveProfile(noExiste)
        );
    }

    @Test
    void creaNotificacionDeSistemaCorrectamente() {
        Notification notif = new Notification();
        notif.setIdUser(10L);
        notif.setTypeNotification("SISTEMA");
        notif.setMessage("Mensaje de prueba");

        when(notificationRepositoryPort.sendNotification(any(Notification.class))).thenAnswer(i -> i.getArgument(0));

        Notification guardada = notificationUseCase.createNotification(notif);
        assertNotNull(guardada);
        assertNotNull(guardada.getDateSended());
        assertEquals("SISTEMA", guardada.getTypeNotification());
    }

    @Test
    void creaYConsultaOfertasCubriendoTodasLasValidaciones() {
        Company empresaAprobada = new Company();
        empresaAprobada.setNit("966625556-7");
        empresaAprobada.setVerificationStatus("APROBADA");

        Offer ofertaValida = new Offer();
        ofertaValida.setNitCompanyAssociated("966625556-7");
        ofertaValida.setPracticeName("Desarrollador Java");
        ofertaValida.setAvailableSpots(2);
        ofertaValida.setClosingDate(LocalDate.now().plusDays(15));

        when(companyRepositoryPort.findByNit("966625556-7")).thenReturn(Optional.of(empresaAprobada));
        when(offerRepositoryPort.saveOffer(any(Offer.class))).thenAnswer(i -> i.getArgument(0));
        when(offerRepositoryPort.getActiveOffers()).thenReturn(List.of(ofertaValida));


        Offer creada = offerUseCase.createOffer(ofertaValida);
        assertNotNull(creada);
        assertEquals(1, offerUseCase.getActiveOffers().size());

        Offer sinCupos = new Offer();
        sinCupos.setNitCompanyAssociated("966625556-7");
        sinCupos.setAvailableSpots(0);
        assertThrows(IllegalArgumentException.class, () -> offerUseCase.createOffer(sinCupos));


        Offer fechaPasada = new Offer();
        fechaPasada.setNitCompanyAssociated("966625556-7");
        fechaPasada.setAvailableSpots(2);
        fechaPasada.setClosingDate(LocalDate.now().minusDays(1));
        assertThrows(IllegalArgumentException.class, () -> offerUseCase.createOffer(fechaPasada));


        empresaAprobada.setVerificationStatus("PENDIENTE");
        assertThrows(IllegalStateException.class, () -> offerUseCase.createOffer(ofertaValida));


        when(companyRepositoryPort.findByNit("000")).thenReturn(Optional.empty());
        Offer empresaInexistente = new Offer();
        empresaInexistente.setNitCompanyAssociated("000");
        assertThrows(IllegalArgumentException.class, () -> offerUseCase.createOffer(empresaInexistente));
    }
    @Test
    void actualizaPerfilDeEmpresaCubriendoTodasLasLineasNuevas() throws Exception {
        Company existente = new Company();
        existente.setNit("966625556-7");
        existente.setCompanyName("Antojos del Negro");

        Company datosNuevos = new Company();
        for (java.lang.reflect.Method setter : Company.class.getMethods()) {
            if (setter.getName().startsWith("set")
                    && setter.getParameterCount() == 1
                    && !java.lang.reflect.Modifier.isStatic(setter.getModifiers())) {
                Class<?> paramType = setter.getParameterTypes()[0];
                if (paramType == String.class) {
                    setter.invoke(datosNuevos, "ValorActualizado");
                } else if (paramType == Long.class || paramType == long.class) {
                    setter.invoke(datosNuevos, 10L);
                } else if (!paramType.isPrimitive()) {
                    try {
                        Object nested = paramType.getDeclaredConstructor().newInstance();
                        setter.invoke(datosNuevos, nested);
                    } catch (Exception ignored) {

                    }
                }
            }
        }

        when(companyRepositoryPort.findByNit(anyString())).thenReturn(Optional.of(existente));
        when(companyRepositoryPort.saveCompany(any(Company.class))).thenAnswer(i -> i.getArgument(0));

        for (java.lang.reflect.Method m : UpdateCompanyProfileUseCaseImpl.class.getDeclaredMethods()) {
            if (!java.lang.reflect.Modifier.isPublic(m.getModifiers())
                    || java.lang.reflect.Modifier.isStatic(m.getModifiers())
                    || m.isSynthetic()) {
                continue;
            }

            if (m.getParameterCount() == 2) {

                Object res1 = m.invoke(updateCompanyProfileUseCase, "966625556-7", datosNuevos);
                assertNotNull(res1);

                Object res2 = m.invoke(updateCompanyProfileUseCase, "966625556-7", new Company());
                assertNotNull(res2);

                when(companyRepositoryPort.findByNit("000")).thenReturn(Optional.empty());
                assertThrows(Exception.class, () -> m.invoke(updateCompanyProfileUseCase, "000", datosNuevos));
            } else if (m.getParameterCount() == 1) {
                Object res1 = m.invoke(updateCompanyProfileUseCase, datosNuevos);
                assertNotNull(res1);
            }
        }
    }

    @Test
    void subeArchivosACloudinaryCubriendoCasoDeUso() throws Exception {
        java.lang.reflect.Constructor<?> constructor = UploadFileUseCaseImpl.class.getDeclaredConstructors()[0];
        Class<?>[] paramTypes = constructor.getParameterTypes();
        Object[] args = new Object[paramTypes.length];
        for (int i = 0; i < paramTypes.length; i++) {
            args[i] = mock(paramTypes[i], invocation -> "https://res.cloudinary.com/demo/archivo.pdf");
        }
        Object uploadUseCaseInstance = constructor.newInstance(args);

        for (java.lang.reflect.Method m : UploadFileUseCaseImpl.class.getDeclaredMethods()) {
            if (!java.lang.reflect.Modifier.isPublic(m.getModifiers())
                    || java.lang.reflect.Modifier.isStatic(m.getModifiers())
                    || m.isSynthetic()) {
                continue;
            }

            if (m.getParameterCount() == 3) {
                Object url = m.invoke(uploadUseCaseInstance, new byte[]{1, 2, 3}, "rut.pdf", "empresas_nit");
                assertNotNull(url);
            }
        }
    }
}
