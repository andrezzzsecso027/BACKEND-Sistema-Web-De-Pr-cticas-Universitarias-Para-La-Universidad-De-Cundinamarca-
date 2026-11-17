package co.edu.ucundinamarca.backendudecprac.infrastructure.adapter.in.web.dtos;

public class VerificationRequest {
    private boolean aprobada;

    public VerificationRequest() {
        // Constructor vacío requerido por Jackson y por los mapeadores de persistencia
    }

    public boolean isAprobada() {
        return aprobada;
    }

    public void setAprobada(boolean aprobada) {
        this.aprobada = aprobada;
    }
}
