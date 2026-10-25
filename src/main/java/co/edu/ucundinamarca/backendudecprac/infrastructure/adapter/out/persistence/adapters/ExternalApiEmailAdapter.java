package co.edu.ucundinamarca.backendudecprac.infrastructure.adapter.out.persistence.adapters;

import co.edu.ucundinamarca.backendudecprac.domain.port.out.EmailRepositoryPort;

import org.springframework.stereotype.Component;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ExternalApiEmailAdapter implements EmailRepositoryPort {


    @Value("${brevo.api.key}")
    private String apiKey;

    @Value("${brevo.sender.email}")
    private String senderEmail;

    @Value("${brevo.sender.name}")
    private String senderName;

    private final RestTemplate restTemplate = new RestTemplate();
    private static final String BREVO_URL = "https://api.brevo.com/v3/smtp/email";


    @Override
    public void sendEmailverification(String toEmail, String content) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.set("api-key", apiKey);

        String htmlBody = "<div style='font-family: Arial, sans-serif; padding: 20px; border: 1px solid #e0e0e0; border-radius: 8px;'>"
                + "<h2 style='color: #00482B;'>Notificación de UdecPrac</h2>"
                + "<p style='font-size: 16px; color: #333;'>" + content + "</p>"
                + "<hr style='border: none; border-top: 1px solid #eee; margin: 20px 0;'>"
                + "<small style='color: #888;'>Sistema de Prácticas Universitarias - Universidad de Cundinamarca</small>"
                + "</div>";

        Map<String, Object> body = new HashMap<>();
        body.put("sender", Map.of("name", senderName.trim(), "email", senderEmail.trim()));
        body.put("replyTo", Map.of("name", senderName.trim(), "email", senderEmail.trim()));
        body.put("to", List.of(Map.of("email", toEmail.trim())));
        body.put("subject", "Notificación Oficial - Sistema UdecPrac");
        body.put("htmlContent", htmlBody);
        body.put("textContent", content);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        try {
            restTemplate.postForEntity(BREVO_URL, request, String.class);
            System.out.println("Correo enviado con éxito a través de Brevo para: " + toEmail);
        } catch (Exception e) {
            throw new RuntimeException("Error enviando correo con Brevo: " + e.getMessage());
        }
    }
}
