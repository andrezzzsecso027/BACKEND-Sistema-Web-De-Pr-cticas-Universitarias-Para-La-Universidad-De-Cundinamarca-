package co.edu.ucundinamarca.backendudecprac.domain.model;

public class User {

    private long idUser;
    private String emailAddres;
    private String password;
    private String userRol;
    private boolean userStatus =true;

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
}
