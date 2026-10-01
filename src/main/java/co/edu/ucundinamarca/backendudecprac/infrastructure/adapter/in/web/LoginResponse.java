package co.edu.ucundinamarca.backendudecprac.infrastructure.adapter.in.web;

public class loginResponse {
    private Long idUsuario;
    private String correo;
    private String rolUsuario;

    public loginResponse(Long idUsuario, String correo, String rolUsuario) {
        this.idUsuario = idUsuario;
        this.correo = correo;
        this.rolUsuario = rolUsuario;
    }

    public Long getIdUsuario() { return idUsuario; }
    public String getCorreo() { return correo; }
    public String getRolUsuario() { return rolUsuario; }
}
