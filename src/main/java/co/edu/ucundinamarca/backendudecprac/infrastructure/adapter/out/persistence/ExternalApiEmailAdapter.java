package co.edu.ucundinamarca.backendudecprac.infrastructure.adapter.out.persistence;

import co.edu.ucundinamarca.backendudecprac.domain.port.out.EmailRepositoryPort;
import com.resend.Resend;
import com.resend.core.exception.ResendException;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import com.resend.services.emails.model.CreateEmailOptions;
import com.resend.services.emails.model.CreateEmailResponse;
import org.springframework.beans.factory.annotation.Value;
@Component
public class ExternalApiEmailAdapter implements EmailRepositoryPort {

    private final Resend resend;

    public ExternalApiEmailAdapter(@Value("${api.email.key}") String apiKey) {
        this.resend = new Resend(apiKey);
    }

    @Override
    public void sendEmailverification(String email, String code) {
        CreateEmailOptions params = CreateEmailOptions.builder()
                .from("onboarding@resend.dev")
                .to(email)
                .subject("Código de Verificación - Plataforma UDEC")
                .html("<h2>Bienvenido a la plataforma</h2><p>Tu código de verificación es: <strong>" + code + "</strong></p>")
                .build();

        try {
            CreateEmailResponse data = resend.emails().send(params);
            System.out.println("Correo enviado exitosamente. ID de Resend: " + data.getId());
        } catch (ResendException e) {
            System.err.println("Error fatal al enviar el correo: " + e.getMessage());
        }
    }
}
