package co.edu.ucundinamarca.backendudecprac.infrastructure.adapter.in.web.DTOs;

public class UpdatePostulationRequest {
    private String newStatus;
    private String observations;

    public String getNewStatus() {
        return newStatus;
    }

    public void setNewStatus(String newStatus) {
        this.newStatus = newStatus;
    }

    public String getObservations() {
        return observations;
    }

    public void setObservations(String observations) {
        this.observations = observations;
    }
}
