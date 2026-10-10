package co.edu.ucundinamarca.backendudecprac.domain.model;

import java.time.LocalDateTime;

public class User {

    private long idUser;
    private String emailAddres;
    private String password;
    private String userRol;
    private boolean userStatus =true;
    private String verificationCode;
    private LocalDateTime codeExpiration;
    private LocalDateTime creationDate;

    //constructor vacio
    public User() {}

    public User(long idUser, String emailAddres, String password, String userRol, boolean userStatus) {
        this.idUser = idUser;
        this.emailAddres = emailAddres;
        this.password = password;
        this.userRol = userRol;
        this.userStatus = userStatus;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public long getIdUser() {
        return idUser;
    }

    public void setIdUser(long idUser) {
        this.idUser = idUser;
    }

    public String getEmailAddres() {
        return emailAddres;
    }

    public void setEmailAddres(String emailAddres) {
        this.emailAddres = emailAddres;
    }

    public String getUserRol() {
        return userRol;
    }

    public void setUserRol(String userRol) {
        this.userRol = userRol;
    }

    public boolean isUserStatus() {
        return userStatus;
    }

    public void setUserStatus(boolean userStatus) {
        this.userStatus = userStatus;
    }

    public LocalDateTime getCodeExpiration() {
        return codeExpiration;
    }

    public void setCodeExpiration(LocalDateTime codeExpiration) {
        this.codeExpiration = codeExpiration;
    }

    public String getVerificationCode() {
        return verificationCode;
    }

    public void setVerificationCode(String verificationCode) {
        this.verificationCode = verificationCode;
    }
    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

}
