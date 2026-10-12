package co.edu.ucundinamarca.backendudecprac.infrastructure.adapter.out.persistence.Entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.*;
import javax.naming.Name;

@Entity
@Table(name = "perfiles_estudiantes")
public class StudentProfileEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id_perfil")
    private Long idProfile;

    @Column(name="habilidades")
    private String skills;

    @Column(name="nivel_ingles")
    private String englishLevel;

    @Column(name="link_hojaDeVida")
    private String linkLifesheet; //

    @Column(name="url_github")
    private String githubUrl;

    @Column(name="sobre_mi", columnDefinition = "TEXT")
    private String aboutMe;


    @OneToOne
    @JoinColumn(name = "cc_estudiante", referencedColumnName = "documento", unique = true)
    private StudentEntity student;


    public Long getIdProfile() {
        return idProfile;
    }

    public void setIdProfile(Long idProfile) {
        this.idProfile = idProfile;
    }

    public String getSkills() {
        return skills;
    }

    public void setSkills(String skills) {
        this.skills = skills;
    }

    public String getEnglishLevel() {
        return englishLevel;
    }

    public void setEnglishLevel(String englishLevel) {
        this.englishLevel = englishLevel;
    }

    public String getLinkLifesheet() {
        return linkLifesheet;
    }

    public void setLinkLifesheet(String linkLifesheet) {
        this.linkLifesheet = linkLifesheet;
    }

    public String getGithubUrl() {
        return githubUrl;
    }

    public void setGithubUrl(String githubUrl) {
        this.githubUrl = githubUrl;
    }

    public String getAboutMe() {
        return aboutMe;
    }

    public void setAboutMe(String aboutMe) {
        this.aboutMe = aboutMe;
    }

    public StudentEntity getStudent() {
        return student;
    }

    public void setStudent(StudentEntity student) {
        this.student = student;
    }
}
