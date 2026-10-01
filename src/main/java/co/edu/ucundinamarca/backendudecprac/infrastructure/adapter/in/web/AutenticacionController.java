package co.edu.ucundinamarca.backendudecprac.infrastructure.adapter.in.web;

import co.edu.ucundinamarca.backendudecprac.infrastructure.adapter.out.persistence.usuarioEntity;
import co.edu.ucundinamarca.backendudecprac.infrastructure.adapter.out.persistence.usuarioJPArepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/autenticacion")
@CrossOrigin(origins = "http://localhost:4200")
public class autenticacionController {
    private final usuarioJPArepository usuariojparepository;

    public autenticacionController(usuarioJPArepository usuariojparepository) {
        this.usuariojparepository = usuariojparepository;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody loginRequest request) {
        Optional<usuarioEntity> usuarioOpt = usuariojparepository.findByCorreoElectronico(request.getCorreo());

        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Correo o contraseña incorrectos");
        }

        usuarioEntity usuario = usuarioOpt.get();

        if (!usuario.getContrasenia().equals(request.getContrasenia())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Correo o contraseña incorrectos");
        }

        loginResponse response = new loginResponse(
                usuario.getIdUsario(),
                usuario.getCorreoElectronico(),
                usuario.getRolUsuario()
        );

        return ResponseEntity.ok(response);
    }
}
