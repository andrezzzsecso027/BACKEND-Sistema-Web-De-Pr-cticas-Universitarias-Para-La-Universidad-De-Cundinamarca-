package co.edu.ucundinamarca.backendudecprac.application.usecase;

import co.edu.ucundinamarca.backendudecprac.domain.exceptions.BusinessRuleException;
import co.edu.ucundinamarca.backendudecprac.domain.exceptions.InvalidInputException;
import co.edu.ucundinamarca.backendudecprac.domain.exceptions.ResourceNotFoundException;
import co.edu.ucundinamarca.backendudecprac.domain.model.*;
import co.edu.ucundinamarca.backendudecprac.domain.model.status.CommentStatus;
import co.edu.ucundinamarca.backendudecprac.domain.model.status.ContractTrackingStatus;
import co.edu.ucundinamarca.backendudecprac.domain.model.status.PostulationStatus;
import co.edu.ucundinamarca.backendudecprac.domain.model.status.PracticeStatus;
import co.edu.ucundinamarca.backendudecprac.domain.port.in.CreateNotificationUseCase;
import co.edu.ucundinamarca.backendudecprac.domain.port.out.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/** Flujos de negocio de postulación, contrato, comentarios y verificación de estudiantes. */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class FlujosDeNegocioTest {

    @Mock private PostulationRepositoryPort postulationPort;
    @Mock private PostulationHistoryRepositoryPort historyPort;
    @Mock private PracticeRepositoryPort practicePort;
    @Mock private OfferRepositoryPort offerPort;
    @Mock private StudentRepositoryPort studentPort;
    @Mock private UserRepositoryPort userPort;
    @Mock private EmailSenderPort emailPort;
    @Mock private ContractDocumentRepositoryPort contractPort;
    @Mock private CommentRepositoryPort commentPort;
    @Mock private CreateNotificationUseCase notificationUseCase;
    @Mock private PasswordEncoderPort passwordEncoder;

    @InjectMocks private UpdatePostulationStatusUseCaseImpl updateStatus;
    @InjectMocks private ManageContractDocumentUseCaseImpl contracts;
    @InjectMocks private CommentUseCaseImpl comments;
    @InjectMocks private PostulationUseCaseImpl postulations;
    @InjectMocks private VerifyStudentUseCaseImpl verifyStudent;

    private static Postulation postulacionEnRevision() {
        Postulation p = new Postulation();
        p.setIdPostulation(1L);
        p.setIdOffer(10L);
        p.setDocumentNumberStudent("1003");
        p.submit(LocalDateTime.now());
        return p;
    }

    private static Offer ofertaConCupos(int cupos) {
        Offer o = new Offer();
        o.setIdOffer(10L);
        o.setPracticeName("Practicante QA");
        o.setAvailableSpots(cupos);
        o.setOfferStatus(true);
        return o;
    }

    @Nested
    @DisplayName("Cambio de estado de una postulación")
    class CambioEstado {

        @BeforeEach
        void guardadoDevuelveLoMismo() {
            when(postulationPort.savePostulation(any())).thenAnswer(i -> i.getArgument(0));
        }

        @Test
        void postulacionInexistenteEs404() {
            when(postulationPort.findById(1L)).thenReturn(null);
            assertThrows(ResourceNotFoundException.class,
                    () -> updateStatus.updateStatus(1L, PostulationStatus.ACEPTADA, null));
        }

        @Test
        void aceptarReservaCupoIniciaPracticaRegistraHistorialYNotifica() {
            Offer oferta = ofertaConCupos(2);
            when(postulationPort.findById(1L)).thenReturn(postulacionEnRevision());
            when(offerPort.findById(10L)).thenReturn(oferta);
            Student estudiante = new Student();
            estudiante.setIdUser(50L);
            estudiante.setName("Ana");
            when(studentPort.findByDocumento("1003")).thenReturn(estudiante);
            User usuario = new User();
            usuario.setEmailAddres("ana@ucundinamarca.edu.co");
            when(userPort.findById(50L)).thenReturn(Optional.of(usuario));

            Postulation resultado = updateStatus.updateStatus(1L, PostulationStatus.ACEPTADA, "Bienvenida");

            assertEquals(PostulationStatus.ACEPTADA, resultado.getStatusPostulation());
            assertEquals(1, oferta.getAvailableSpots());
            verify(practicePort).savePractice(any(Practice.class));
            verify(historyPort).saveHistory(any(PostulationHistory.class));
            verify(emailPort).sendEmail(eq("ana@ucundinamarca.edu.co"), contains("Bienvenida"));
        }

        @Test
        void siElCorreoFallaLaAceptacionNoSeRevierte() {
            when(postulationPort.findById(1L)).thenReturn(postulacionEnRevision());
            when(offerPort.findById(10L)).thenReturn(ofertaConCupos(1));
            Student estudiante = new Student();
            estudiante.setIdUser(50L);
            when(studentPort.findByDocumento("1003")).thenReturn(estudiante);
            when(userPort.findById(50L)).thenReturn(Optional.of(new User()));
            doThrow(new IllegalStateException("Brevo caído")).when(emailPort).sendEmail(any(), any());

            Postulation resultado = updateStatus.updateStatus(1L, PostulationStatus.ACEPTADA, " ");

            assertTrue(resultado.isAccepted());
        }

        @Test
        void aceptarSinEstudianteRegistradoNoEnviaCorreo() {
            when(postulationPort.findById(1L)).thenReturn(postulacionEnRevision());
            when(offerPort.findById(10L)).thenReturn(ofertaConCupos(1));
            when(studentPort.findByDocumento("1003")).thenReturn(null);

            updateStatus.updateStatus(1L, PostulationStatus.ACEPTADA, null);

            verifyNoInteractions(emailPort);
        }

        @Test
        void aceptarConOfertaInexistenteEs404() {
            when(postulationPort.findById(1L)).thenReturn(postulacionEnRevision());
            when(offerPort.findById(10L)).thenReturn(null);
            assertThrows(ResourceNotFoundException.class,
                    () -> updateStatus.updateStatus(1L, PostulationStatus.ACEPTADA, null));
        }

        @Test
        void rechazarNoTocaCuposNiPracticaNiCorreo() {
            when(postulationPort.findById(1L)).thenReturn(postulacionEnRevision());

            Postulation resultado = updateStatus.updateStatus(1L, PostulationStatus.RECHAZADA, "Perfil no compatible");

            assertEquals(PostulationStatus.RECHAZADA, resultado.getStatusPostulation());
            verifyNoInteractions(offerPort, practicePort, emailPort);
            verify(historyPort).saveHistory(any());
        }
    }

    @Nested
    @DisplayName("Contrato de práctica")
    class Contratos {

        @BeforeEach
        void guardadoDevuelveLoMismo() {
            when(contractPort.saveContractDocument(any())).thenAnswer(i -> i.getArgument(0));
        }

        @Test
        void exigePostulacionYNit() {
            ContractDocument sinPostulacion = new ContractDocument();
            ContractDocument sinNit = new ContractDocument();
            sinNit.setIdPostulation(1L);
            sinNit.setNitCompany(" ");
            assertThrows(InvalidInputException.class, () -> contracts.saveOrUpdateContract(null));
            assertThrows(InvalidInputException.class, () -> contracts.saveOrUpdateContract(sinPostulacion));
            assertThrows(InvalidInputException.class, () -> contracts.saveOrUpdateContract(sinNit));
        }

        @Test
        void contratoNuevoConArchivoQuedaFirmadoYSinArchivoEnCurso() {
            ContractDocument conArchivo = new ContractDocument();
            conArchivo.setIdPostulation(1L);
            conArchivo.setNitCompany("900");
            conArchivo.setFileUrl("https://res.cloudinary.com/x/contrato.pdf");
            ContractDocument sinArchivo = new ContractDocument();
            sinArchivo.setIdPostulation(2L);
            sinArchivo.setNitCompany("900");
            when(contractPort.findByPostulationId(anyLong())).thenReturn(Optional.empty());

            assertEquals(ContractTrackingStatus.CONTRATO_FIRMADO, contracts.saveOrUpdateContract(conArchivo).getTrackingStatus());
            assertEquals(ContractTrackingStatus.EN_CURSO, contracts.saveOrUpdateContract(sinArchivo).getTrackingStatus());
            assertNotNull(sinArchivo.getUploadDate());
        }

        @Test
        void actualizarSoloCambiaLoInformado() {
            ContractDocument existente = new ContractDocument();
            existente.setIdPostulation(1L);
            existente.setNitCompany("900");
            existente.setFileName("original.pdf");
            existente.setFileUrl("https://res.cloudinary.com/x/original.pdf");
            when(contractPort.findByPostulationId(1L)).thenReturn(Optional.of(existente));

            ContractDocument cambios = new ContractDocument();
            cambios.setIdPostulation(1L);
            cambios.setNitCompany("900");
            cambios.setFileName(" ");
            cambios.setCompanyComments("Firmado por ambas partes");
            cambios.setTrackingStatus(ContractTrackingStatus.FINALIZADA);

            ContractDocument resultado = contracts.saveOrUpdateContract(cambios);

            assertEquals("original.pdf", resultado.getFileName());
            assertEquals("https://res.cloudinary.com/x/original.pdf", resultado.getFileUrl());
            assertEquals("Firmado por ambas partes", resultado.getCompanyComments());
            assertEquals(ContractTrackingStatus.FINALIZADA, resultado.getTrackingStatus());
        }

        @Test
        void consultasDelegadasAlRepositorio() {
            ContractDocument c = new ContractDocument();
            when(contractPort.findByNitCompany("900")).thenReturn(List.of(c));
            when(contractPort.findAllContracts()).thenReturn(List.of(c, c));
            when(contractPort.findByPostulationId(3L)).thenReturn(Optional.of(c));

            assertEquals(1, contracts.getContractsByCompanyNit("900").size());
            assertEquals(2, contracts.getAllContracts().size());
            assertTrue(contracts.getContractByPostulationId(3L).isPresent());
        }
    }

    @Nested
    @DisplayName("Calificación de la empresa (comentarios)")
    class Comentarios {

        private static final String DUENO = "dueno@ucundinamarca.edu.co";
        private static final String OTRO = "otro@ucundinamarca.edu.co";

        /** DUENO (doc 1003) hizo la postulación 1 de la que nace la práctica 7; OTRO tiene el doc 2002. */
        @BeforeEach
        void sesiones() {
            registrarEstudiante(DUENO, 50L, "1003");
            registrarEstudiante(OTRO, 51L, "2002");
            when(postulationPort.findById(1L)).thenReturn(postulacionEnRevision()); // documento 1003
        }

        private void registrarEstudiante(String correo, long idUser, String documento) {
            User u = new User();
            u.setIdUser(idUser);
            Student s = new Student();
            s.setDocument(documento);
            when(userPort.findByCorreoElectronico(correo)).thenReturn(Optional.of(u));
            when(studentPort.findByIdUser(idUser)).thenReturn(Optional.of(s));
        }

        private Comment comentario() {
            Comment c = new Comment();
            c.setIdPractice(7L);
            c.setCommentText("Excelente acompañamiento");
            c.setCompanyRating(5.0);
            return c;
        }

        private Practice practicaFinalizada() {
            Practice p = Practice.startFor(1L);
            p.update(null, null, PracticeStatus.FINALIZADA);
            return p;
        }

        @Test
        void practicaInexistenteEs404() {
            Comment c = comentario();
            when(practicePort.findById(7L)).thenReturn(null);
            assertThrows(ResourceNotFoundException.class, () -> comments.createComment(c, DUENO));
        }

        @Test
        @DisplayName("Un estudiante no puede calificar la práctica de otro (se responde 404, no se revela)")
        void noSeCalificaLaPracticaDeOtroEstudiante() {
            Comment c = comentario();
            when(practicePort.findById(7L)).thenReturn(practicaFinalizada());
            assertThrows(ResourceNotFoundException.class, () -> comments.createComment(c, OTRO));
            assertThrows(ResourceNotFoundException.class, () -> comments.createComment(c, "sin-cuenta@x.co"));
            verify(commentPort, never()).saveComment(any());
        }

        @Test
        void soloSeCalificaUnaPracticaFinalizada() {
            Comment c = comentario();
            when(practicePort.findById(7L)).thenReturn(Practice.startFor(1L));
            assertThrows(BusinessRuleException.class, () -> comments.createComment(c, DUENO));
        }

        @Test
        void soloUnComentarioPorPractica() {
            Comment c = comentario();
            when(practicePort.findById(7L)).thenReturn(practicaFinalizada());
            when(commentPort.existsByIdPractice(7L)).thenReturn(true);
            assertThrows(BusinessRuleException.class, () -> comments.createComment(c, DUENO));
        }

        @Test
        @DisplayName("La calificación debe estar entre 1 y 5 estrellas y el comentario no puede ir vacío")
        void calificacionFueraDeRangoOTextoVacioEs400() {
            when(practicePort.findById(7L)).thenReturn(practicaFinalizada());
            Comment seis = comentario();
            seis.setCompanyRating(6.0);
            Comment sinNota = comentario();
            sinNota.setCompanyRating(null);
            Comment vacio = comentario();
            vacio.setCommentText("   ");
            Comment largo = comentario();
            largo.setCommentText("a".repeat(Comment.MAX_TEXT_LENGTH + 1));

            for (Comment c : List.of(seis, sinNota, vacio, largo)) {
                assertThrows(InvalidInputException.class, () -> comments.createComment(c, DUENO));
            }
            verify(commentPort, never()).saveComment(any());
        }

        @Test
        void comentarioValidoQuedaPendienteYNotificaACadaAdministrador() {
            when(practicePort.findById(7L)).thenReturn(practicaFinalizada());
            when(commentPort.saveComment(any())).thenAnswer(i -> i.getArgument(0));
            User admin1 = new User();
            admin1.setIdUser(1);
            User admin2 = new User();
            admin2.setIdUser(2);
            when(userPort.findByRole("administrador")).thenReturn(List.of(admin1, admin2));

            Comment guardado = comments.createComment(comentario(), DUENO);

            assertEquals(CommentStatus.PENDIENTE, guardado.getValidationStatus());
            verify(notificationUseCase, times(2)).createNotification(any());
        }

        @Test
        void listaComentariosAprobadosDeLaEmpresa() {
            when(commentPort.findApprovedByCompanyNit("900")).thenReturn(List.of(comentario()));
            assertEquals(1, comments.getApprovedCommentsByCompany("900").size());
        }
    }

    @Nested
    @DisplayName("Postularse a una oferta")
    class Postulaciones {

        private Postulation solicitud() {
            Postulation p = new Postulation();
            p.setIdOffer(10L);
            p.setDocumentNumberStudent("1003");
            return p;
        }

        @Test
        void noSePuedePostularDosVeces() {
            Postulation p = solicitud();
            when(postulationPort.existsByStudentAndOffer(10L, "1003")).thenReturn(true);
            assertThrows(BusinessRuleException.class, () -> postulations.createPostulation(p));
        }

        @Test
        void ofertaInexistenteEs404() {
            Postulation p = solicitud();
            when(offerPort.findById(10L)).thenReturn(null);
            assertThrows(ResourceNotFoundException.class, () -> postulations.createPostulation(p));
        }

        @Test
        void ofertaCerradaNoAceptaPostulaciones() {
            Postulation p = solicitud();
            Offer cerrada = ofertaConCupos(0);
            when(offerPort.findById(10L)).thenReturn(cerrada);
            assertThrows(BusinessRuleException.class, () -> postulations.createPostulation(p));
        }

        @Test
        void estudianteConPracticaActivaNoPuedePostularse() {
            Postulation p = solicitud();
            when(offerPort.findById(10L)).thenReturn(ofertaConCupos(3));
            when(postulationPort.hasActivePracticeOrAcceptedPostulation("1003")).thenReturn(true);
            assertThrows(BusinessRuleException.class, () -> postulations.createPostulation(p));
        }

        @Test
        void postulacionValidaNaceEnRevision() {
            when(offerPort.findById(10L)).thenReturn(ofertaConCupos(3));
            when(postulationPort.savePostulation(any())).thenAnswer(i -> i.getArgument(0));

            Postulation creada = postulations.createPostulation(solicitud());

            assertEquals(PostulationStatus.POSTULADA, creada.getStatusPostulation());
        }
    }

    @Nested
    @DisplayName("Verificación del correo del estudiante")
    class VerificacionEstudiante {

        private static final String CORREO = "ana@ucundinamarca.edu.co";

        private User pendiente(LocalDateTime expiracion) {
            User u = new User();
            u.setEmailAddres(CORREO);
            u.setUserStatus(false);
            u.setVerificationCode("hash-del-codigo");
            u.setCodeExpiration(expiracion);
            return u;
        }

        @Test
        void correoDesconocidoEs404() {
            when(userPort.findByCorreoElectronico(CORREO)).thenReturn(Optional.empty());
            assertThrows(ResourceNotFoundException.class, () -> verifyStudent.verifyCode(CORREO, "123456"));
        }

        @Test
        void cuentaYaVerificadaNoSeVuelveAVerificar() {
            User verificado = pendiente(LocalDateTime.now().plusMinutes(5));
            verificado.setUserStatus(true);
            when(userPort.findByCorreoElectronico(CORREO)).thenReturn(Optional.of(verificado));
            assertThrows(BusinessRuleException.class, () -> verifyStudent.verifyCode(CORREO, "123456"));
        }

        @Test
        void codigoExpiradoSeRechaza() {
            when(userPort.findByCorreoElectronico(CORREO)).thenReturn(Optional.of(pendiente(LocalDateTime.now().minusMinutes(1))));
            assertThrows(BusinessRuleException.class, () -> verifyStudent.verifyCode(CORREO, "123456"));
        }

        @Test
        void codigoIncorrectoSeRechaza() {
            when(userPort.findByCorreoElectronico(CORREO)).thenReturn(Optional.of(pendiente(LocalDateTime.now().plusMinutes(5))));
            when(passwordEncoder.matches("000000", "hash-del-codigo")).thenReturn(false);
            assertThrows(InvalidInputException.class, () -> verifyStudent.verifyCode(CORREO, "000000"));
        }

        @Test
        void codigoCorrectoActivaLaCuentaYBorraElCodigo() {
            User usuario = pendiente(LocalDateTime.now().plusMinutes(5));
            when(userPort.findByCorreoElectronico(CORREO)).thenReturn(Optional.of(usuario));
            when(passwordEncoder.matches("123456", "hash-del-codigo")).thenReturn(true);

            verifyStudent.verifyCode(CORREO, "123456");

            assertTrue(usuario.isUserStatus());
            assertNull(usuario.getVerificationCode());
            verify(userPort).saveUser(usuario);
        }
    }
}
