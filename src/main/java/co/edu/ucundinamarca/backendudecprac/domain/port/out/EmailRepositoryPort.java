package co.edu.ucundinamarca.backendudecprac.domain.port.out;

public interface EmailRepositoryPort {
    void sendEmailverification (String email, String code);
}
