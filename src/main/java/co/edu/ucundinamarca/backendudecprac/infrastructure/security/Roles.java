package co.edu.ucundinamarca.backendudecprac.infrastructure.security;

/**
 * Roles del sistema tal como los entiende Spring Security.
 * En BD se guardan en minúscula ("estudiante", "empresa", "administrador");
 * UserDetailsServiceImpl los normaliza a mayúscula para evitar errores por mayúsculas/minúsculas.
 */
public final class Roles {

    public static final String ESTUDIANTE = "ESTUDIANTE";
    public static final String EMPRESA = "EMPRESA";
    public static final String ADMINISTRADOR = "ADMINISTRADOR";

    private Roles() {
    }
}
