package co.edu.ucundinamarca.backendudecprac.domain.model;

public class Usuario {

    private long idUsuario;
    private String correoElectronico;
    private String contraseña;
    private String rolUsuario;
    private boolean estadoUsuario;

    //constructor vacio
    public Usuario() {}

    //constructor con parametros
    public Usuario(long idUsuario, String correoElectronico, String contraseña, String rolUsuario, boolean estadoUsuario) {
        this.idUsuario = idUsuario;
        this.correoElectronico = correoElectronico;
        this.contraseña = contraseña;
        this.rolUsuario = rolUsuario;
        this.estadoUsuario = estadoUsuario;
    }

    public String getContraseña() {
        return contraseña;
    }

    public void setContraseña(String contraseña) {
        this.contraseña = contraseña;
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
