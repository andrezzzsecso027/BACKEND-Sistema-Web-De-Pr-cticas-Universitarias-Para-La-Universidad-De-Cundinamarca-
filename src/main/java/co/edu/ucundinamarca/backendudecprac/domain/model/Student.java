package co.edu.ucundinamarca.backendudecprac.domain.model;

import java.time.LocalDateTime;

public class Student {

    private Long idUser;
    private String document;
    private String name;
    private String lastName;
    private String addres;
    private String phoneNumber;
    private String headquarters;
    private String academicProgram;

    public Student() {}
    public Student(Long idUser, String document, String name, String lastName, String addres, String phoneNumber, String headquarters, String academicProgram) {
        this.idUser = idUser;
        this.document = document;
        this.name = name;
        this.lastName = lastName;
        this.addres = addres;
        this.phoneNumber = phoneNumber;
        this.headquarters = headquarters;
        this.academicProgram = academicProgram;
    }

    public Long getIdUser() {
        return idUser;
    }

    public void setIdUser(Long idUser) {
        this.idUser = idUser;
    }

    public String getDocument() {
        return document;
    }

    public void setDocument(String document) {
        this.document = document;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getAddres() {
        return addres;
    }

    public void setAddres(String addres) {
        this.addres = addres;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getHeadquarters() {
        return headquarters;
    }

    public void setHeadquarters(String headquarters) {
        this.headquarters = headquarters;
    }

    public String getAcademicProgram() {
        return academicProgram;
    }

    public void setAcademicProgram(String academicProgram) {
        this.academicProgram = academicProgram;
    }

}