package co.edu.ucundinamarca.backendudecprac.infrastructure.adapter.in.web;

import co.edu.ucundinamarca.backendudecprac.infrastructure.Security.JWTservice;
import co.edu.ucundinamarca.backendudecprac.infrastructure.adapter.out.persistence.UserEntity;
import co.edu.ucundinamarca.backendudecprac.infrastructure.adapter.out.persistence.UserJPArepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:4200")
public class AuthController{

    private final UserJPArepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JWTservice jwtService;

    public AuthController(UserJPArepository userRepository, AuthenticationManager authenticationManager, UserDetailsService userDetailsService, JWTservice jwtService) {
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            // 1. Spring Security verifica las contraseñas encriptadas de forma segura.
            // Si la contraseña es incorrecta, lanza una excepción que atrapamos en el catch.
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Correo o contraseña incorrectos");
        }

        // 2. Generamos el Token JWT
        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());
        String jwtToken = jwtService.generateToken(userDetails);

        // 3. Buscamos al usuario en la BD para extraer su ID y Rol
        UserEntity user = userRepository.findByCorreoElectronico(request.getEmail()).get();

        // 4. Armamos tu respuesta combinada
        LoginResponse response = new LoginResponse(
                user.getIdUsario(),
                user.getCorreoElectronico(),
                user.getRolUsuario(),
                jwtToken
        );

        return ResponseEntity.ok(response);
    }
}
