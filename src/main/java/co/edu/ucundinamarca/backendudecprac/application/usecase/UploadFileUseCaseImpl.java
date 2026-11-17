package co.edu.ucundinamarca.backendudecprac.application.usecase;

import co.edu.ucundinamarca.backendudecprac.domain.exceptions.InvalidInputException;
import co.edu.ucundinamarca.backendudecprac.domain.port.in.UploadFileUseCase;
import co.edu.ucundinamarca.backendudecprac.domain.port.out.FileStoragePort;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UploadFileUseCaseImpl implements UploadFileUseCase {
    private final FileStoragePort fileStoragePort;

    public UploadFileUseCaseImpl(FileStoragePort fileStoragePort) {
        this.fileStoragePort = fileStoragePort;
    }

    @Override
    public String uploadFile(byte[] fileBytes, String originalFilename, String folder) {
        if (fileBytes == null || fileBytes.length == 0) {
            throw new InvalidInputException("El archivo enviado está vacío.");
        }

        String extension = "";
        String baseName = "archivo";

        if (originalFilename != null && originalFilename.contains(".")) {
            int lastDot = originalFilename.lastIndexOf(".");
            baseName = originalFilename.substring(0, lastDot).replaceAll("[^a-zA-Z0-9_-]", "_");
            extension = originalFilename.substring(lastDot).toLowerCase(); // ej: ".pdf" o ".png"
        }

        // Si es PDF conservamos la extensión .pdf en el public_id para que el navegador lo abra como documento
        String uniquePublicId = baseName + "_" + UUID.randomUUID().toString().substring(0, 8)
                + (extension.equals(".pdf") ? ".pdf" : "");

        return fileStoragePort.uploadFile(fileBytes, uniquePublicId, folder);
    }
}
