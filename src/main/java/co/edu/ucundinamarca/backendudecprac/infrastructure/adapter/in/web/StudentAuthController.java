package co.edu.ucundinamarca.backendudecprac.infrastructure.adapter.in.web;

import co.edu.ucundinamarca.backendudecprac.application.usecase.VerifyStudentUseCaseImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/estudiantes/auth")
@CrossOrigin(origins = "http://localhost:4200")
public class StudentAuthController {
    private final VerifyStudentUseCaseImpl verifyStudentUseCaseImpl;

    public StudentAuthController(VerifyStudentUseCaseImpl verifyStudentUseCaseImpl) {
        this.verifyStudentUseCaseImpl = verifyStudentUseCaseImpl;
    }
    @PostMapping("/verificar")
    public ResponseEntity<String> verifyCode(@RequestBody CodeVerificationRequest request){
        try {

            System.out.println("Email recibido en backend: [" + request.getEmail() + "]");
            System.out.println("Código recibido en backend: [" + request.getCode() + "]");

            verifyStudentUseCaseImpl.verifyCode(request.getEmail(), request.getCode());


            return ResponseEntity.ok("Cuenta verificada exitosamente. Ya puedes iniciar sesión.");

        } catch (IllegalArgumentException | IllegalStateException e) {

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ocurrió un error en el servidor.");
        }
    }
}
