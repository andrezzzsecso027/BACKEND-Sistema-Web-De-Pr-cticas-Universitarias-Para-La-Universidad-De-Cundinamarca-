package co.edu.ucundinamarca.backendudecprac.domain.model.status;

import co.edu.ucundinamarca.backendudecprac.domain.exceptions.InvalidInputException;

import java.text.Normalizer;
import java.util.Arrays;
import java.util.Locale;

/**
 * Convierte textos (de la BD o del frontend) a enums de estado de forma tolerante:
 * "en revisión", "En_Revision" y "EN_REVISION" se tratan igual.
 */
final class StatusParser {

    private StatusParser() {
    }

    static String normalize(String value) {
        if (value == null) return null;
        String sinTildes = Normalizer.normalize(value.trim(), Normalizer.Form.NFD).replaceAll("\\p{M}", "");
        return sinTildes.toUpperCase(Locale.ROOT).replaceAll("[\\s-]+", "_");
    }

    static <E extends Enum<E>> E parse(Class<E> type, String normalized, String rawValue, String entityName) {
        if (normalized == null || normalized.isBlank()) {
            throw new InvalidInputException("El estado de " + entityName + " es obligatorio.");
        }
        try {
            return Enum.valueOf(type, normalized);
        } catch (IllegalArgumentException e) {
            throw new InvalidInputException("Estado de " + entityName + " inválido: '" + rawValue
                    + "'. Valores permitidos: " + Arrays.toString(type.getEnumConstants()) + ".");
        }
    }
}
