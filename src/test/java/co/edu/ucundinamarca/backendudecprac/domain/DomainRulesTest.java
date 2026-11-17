package co.edu.ucundinamarca.backendudecprac.domain;

import co.edu.ucundinamarca.backendudecprac.domain.exceptions.BusinessRuleException;
import co.edu.ucundinamarca.backendudecprac.domain.exceptions.InvalidInputException;
import co.edu.ucundinamarca.backendudecprac.domain.exceptions.InvalidStateTransitionException;
import co.edu.ucundinamarca.backendudecprac.domain.model.Company;
import co.edu.ucundinamarca.backendudecprac.domain.model.Offer;
import co.edu.ucundinamarca.backendudecprac.domain.model.Postulation;
import co.edu.ucundinamarca.backendudecprac.domain.model.Practice;
import co.edu.ucundinamarca.backendudecprac.domain.model.status.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Reglas de negocio que ahora viven en el dominio (sin Spring, sin mocks, sin BD).
 */
class DomainRulesTest {

    private static final LocalDateTime NOW = LocalDateTime.of(2026, 11, 19, 17, 0);

    @Nested
    @DisplayName("Postulación")
    class PostulacionTest {

        private Postulation nueva() {
            Postulation p = new Postulation();
            p.setIdOffer(1L);
            p.setDocumentNumberStudent("1001");
            p.submit(NOW);
            return p;
        }

        @Test
        @DisplayName("Nace en POSTULADA (en revisión)")
        void naceEnRevision() {
            assertEquals(PostulationStatus.POSTULADA, nueva().getStatusPostulation());
        }

        @Test
        @DisplayName("POSTULADA -> ACEPTADA devuelve el estado anterior para el historial")
        void aceptar() {
            Postulation p = nueva();
            PostulationStatus anterior = p.changeStatus(PostulationStatus.ACEPTADA, "Bienvenido", NOW);
            assertEquals(PostulationStatus.POSTULADA, anterior);
            assertTrue(p.isAccepted());
        }

        @Test
        @DisplayName("Una postulación RECHAZADA no se puede aceptar después, ni aceptar dos veces")
        void estadosFinales() {
            Postulation rechazada = nueva();
            rechazada.changeStatus(PostulationStatus.RECHAZADA, null, NOW);
            assertThrows(InvalidStateTransitionException.class,
                    () -> rechazada.changeStatus(PostulationStatus.ACEPTADA, null, NOW));

            Postulation aceptada = nueva();
            aceptada.changeStatus(PostulationStatus.ACEPTADA, null, NOW);
            assertThrows(InvalidStateTransitionException.class,
                    () -> aceptada.changeStatus(PostulationStatus.ACEPTADA, null, NOW));
        }
    }

    @Nested
    @DisplayName("Oferta")
    class OfertaTest {

        @Test
        @DisplayName("Aceptar consume un cupo y la oferta se cierra sola al llegar a 0")
        void reservaCupos() {
            Offer o = new Offer();
            o.setAvailableSpots(1);
            o.publish(LocalDate.of(2026, 11, 1));
            assertTrue(o.isOpenForApplications());

            o.reserveSpot();
            assertEquals(0, o.getAvailableSpots());
            assertFalse(o.getOfferStatus());
            assertThrows(BusinessRuleException.class, o::reserveSpot);
        }

        @Test
        @DisplayName("No se publica sin cupos ni con fecha de cierre pasada")
        void validaPublicacion() {
            Offer sinCupos = new Offer();
            sinCupos.setAvailableSpots(0);
            LocalDate hoy = LocalDate.now();
            assertThrows(InvalidInputException.class, () -> sinCupos.publish(hoy));

            Offer vencida = new Offer();
            vencida.setAvailableSpots(3);
            vencida.setClosingDate(LocalDate.now().minusDays(1));
            assertThrows(InvalidInputException.class, () -> vencida.publish(hoy));
        }
    }

    @Nested
    @DisplayName("Práctica")
    class PracticaTest {

        @Test
        @DisplayName("Nace EN_CURSO y solo puede pasar a FINALIZADA (estado final)")
        void cicloDeVida() {
            Practice p = Practice.startFor(10L);
            assertEquals(PracticeStatus.EN_CURSO, p.getPracticeStatus());

            p.update(null, null, PracticeStatus.FINALIZADA);
            assertTrue(p.isFinished());
            assertThrows(InvalidStateTransitionException.class, () -> p.update(null, null, PracticeStatus.EN_CURSO));
        }

        @Test
        @DisplayName("La fecha de fin no puede ser anterior a la de inicio")
        void validaFechas() {
            Practice p = Practice.startFor(10L);
            LocalDate inicio = LocalDate.of(2026, 6, 1);
            LocalDate finAnterior = LocalDate.of(2026, 1, 1);
            assertThrows(InvalidInputException.class, () -> p.update(inicio, finAnterior, null));
        }
    }

    @Nested
    @DisplayName("Empresa")
    class EmpresaTest {

        @Test
        @DisplayName("PENDIENTE -> APROBADA -> RECHAZADA (inhabilitar); repetir el mismo estado no se permite")
        void verificacion() {
            Company c = new Company();
            c.setRutPdfUrl("https://res.cloudinary.com/demo/udecprac/empresas_nit/rut.pdf");
            assertEquals(CompanyVerificationStatus.PENDIENTE, c.getVerificationStatus());
            c.approve();
            assertTrue(c.isApproved());
            assertThrows(InvalidStateTransitionException.class, c::approve);
            c.reject();
            assertFalse(c.isApproved());
        }

        @Test
        @DisplayName("No se puede aprobar una empresa sin certificado RUT/NIT en PDF")
        void sinCertificadoNoSeAprueba() {
            Company sinPdf = new Company();
            assertThrows(BusinessRuleException.class, sinPdf::approve);

            Company conOtroArchivo = new Company();
            conOtroArchivo.setRutPdfUrl("https://res.cloudinary.com/demo/logo.png");
            assertThrows(BusinessRuleException.class, conOtroArchivo::approve);
            assertEquals(CompanyVerificationStatus.PENDIENTE, conOtroArchivo.getVerificationStatus());

            Company sinHttps = new Company();
            sinHttps.setRutPdfUrl("http://servidor/rut.pdf");
            assertFalse(sinHttps.hasCertificadoNit());
        }
    }

    @Nested
    @DisplayName("Conversión de estados desde texto (BD y frontend)")
    class ParseoTest {

        @Test
        @DisplayName("Tolera mayúsculas/minúsculas, tildes, espacios y valores históricos")
        void tolerante() {
            assertEquals(PostulationStatus.POSTULADA, PostulationStatus.from("en revisión"));
            assertEquals(PostulationStatus.ACEPTADA, PostulationStatus.from(" aceptada "));
            assertEquals(PracticeStatus.EN_CURSO, PracticeStatus.from("ASIGNADA"));
            assertEquals(ContractTrackingStatus.CONTRATO_FIRMADO, ContractTrackingStatus.from("contrato firmado"));
        }

        @Test
        @DisplayName("Un valor desconocido es un error de entrada (400) que lista los valores permitidos")
        void invalido() {
            InvalidInputException ex = assertThrows(InvalidInputException.class, () -> PostulationStatus.from("APROBADISIMA"));
            assertTrue(ex.getMessage().contains("POSTULADA"));
        }
    }
}
