package co.edu.ucundinamarca.backendudecprac.infrastructure.adapter.out.persistence;

import jakarta.persistence.*;

@Entity
@Table(name="usuarios")
public class usuarioEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long idUsario;

    @Column(nullable = false, unique=true)
    private String correoElectronico;
    @Column(nullable = false)
    private String contrasenia;

    @Column(nullable = false)
    private String rolUsuario;

    @Column(nullable = false)
    private boolean estado=true;

    public usuarioEntity() {}

    public long getIdUsario() {
        return idUsario;
    }

    public void setIdUsario(long idUsario) {
        this.idUsario = idUsario;
    }

    public String getCorreoElectronico() {
        return correoElectronico;
    }

    public void setCorreoElectronico(String correoElectronico) {
        this.correoElectronico = correoElectronico;
    }

    public String getContrasenia() {
        return contrasenia;
    }

    public void setContrasenia(String contrasenia) {
        this.contrasenia = contrasenia;
    }

    public String getRolUsuario() {
        return rolUsuario;
    }

    public void setRolUsuario(String rolUsuario) {
        this.rolUsuario = rolUsuario;
    }

    public boolean isEstado() {
        return estado;
    }

    public void setEstado(boolean estado) {
        this.estado = estado;
    }

}
