package co.edu.ucundinamarca.backendudecprac.domain.model;

import java.time.LocalDateTime;

public class Notification {

    private long idNotification;
    private long idUser;
    private String typeNotification;
    private String message;
    private LocalDateTime dateSended;

    public Notification() {
    }

    public Notification(long idNotification, long idUser, String typeNotification, String message, LocalDateTime dateSended) {
        this.idNotification = idNotification;
        this.idUser = idUser;
        this.typeNotification = typeNotification;
        this.message = message;
        this.dateSended = dateSended;
    }

    public long getIdNotification() {
        return idNotification;
    }

    public void setIdNotification(long idNotification) {
        this.idNotification = idNotification;
    }

    public long getIdUser() {
        return idUser;
    }

    public void setIdUser(long idUser) {
        this.idUser = idUser;
    }

    public String getTypeNotification() {
        return typeNotification;
    }

    public void setTypeNotification(String typeNotification) {
        this.typeNotification = typeNotification;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getDateSended() {
        return dateSended;
    }

    public void setDateSended(LocalDateTime dateSended) {
        this.dateSended = dateSended;
    }
}
