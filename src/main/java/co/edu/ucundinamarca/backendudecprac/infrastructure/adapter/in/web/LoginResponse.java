package co.edu.ucundinamarca.backendudecprac.infrastructure.adapter.in.web;

public class LoginResponse {
    private Long idUser;
    private String email;
    private String userRol;


    private String token;


    public LoginResponse(Long idUser, String email, String userRol, String token) {
        this.idUser = idUser;
        this.email = email;
        this.userRol = userRol;
        this.token = token;
    }



    public Long getIdUser() { return idUser; }
    public String getEmail() { return email; }
    public String getUserRol() { return userRol; }
    public String getToken() {return token;}

}
