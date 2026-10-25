package co.edu.ucundinamarca.backendudecprac.infrastructure.adapter.in.web.controllers;

import co.edu.ucundinamarca.backendudecprac.domain.port.in.UploadFileUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/archivos")
@CrossOrigin(origins = "http://localhost:4200")
public class FileUploadController {
    private final UploadFileUseCase uploadFileUseCase;

    public FileUploadController(UploadFileUseCase uploadFileUseCase) {
        this.uploadFileUseCase = uploadFileUseCase;
    }
    @PostMapping("/subir")
    public ResponseEntity<?> subirArchivo(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "carpeta", defaultValue = "general") String carpeta) {
        try {
            String urlCloudinary = uploadFileUseCase.uploadFile(
                    file.getBytes(),
                    file.getOriginalFilename(),
                    carpeta
            );
            return ResponseEntity.ok(Map.of("url", urlCloudinary));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}
