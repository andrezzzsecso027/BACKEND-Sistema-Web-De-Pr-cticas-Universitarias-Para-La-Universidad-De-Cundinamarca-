package co.edu.ucundinamarca.backendudecprac.infrastructure.adapter.in.web.controllers;

import co.edu.ucundinamarca.backendudecprac.domain.port.in.UpdatePasswordUseCase;
import co.edu.ucundinamarca.backendudecprac.infrastructure.security.JWTservice;
import co.edu.ucundinamarca.backendudecprac.infrastructure.adapter.in.web.dtos.LoginRequest;
import co.edu.ucundinamarca.backendudecprac.infrastructure.adapter.in.web.dtos.LoginResponse;
import co.edu.ucundinamarca.backendudecprac.infrastructure.adapter.in.web.dtos.UpdatePasswordRequest;
import co.edu.ucundinamarca.backendudecprac.infrastructure.adapter.out.persistence.entities.UserEntity;
import co.edu.ucundinamarca.backendudecprac.infrastructure.adapter.out.persistence.jpa.UserJPArepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:4200")
public class AuthController{

    private final UserJPArepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JWTservice jwtService;
    private final UpdatePasswordUseCase updatePasswordUseCase;
    public AuthController(UserJPArepository userRepository, AuthenticationManager authenticationManager, UserDetailsService userDetailsService, JWTservice jwtService, UpdatePasswordUseCase updatePasswordUseCase) {
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtService = jwtService;
        this.updatePasswordUseCase = updatePasswordUseCase;
    }

    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody LoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
        } catch (DisabledException e) {
            // 2. MAGIA: Atrapamos específicamente a los usuarios con estado 'false'
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Tu cuenta de empresa aún está pendiente de verificación por el administrador.");
        } catch (BadCredentialsException e) {
            // 3. Atrapamos contraseñas incorrectas
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Correo o contraseña incorrectos");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Ocurrió un error en la autenticación");
        }

        // 2. Generamos el Token JWT
        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());
        String jwtToken = jwtService.generateToken(userDetails);

        // 3. Buscamos al usuario en la BD para extraer su ID y Rol
        UserEntity user = userRepository.findByCorreoElectronico(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        // 4. Armamos tu respuesta combinada
        LoginResponse response = new LoginResponse(
                user.getIdUsario(),
                user.getCorreoElectronico(),
                user.getRolUsuario(),
                jwtToken
        );

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/cambiar-contrasena")
    public ResponseEntity<String> cambiarContrasena(@RequestBody UpdatePasswordRequest request) {
        try {
            updatePasswordUseCase.updatePassword(
                    request.getEmail(),
                    request.getCurrentPassword(),
                    request.getNewPassword()
            );
            return ResponseEntity.ok("Contraseña actualizada exitosamente.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }
}
