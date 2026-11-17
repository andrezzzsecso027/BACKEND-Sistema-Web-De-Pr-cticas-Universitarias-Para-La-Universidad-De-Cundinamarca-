package co.edu.ucundinamarca.backendudecprac.infrastructure.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

import static co.edu.ucundinamarca.backendudecprac.infrastructure.security.Roles.ADMINISTRADOR;
import static co.edu.ucundinamarca.backendudecprac.infrastructure.security.Roles.EMPRESA;
import static co.edu.ucundinamarca.backendudecprac.infrastructure.security.Roles.ESTUDIANTE;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final AuthenticationProvider authenticationProvider;
    private final JWTservice jwtService;
    private final UserDetailsService userDetailsService;

    public SecurityConfig(AuthenticationProvider authenticationProvider,
                          JWTservice jwtService,
                          UserDetailsService userDetailsService) {
        this.authenticationProvider = authenticationProvider;
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable()) // API stateless con JWT: no usa cookies de sesión
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // ---------- Públicos (sin token) ----------
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/error", "/actuator/health").permitAll()
                        .requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**", "/api-docs/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/registro/estudiantes", "/api/registro/empresas").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/estudiantes/auth/verificar").permitAll()
                        // El registro de empresa sube el PDF del NIT antes de tener cuenta
                        .requestMatchers(HttpMethod.POST, "/api/archivos/subir").permitAll()
                        // Catálogo público de ofertas (también lo usa el healthcheck del Dockerfile)
                        .requestMatchers(HttpMethod.GET, "/api/ofertas/estudiantes").permitAll()

                        // ---------- Administrador ----------
                        // El panel de empresa reutiliza estos dos listados para ubicar su propia empresa/ofertas
                        .requestMatchers(HttpMethod.GET, "/api/admin/ofertas", "/api/admin/empresas/todas")
                            .hasAnyRole(ADMINISTRADOR, EMPRESA)
                        .requestMatchers("/api/admin/**").hasRole(ADMINISTRADOR)
                        .requestMatchers(HttpMethod.GET, "/api/documentos/todos").hasRole(ADMINISTRADOR)

                        // ---------- Empresa ----------
                        .requestMatchers(HttpMethod.POST, "/api/ofertas/crear").hasRole(EMPRESA)
                        .requestMatchers(HttpMethod.PATCH, "/api/postulaciones/*/estado").hasRole(EMPRESA)
                        .requestMatchers(HttpMethod.GET, "/api/postulaciones/oferta/*").hasAnyRole(EMPRESA, ADMINISTRADOR)
                        .requestMatchers(HttpMethod.PATCH, "/api/practicas/*").hasRole(EMPRESA)
                        .requestMatchers(HttpMethod.PUT, "/api/registro/empresas/nit/*/perfil").hasRole(EMPRESA)
                        .requestMatchers(HttpMethod.POST, "/api/documentos/seguimiento").hasRole(EMPRESA)
                        .requestMatchers(HttpMethod.GET, "/api/documentos/empresa/*").hasAnyRole(EMPRESA, ADMINISTRADOR)
                        .requestMatchers(HttpMethod.GET, "/api/ia/ranking-postulados/*").hasAnyRole(EMPRESA, ADMINISTRADOR)

                        // ---------- Estudiante ----------
                        .requestMatchers(HttpMethod.POST, "/api/postulaciones/aplicar").hasRole(ESTUDIANTE)
                        .requestMatchers(HttpMethod.POST, "/api/registro/estudiantes/perfil").hasRole(ESTUDIANTE)
                        .requestMatchers(HttpMethod.GET, "/api/ia/recomendar-ofertas/*").hasRole(ESTUDIANTE)
                        .requestMatchers(HttpMethod.POST, "/api/comentarios/crear").hasRole(ESTUDIANTE)
                        .requestMatchers(HttpMethod.GET, "/api/estudiantes/me", "/api/estudiantes/me/**").hasRole(ESTUDIANTE)

                        // ---------- Cualquier usuario autenticado ----------
                        // (cambiar contraseña, consultar perfiles, prácticas, historial, listados, archivos)
                        .anyRequest().authenticated()
                )
                // Sin token válido -> 401 (no 403), así el frontend sabe que debe volver a iniciar sesión
                .exceptionHandling(ex -> ex.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(new JwtAuthenticationFilter(jwtService, userDetailsService),
                        UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:4200"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
