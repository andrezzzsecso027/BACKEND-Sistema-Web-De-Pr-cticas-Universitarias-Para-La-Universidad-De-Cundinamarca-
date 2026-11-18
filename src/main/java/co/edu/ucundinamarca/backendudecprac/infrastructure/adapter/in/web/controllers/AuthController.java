package co.edu.ucundinamarca.backendudecprac.infrastructure.adapter.in.web.controllers;

import co.edu.ucundinamarca.backendudecprac.domain.port.in.UpdatePasswordUseCase;
import co.edu.ucundinamarca.backendudecprac.infrastructure.security.JWTservice;
import co.edu.ucundinamarca.backendudecprac.infrastructure.adapter.in.web.dtos.LoginRequest;
import co.edu.ucundinamarca.backendudecprac.infrastructure.adapter.in.web.dtos.LoginResponse;
import co.edu.ucundinamarca.backendudecprac.infrastructure.adapter.in.web.dtos.UpdatePasswordRequest;
import co.edu.ucundinamarca.backendudecprac.domain.model.User;
import co.edu.ucundinamarca.backendudecprac.domain.port.in.FindUserUseCase;
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
public class AuthController{

    private final FindUserUseCase findUserUseCase;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JWTservice jwtService;
    private final UpdatePasswordUseCase updatePasswordUseCase;
    public AuthController(FindUserUseCase findUserUseCase, AuthenticationManager authenticationManager, UserDetailsService userDetailsService, JWTservice jwtService, UpdatePasswordUseCase updatePasswordUseCase) {
        this.findUserUseCase = findUserUseCase;
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
        User user = findUserUseCase.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        // 4. Armamos tu respuesta combinada
        LoginResponse response = new LoginResponse(
                user.getIdUser(),
                user.getEmailAddres(),
                user.getUserRol(),
                jwtToken
        );

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/cambiar-contrasena")
    /**
     * El correo se toma del token JWT (usuario autenticado), no del cuerpo: así nadie puede
     * intentar cambiar la contraseña de otra cuenta. Errores -> GlobalExceptionHandler (400 / 401 / 404).
     */
    public ResponseEntity<String> cambiarContrasena(@RequestBody UpdatePasswordRequest request,
                                                    java.security.Principal principal) {
        updatePasswordUseCase.updatePassword(
                principal.getName(),
                request.getCurrentPassword(),
                request.getNewPassword()
        );
        return ResponseEntity.ok("Contraseña actualizada exitosamente.");
    }
}
