package co.edu.ucundinamarca.backendudecprac.domain.port.in;

public interface UploadFileUseCase {
    String uploadFile(byte[] fileBytes, String originalFilename, String folder);
}
