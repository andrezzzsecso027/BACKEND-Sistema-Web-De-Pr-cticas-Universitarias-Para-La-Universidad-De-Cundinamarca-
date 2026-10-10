package co.edu.ucundinamarca.backendudecprac.infrastructure.adapter.out.persistence;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name="usuarios")
public class UserEntity {

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

    @Column(name = "codigo_verificacion", length = 100)
    private String codigoVerificacion;

    @Column(name = "fecha_expiracion_codigo")
    private LocalDateTime fechaExpiracionCodigo;

    @Column(name= "fecha_registro")
    private LocalDateTime fechaRegistro;


    public UserEntity() {}

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

    public String getCodigoVerificacion() {
        return codigoVerificacion;
    }

    public void setCodigoVerificacion(String codigoVerificacion) {
        this.codigoVerificacion = codigoVerificacion;
    }

    public LocalDateTime getFechaExpiracionCodigo() {
        return fechaExpiracionCodigo;
    }

    public void setFechaExpiracionCodigo(LocalDateTime fechaExpiracionCodigo) {
        this.fechaExpiracionCodigo = fechaExpiracionCodigo;
    }
    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }
}
