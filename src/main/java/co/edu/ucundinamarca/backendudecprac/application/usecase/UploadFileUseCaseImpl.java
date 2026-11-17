package co.edu.ucundinamarca.backendudecprac.application.usecase;

import co.edu.ucundinamarca.backendudecprac.domain.exceptions.InvalidInputException;
import co.edu.ucundinamarca.backendudecprac.domain.port.in.UploadFileUseCase;
import co.edu.ucundinamarca.backendudecprac.domain.port.out.FileStoragePort;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Sube archivos al almacenamiento externo validando antes de gastar ancho de banda o cuota:
 *  - solo carpetas conocidas (el endpoint es público porque el registro de empresa sube el NIT sin cuenta),
 *  - tipo de archivo según la carpeta (PDF para documentos, imagen para fotos/logos),
 *  - el contenido real del archivo (firma de bytes), no solo la extensión del nombre,
 *  - tamaño máximo de 10 MB.
 */
@Service
public class UploadFileUseCaseImpl implements UploadFileUseCase {

    public static final long TAMANO_MAXIMO_BYTES = 10L * 1024 * 1024;

    private enum Tipo { PDF, IMAGEN }

    private static final Map<String, Tipo> CARPETAS = Map.of(
            "empresas_nit", Tipo.PDF,
            "contratos_practicas", Tipo.PDF,
            "estudiantes_cv", Tipo.PDF,
            "empresas_logos", Tipo.IMAGEN,
            "estudiantes_fotos", Tipo.IMAGEN,
            "admin_perfil", Tipo.IMAGEN
    );

    private static final Set<String> EXTENSIONES_IMAGEN = Set.of(".png", ".jpg", ".jpeg", ".webp");

    private final FileStoragePort fileStoragePort;

    public UploadFileUseCaseImpl(FileStoragePort fileStoragePort) {
        this.fileStoragePort = fileStoragePort;
    }

    @Override
    public String uploadFile(byte[] fileBytes, String originalFilename, String folder) {
        if (fileBytes == null || fileBytes.length == 0) {
            throw new InvalidInputException("El archivo enviado está vacío.");
        }
        if (fileBytes.length > TAMANO_MAXIMO_BYTES) {
            throw new InvalidInputException("El archivo supera el tamaño máximo permitido de 10 MB.");
        }
        Tipo tipo = CARPETAS.get(folder);
        if (tipo == null) {
            throw new InvalidInputException("Carpeta de destino no permitida: " + folder);
        }

        String extension = "";
        String baseName = "archivo";
        if (originalFilename != null && originalFilename.contains(".")) {
            int lastDot = originalFilename.lastIndexOf('.');
            baseName = originalFilename.substring(0, lastDot).replaceAll("[^a-zA-Z0-9_-]", "_");
            extension = originalFilename.substring(lastDot).toLowerCase(Locale.ROOT);
        }

        if (tipo == Tipo.PDF && (!".pdf".equals(extension) || !esPdf(fileBytes))) {
            throw new InvalidInputException("El documento debe ser un archivo PDF válido.");
        }
        if (tipo == Tipo.IMAGEN && (!EXTENSIONES_IMAGEN.contains(extension) || !esImagen(fileBytes))) {
            throw new InvalidInputException("La imagen debe ser PNG, JPG o WEBP.");
        }

        // Para PDF se conserva la extensión en el public_id para que el navegador lo abra como documento
        String uniquePublicId = baseName + "_" + UUID.randomUUID().toString().substring(0, 8)
                + (tipo == Tipo.PDF ? ".pdf" : "");

        return fileStoragePort.uploadFile(fileBytes, uniquePublicId, folder);
    }

    /** Cada PDF empieza por "%PDF-". */
    private static boolean esPdf(byte[] b) {
        return empiezaCon(b, "%PDF-".getBytes(StandardCharsets.US_ASCII));
    }

    private static boolean esImagen(byte[] b) {
        boolean png = empiezaCon(b, new byte[]{(byte) 0x89, 'P', 'N', 'G'});
        boolean jpg = empiezaCon(b, new byte[]{(byte) 0xFF, (byte) 0xD8, (byte) 0xFF});
        boolean webp = b.length >= 12 && empiezaCon(b, "RIFF".getBytes(StandardCharsets.US_ASCII))
                && b[8] == 'W' && b[9] == 'E' && b[10] == 'B' && b[11] == 'P';
        return png || jpg || webp;
    }

    private static boolean empiezaCon(byte[] datos, byte[] prefijo) {
        if (datos.length < prefijo.length) return false;
        for (int i = 0; i < prefijo.length; i++) {
            if (datos[i] != prefijo[i]) return false;
        }
        return true;
    }
}
