package co.edu.ucundinamarca.backendudecprac.domain.port.out;

public interface FileStorageRepositoryPort {
    String uploadFile(byte[] fileBytes, String fileName, String folder);
}
