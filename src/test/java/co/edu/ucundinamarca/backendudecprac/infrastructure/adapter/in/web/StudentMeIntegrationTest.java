package co.edu.ucundinamarca.backendudecprac.infrastructure.adapter.in.web;

import co.edu.ucundinamarca.backendudecprac.domain.model.*;
import co.edu.ucundinamarca.backendudecprac.domain.model.status.CompanyVerificationStatus;
import co.edu.ucundinamarca.backendudecprac.domain.model.status.PostulationStatus;
import co.edu.ucundinamarca.backendudecprac.domain.port.out.*;
import co.edu.ucundinamarca.backendudecprac.infrastructure.security.JWTservice;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** GET /api/estudiantes/me y /me/postulaciones: identidad desde el token, solo datos propios. */
@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:student-me-test;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.show-sql=false",
        "jwt.secret=clave-de-pruebas-de-al-menos-32-bytes-para-hs256",
        "brevo.api.key=test", "brevo.sender.email=test@test.co", "brevo.sender.name=test",
        "cloudinary.cloud-name=test", "cloudinary.api-key=test", "cloudinary.api-secret=test",
        "gemini.api.key="
})
@AutoConfigureMockMvc
class StudentMeIntegrationTest {

    private static final String ANA = "ana@ucundinamarca.edu.co";
    private static final String LUIS = "luis@ucundinamarca.edu.co";
    private static final String EMPRESA = "talento@nexus.co";

    @Autowired private MockMvc mockMvc;
    @Autowired private JWTservice jwtService;
    @Autowired private UserDetailsService userDetailsService;
    @Autowired private UserRepositoryPort users;
    @Autowired private StudentRepositoryPort students;
    @Autowired private CompanyRepositoryPort companies;
    @Autowired private OfferRepositoryPort offers;
    @Autowired private PostulationRepositoryPort postulations;

    private static boolean sembrado = false;

    @BeforeEach
    void sembrar() {
        if (sembrado) return;
        sembrado = true;
        long idAna = usuario(ANA, "estudiante");
        long idLuis = usuario(LUIS, "estudiante");
        long idEmpresa = usuario(EMPRESA, "empresa");
        estudiante("1001", idAna, "Ana");
        estudiante("2002", idLuis, "Luis");

        Company c = new Company();
        c.setNit("830-1"); c.setIdUser(idEmpresa); c.setCompanyName("Nexus Software");
        c.setVerificationStatus(CompanyVerificationStatus.APROBADA);
        companies.saveCompany(c);

        Offer o = new Offer();
        o.setPracticeName("Practicante backend"); o.setNitCompanyAssociated("830-1"); o.setPracticeDescription("Spring Boot");
        o.setProfession("Ingeniería de Sistemas"); o.setModality("Remoto"); o.setLocation("Bogotá");
        o.setSalary(BigDecimal.valueOf(1300000)); o.setAvailableSpots(2); o.setClosingDate(LocalDate.now().plusDays(10));
        o.setPublicationDate(LocalDate.now());
        Long idOferta = offers.saveOffer(o).getIdOffer();

        postular(idOferta, "1001");
        postular(idOferta, "2002");
    }

    private long usuario(String email, String rol) {
        User u = new User();
        u.setEmailAddres(email); u.setPassword("x"); u.setUserRol(rol); u.setUserStatus(true);
        u.setCreationDate(LocalDateTime.now());
        return users.saveUser(u).getIdUser();
    }

    private void estudiante(String doc, long idUser, String nombre) {
        Student s = new Student();
        s.setIdUser(idUser); s.setDocument(doc); s.setName(nombre); s.setLastName("Prueba");
        s.setAddres("Calle 1"); s.setPhoneNumber("300"); s.setHeadquarters("Fusagasugá");
        s.setAcademicProgram("Ingeniería de Sistemas");
        students.saveStudent(s);
    }

    private void postular(Long idOferta, String doc) {
        Postulation p = new Postulation();
        p.setIdOffer(idOferta); p.setDocumentNumberStudent(doc);
        p.submit(LocalDateTime.now());
        postulations.savePostulation(p);
    }

    private String bearer(String email) {
        return "Bearer " + jwtService.generateToken(userDetailsService.loadUserByUsername(email));
    }

    @Test
    @DisplayName("GET /me devuelve el estudiante de la sesión (su cédula), sin pedirla por parámetro")
    void miPerfil() throws Exception {
        mockMvc.perform(get("/api/estudiantes/me").header("Authorization", bearer(ANA)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.document").value("1001"))
                .andExpect(jsonPath("$.name").value("Ana"));
    }

    @Test
    @DisplayName("GET /me/postulaciones devuelve solo las postulaciones propias, con oferta y empresa")
    void misPostulaciones() throws Exception {
        mockMvc.perform(get("/api/estudiantes/me/postulaciones").header("Authorization", bearer(ANA)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].offerName").value("Practicante backend"))
                .andExpect(jsonPath("$[0].companyName").value("Nexus Software"))
                .andExpect(jsonPath("$[0].status").value(PostulationStatus.POSTULADA.name()));
    }

    @Test
    @DisplayName("Una empresa no puede usar los endpoints del estudiante (403) y sin token es 401")
    void soloEstudiantes() throws Exception {
        mockMvc.perform(get("/api/estudiantes/me").header("Authorization", bearer(EMPRESA))).andExpect(status().isForbidden());
        mockMvc.perform(get("/api/estudiantes/me/postulaciones")).andExpect(status().isUnauthorized());
    }
}
