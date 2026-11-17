package co.edu.ucundinamarca.backendudecprac.infrastructure.adapter.in.web;

import co.edu.ucundinamarca.backendudecprac.infrastructure.adapter.out.persistence.entities.UserEntity;
import co.edu.ucundinamarca.backendudecprac.infrastructure.adapter.out.persistence.jpa.UserJPArepository;
import co.edu.ucundinamarca.backendudecprac.infrastructure.security.JWTservice;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Envía los mismos JSON que arma el frontend (perfil-estudiante.ts y panel-empresa.ts)
 * para garantizar que los DTOs nuevos aceptan exactamente esos campos. Usa H2 en memoria.
 */
@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:contract-test;DB_CLOSE_DELAY=-1",
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
class FrontendContractIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private JWTservice jwtService;
    @Autowired private UserDetailsService userDetailsService;
    @Autowired private UserJPArepository userRepository;

    @BeforeEach
    void usuarios() {
        guardar("est@ucundinamarca.edu.co", "estudiante");
        guardar("emp@test.co", "empresa");
    }

    private void guardar(String email, String rol) {
        if (userRepository.existsByCorreoElectronico(email)) return;
        UserEntity u = new UserEntity();
        u.setCorreoElectronico(email);
        u.setContrasenia("x");
        u.setRolUsuario(rol);
        u.setEstado(true);
        u.setFechaRegistro(LocalDateTime.now());
        userRepository.save(u);
    }

    private String bearer(String email) {
        return "Bearer " + jwtService.generateToken(userDetailsService.loadUserByUsername(email));
    }

    @Test
    @DisplayName("POST perfil de estudiante acepta el JSON del frontend y pasa por el caso de uso")
    void perfilEstudiante() throws Exception {
        String payloadFrontend = """
                {"documentStudent":"999","aboutMe":"Hola","skills":"Java","englishLevel":"B1",
                 "githubUrl":"https://github.com/x","linkLifesheet":"https://cv.pdf","photoUrl":"https://foto.png"}
                """;
        // El estudiante 999 no existe: el caso de uso lanza ResourceNotFoundException -> 404 (antes se guardaba sin validar)
        mockMvc.perform(post("/api/registro/estudiantes/perfil").header("Authorization", bearer("est@ucundinamarca.edu.co"))
                        .contentType(MediaType.APPLICATION_JSON).content(payloadFrontend))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("El estudiante no existe")));
    }

    @Test
    @DisplayName("PUT perfil de empresa acepta el JSON del frontend (con representante legal anidado)")
    void perfilEmpresa() throws Exception {
        String payloadFrontend = """
                {"companyName":"ACME","legalName":"ACME SAS","companyType":"Privada","companyPhoneNumber":"300",
                 "department":"Cundinamarca","city":"Fusagasugá","companyAddress":"Calle 1","companyDescription":"desc",
                 "logoUrl":"https://logo.png","rutPdfUrl":"https://rut.pdf",
                 "legalRepresentative":{"nameRepresentative":"Ana","lastNameRepresentative":"Pérez","numberDocument":"123"}}
                """;
        mockMvc.perform(put("/api/registro/empresas/nit/NO-EXISTE/perfil").header("Authorization", bearer("emp@test.co"))
                        .contentType(MediaType.APPLICATION_JSON).content(payloadFrontend))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST seguimiento de contrato acepta el JSON del frontend y devuelve el documento guardado")
    void seguimientoContrato() throws Exception {
        String payloadFrontend = """
                {"idPostulation":1,"nitCompany":"900-1","documentStudent":"1001","studentFullName":"Ana Pérez",
                 "offerName":"Práctica QA","fileName":"contrato.pdf","fileUrl":"https://contrato.pdf",
                 "companyComments":"ok","trackingStatus":"CONTRATO_FIRMADO"}
                """;
        mockMvc.perform(post("/api/documentos/seguimiento").header("Authorization", bearer("emp@test.co"))
                        .contentType(MediaType.APPLICATION_JSON).content(payloadFrontend))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idPostulation").value(1))
                .andExpect(jsonPath("$.nitCompany").value("900-1"))
                .andExpect(jsonPath("$.trackingStatus").value("CONTRATO_FIRMADO"))
                .andExpect(jsonPath("$.uploadDate").exists());
    }

    // ---------- Manejo global de errores: texto plano (lo que el frontend muestra con err.error) ----------

    @Test
    @DisplayName("Postulación inexistente -> 404 con el mensaje del dominio en texto plano UTF-8")
    void errorNoEncontrado() throws Exception {
        mockMvc.perform(patch("/api/postulaciones/999/estado").header("Authorization", bearer("emp@test.co"))
                        .contentType(MediaType.APPLICATION_JSON).content("{\"newStatus\":\"ACEPTADA\"}"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN))
                .andExpect(content().string("La postulación no existe."));
    }

    @Test
    @DisplayName("Estado desconocido -> 400 indicando los valores permitidos")
    void errorEstadoInvalido() throws Exception {
        mockMvc.perform(patch("/api/postulaciones/999/estado").header("Authorization", bearer("emp@test.co"))
                        .contentType(MediaType.APPLICATION_JSON).content("{\"newStatus\":\"APROBADISIMA\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("POSTULADA, ACEPTADA, RECHAZADA")));
    }

    @Test
    @DisplayName("Contraseña actual incorrecta -> 401 (el interceptor del frontend no cierra sesión en /auth)")
    void errorCredenciales() throws Exception {
        mockMvc.perform(patch("/api/auth/cambiar-contrasena").header("Authorization", bearer("est@ucundinamarca.edu.co"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"otro@correo.co\",\"currentPassword\":\"mala\",\"newPassword\":\"nueva123\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("La contraseña actual ingresada es incorrecta."));
    }

    @Test
    @DisplayName("JSON mal formado -> 400 (antes el handler genérico lo convertía en 500)")
    void errorJsonMalFormado() throws Exception {
        mockMvc.perform(post("/api/postulaciones/aplicar").header("Authorization", bearer("est@ucundinamarca.edu.co"))
                        .contentType(MediaType.APPLICATION_JSON).content("{esto no es json"))
                .andExpect(status().isBadRequest());
    }
}
