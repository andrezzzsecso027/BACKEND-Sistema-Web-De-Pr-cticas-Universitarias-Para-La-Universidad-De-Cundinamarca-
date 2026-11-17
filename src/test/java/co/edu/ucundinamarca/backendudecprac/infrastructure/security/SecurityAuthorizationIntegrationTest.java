package co.edu.ucundinamarca.backendudecprac.infrastructure.security;

import co.edu.ucundinamarca.backendudecprac.infrastructure.adapter.out.persistence.entities.UserEntity;
import co.edu.ucundinamarca.backendudecprac.infrastructure.adapter.out.persistence.jpa.UserJPArepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Verifica de punta a punta (filtro JWT + reglas por rol) usando H2 en memoria.
 * No toca la base de datos real ni servicios externos.
 */
@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:security-test;DB_CLOSE_DELAY=-1",
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
class SecurityAuthorizationIntegrationTest {

    private static final String ADMIN = "admin@test.co";
    private static final String ESTUDIANTE = "estudiante@ucundinamarca.edu.co";
    private static final String EMPRESA = "empresa@test.co";
    private static final String EMPRESA_PENDIENTE = "pendiente@test.co";

    @Autowired private MockMvc mockMvc;
    @Autowired private JWTservice jwtService;
    @Autowired private UserDetailsService userDetailsService;
    @Autowired private UserJPArepository userRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    @BeforeEach
    void crearUsuarios() {
        // Roles en minúscula, tal como quedan en la BD real
        guardarSiNoExiste(ADMIN, "administrador", true);
        guardarSiNoExiste(ESTUDIANTE, "estudiante", true);
        guardarSiNoExiste(EMPRESA, "empresa", true);
        guardarSiNoExiste(EMPRESA_PENDIENTE, "empresa", false);
    }

    private void guardarSiNoExiste(String email, String rol, boolean activo) {
        if (userRepository.existsByCorreoElectronico(email)) return;
        UserEntity u = new UserEntity();
        u.setCorreoElectronico(email);
        u.setContrasenia(passwordEncoder.encode("secreta123"));
        u.setRolUsuario(rol);
        u.setEstado(activo);
        u.setFechaRegistro(LocalDateTime.now());
        userRepository.save(u);
    }

    private String bearer(String email) {
        return "Bearer " + jwtService.generateToken(userDetailsService.loadUserByUsername(email));
    }

    @Test
    @DisplayName("Sin token, un endpoint protegido responde 401")
    void sinTokenEndpointProtegidoDa401() throws Exception {
        mockMvc.perform(get("/api/admin/estudiantes")).andExpect(status().isUnauthorized());
        mockMvc.perform(get("/api/postulaciones/oferta/1")).andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Endpoints públicos funcionan sin token")
    void endpointsPublicosSinToken() throws Exception {
        mockMvc.perform(get("/api/ofertas/estudiantes")).andExpect(status().is2xxSuccessful());
        mockMvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"" + ADMIN + "\",\"password\":\"secreta123\"}"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Un token inválido en un endpoint público no lo bloquea")
    void tokenInvalidoEnEndpointPublicoNoBloquea() throws Exception {
        mockMvc.perform(get("/api/ofertas/estudiantes").header("Authorization", "Bearer basura.token.falso"))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    @DisplayName("Un token inválido en un endpoint protegido responde 401")
    void tokenInvalidoEnEndpointProtegidoDa401() throws Exception {
        mockMvc.perform(get("/api/admin/estudiantes").header("Authorization", "Bearer basura.token.falso"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("El administrador accede a /api/admin/**")
    void adminAccedeAPanelAdmin() throws Exception {
        mockMvc.perform(get("/api/admin/estudiantes").header("Authorization", bearer(ADMIN)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Estudiante y empresa reciben 403 en endpoints exclusivos del administrador")
    void otrosRolesNoAccedenAAdmin() throws Exception {
        mockMvc.perform(get("/api/admin/estudiantes").header("Authorization", bearer(ESTUDIANTE)))
                .andExpect(status().isForbidden());
        mockMvc.perform(get("/api/admin/empresas/pendientes").header("Authorization", bearer(EMPRESA)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("La empresa puede usar los listados compartidos del admin que necesita su panel")
    void empresaAccedeAListadosCompartidos() throws Exception {
        mockMvc.perform(get("/api/admin/ofertas").header("Authorization", bearer(EMPRESA)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Solo el estudiante puede postularse; la empresa recibe 403")
    void soloEstudiantePuedePostularse() throws Exception {
        String body = "{\"idOffer\":999,\"documentNumberStudent\":\"123\"}";
        mockMvc.perform(post("/api/postulaciones/aplicar").header("Authorization", bearer(EMPRESA))
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isForbidden());
        // El estudiante pasa la seguridad (la oferta 999 no existe, pero ya no es un 401/403)
        mockMvc.perform(post("/api/postulaciones/aplicar").header("Authorization", bearer(ESTUDIANTE))
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().is(not(403)))
                .andExpect(status().is(not(401)));
    }

    @Test
    @DisplayName("Un usuario deshabilitado (empresa pendiente) no se autentica aunque tenga token firmado")
    void usuarioDeshabilitadoDa401() throws Exception {
        String token = jwtService.generateToken(
                User.withUsername(EMPRESA_PENDIENTE).password("x").roles(Roles.EMPRESA).build());
        mockMvc.perform(get("/api/admin/ofertas").header("Authorization", "Bearer " + token))
                .andExpect(status().isUnauthorized());
    }
}
