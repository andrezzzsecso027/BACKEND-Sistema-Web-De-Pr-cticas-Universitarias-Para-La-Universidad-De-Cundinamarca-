package co.edu.ucundinamarca.backendudecprac.infrastructure.adapter.in.web.controllers;

import co.edu.ucundinamarca.backendudecprac.domain.port.in.VerifyStudentUseCase;
import co.edu.ucundinamarca.backendudecprac.infrastructure.adapter.in.web.dtos.CodeVerificationRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/estudiantes/auth")
@CrossOrigin(origins = "http://localhost:4200")
public class StudentAuthController {

    private final VerifyStudentUseCase verifyStudentUseCase;

    public StudentAuthController(VerifyStudentUseCase verifyStudentUseCase) {
        this.verifyStudentUseCase = verifyStudentUseCase;
    }

    @PostMapping("/verificar")
    public ResponseEntity<String> verifyCode(@RequestBody CodeVerificationRequest request) {
        verifyStudentUseCase.verifyCode(request.getEmail(), request.getCode());
        return ResponseEntity.ok("Cuenta verificada exitosamente. Ya puedes iniciar sesión.");
    }
}
