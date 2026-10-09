package co.edu.ucundinamarca.backendudecprac.domain.port.in;

public interface VerifyStudentUseCase {
    void verifyCode(String email, String code);
}
