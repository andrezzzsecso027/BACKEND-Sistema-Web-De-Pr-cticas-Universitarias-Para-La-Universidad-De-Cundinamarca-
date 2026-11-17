package co.edu.ucundinamarca.backendudecprac.architecture;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Hace cumplir la regla de dependencias de la arquitectura hexagonal leyendo los imports del código fuente:
 *
 *   infrastructure  ──►  application  ──►  domain
 *
 * Las dependencias solo apuntan hacia el dominio. Si alguien rompe la regla, este test falla
 * y muestra el archivo y el import exacto.
 */
class HexagonalArchitectureTest {

    private static final Path BASE = Path.of("src/main/java/co/edu/ucundinamarca/backendudecprac");
    private static final String PKG = "co.edu.ucundinamarca.backendudecprac";

    @Test
    @DisplayName("El dominio es puro: no depende de aplicación, infraestructura ni de ningún framework")
    void dominioNoDependeDeNadaExterno() throws IOException {
        List<String> violaciones = buscarImports(BASE.resolve("domain"), List.of(
                PKG + ".application",
                PKG + ".infrastructure",
                "org.springframework",
                "jakarta.",
                "io.jsonwebtoken",
                "com.cloudinary"
        ));
        assertTrue(violaciones.isEmpty(), "El dominio tiene dependencias prohibidas:\n" + String.join("\n", violaciones));
    }

    @Test
    @DisplayName("La aplicación solo depende del dominio (de Spring únicamente @Service y @Transactional)")
    void aplicacionNoDependeDeInfraestructura() throws IOException {
        List<String> violaciones = buscarImports(BASE.resolve("application"), List.of(
                PKG + ".infrastructure",
                "org.springframework.security",
                "org.springframework.web",
                "org.springframework.data",
                "jakarta.persistence"
        ));
        assertTrue(violaciones.isEmpty(), "La aplicación depende de infraestructura:\n" + String.join("\n", violaciones));
    }

    @Test
    @DisplayName("Los controladores (adaptadores de entrada) solo usan puertos de entrada, nunca repositorios ni implementaciones")
    void controladoresSoloUsanPuertosDeEntrada() throws IOException {
        List<String> violaciones = buscarImports(BASE.resolve("infrastructure/adapter/in/web/controllers"), List.of(
                PKG + ".domain.port.out",
                PKG + ".application",
                PKG + ".infrastructure.adapter.out"
        ));
        assertTrue(violaciones.isEmpty(), "Un controlador se salta los casos de uso:\n" + String.join("\n", violaciones));
    }

    private List<String> buscarImports(Path carpeta, List<String> prohibidos) throws IOException {
        List<String> violaciones = new ArrayList<>();
        try (Stream<Path> archivos = Files.walk(carpeta)) {
            for (Path archivo : archivos.filter(p -> p.toString().endsWith(".java")).toList()) {
                for (String linea : Files.readAllLines(archivo)) {
                    String l = linea.trim();
                    if (!l.startsWith("import ")) continue;
                    String importado = l.replace("import static ", "").replace("import ", "");
                    for (String prohibido : prohibidos) {
                        if (importado.startsWith(prohibido)) {
                            violaciones.add("  " + BASE.relativize(archivo) + " -> " + l);
                        }
                    }
                }
            }
        }
        return violaciones;
    }
}
