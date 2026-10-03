package co.edu.ucundinamarca.backendudecprac.infrastructure.adapter.in.web;

import co.edu.ucundinamarca.backendudecprac.infrastructure.adapter.out.persistence.UserEntity;
import co.edu.ucundinamarca.backendudecprac.infrastructure.adapter.out.persistence.UserJPArepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/autenticacion")
@CrossOrigin(origins = "http://localhost:4200")
public class AutenticacionController {
    private final UserJPArepository usuariojparepository;

    public AutenticacionController(UserJPArepository usuariojparepository) {
        this.usuariojparepository = usuariojparepository;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        Optional<UserEntity> usuarioOpt = usuariojparepository.findByCorreoElectronico(request.getEmail());

        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Correo o contraseña incorrectos");
        }

        UserEntity usuario = usuarioOpt.get();

        if (!usuario.getContrasenia().equals(request.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Correo o contraseña incorrectos");
        }

        LoginResponse response = new LoginResponse(
                usuario.getIdUsario(),
                usuario.getCorreoElectronico(),
                usuario.getRolUsuario()
        );

        return ResponseEntity.ok(response);
    }
}
