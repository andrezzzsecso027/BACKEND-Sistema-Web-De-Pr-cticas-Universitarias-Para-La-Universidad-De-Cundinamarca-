package co.edu.ucundinamarca.backendudecprac.domain.port.in;

public interface UpdatePasswordUseCase {
    void updatePassword(String email, String currentPassword, String newPassword);
}
