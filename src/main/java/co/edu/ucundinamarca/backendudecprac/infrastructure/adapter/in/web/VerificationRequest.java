package co.edu.ucundinamarca.backendudecprac.infrastructure.adapter.in.web;

public class VerificationRequest {
    private boolean aprobada;

    public VerificationRequest() {}

    public boolean isAprobada() {
        return aprobada;
    }

    public void setAprobada(boolean aprobada) {
        this.aprobada = aprobada;
    }
}
