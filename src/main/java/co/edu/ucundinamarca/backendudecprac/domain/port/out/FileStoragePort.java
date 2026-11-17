package co.edu.ucundinamarca.backendudecprac.domain.port.out;

/**
 * Puerto de salida para almacenar archivos (implementado hoy con Cloudinary).
 * Devuelve la URL pública del archivo almacenado.
 */
public interface FileStoragePort {
    String uploadFile(byte[] fileBytes, String fileName, String folder);
}
