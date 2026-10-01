package co.edu.ucundinamarca.backendudecprac.domain.model;

public class Usuario {

    private long idUsuario;
    private String correoElectronico;
    private String contrasenia;
    private String rolUsuario;
    private boolean estadoUsuario=true;

    //constructor vacio
    public Usuario() {}

    public Usuario(long idUsuario, String correoElectronico, String contrasenia, String rolUsuario, boolean estadoUsuario) {
        this.idUsuario = idUsuario;
        this.correoElectronico = correoElectronico;
        this.contrasenia = contrasenia;
        this.rolUsuario = rolUsuario;
        this.estadoUsuario = estadoUsuario;
    }

    public String getContrasenia() {
        return contrasenia;
    }

    public void setContrasenia(String contrasenia) {
        this.contrasenia = contrasenia;
    }

    public long getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(long idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getCorreoElectronico() {
        return correoElectronico;
    }

    public void setCorreoElectronico(String correoElectronico) {
        this.correoElectronico = correoElectronico;
    }

    public String getRolUsuario() {
        return rolUsuario;
    }

    public void setRolUsuario(String rolUsuario) {
        this.rolUsuario = rolUsuario;
    }

    public boolean isEstadoUsuario() {
        return estadoUsuario;
    }

    public void setEstadoUsuario(boolean estadoUsuario) {
        this.estadoUsuario = estadoUsuario;
    }
}
