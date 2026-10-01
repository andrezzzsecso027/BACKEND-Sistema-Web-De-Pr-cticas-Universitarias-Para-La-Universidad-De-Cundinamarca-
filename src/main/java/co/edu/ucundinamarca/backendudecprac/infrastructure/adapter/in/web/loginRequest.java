package co.edu.ucundinamarca.backendudecprac.infrastructure.adapter.in.web;

public class loginRequest {
    private String correo;
    private String contrasenia;

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }
    public String getContrasenia() { return contrasenia; }
    public void setContrasenia(String contrasenia) { this.contrasenia = contrasenia; }
}
