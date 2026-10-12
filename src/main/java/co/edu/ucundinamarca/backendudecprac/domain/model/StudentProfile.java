package co.edu.ucundinamarca.backendudecprac.domain.model;

public class StudentProfile {
    private Long idProfile;
    private String documentStudent;
    private String skills;
    private String englishLevel;
    private String linkLifesheet;
    private String githubUrl;
    private String aboutMe;

    public StudentProfile() {}
    public String getLinkLifesheet() {
        return linkLifesheet;
    }

    public void setLinkLifesheet(String linkLifesheet) {
        this.linkLifesheet = linkLifesheet;
    }

    public Long getIdProfile() {
        return idProfile;
    }

    public void setIdProfile(Long idProfile) {
        this.idProfile = idProfile;
    }

    public String getDocumentStudent() {
        return documentStudent;
    }

    public void setDocumentStudent(String documentStudent) {
        this.documentStudent = documentStudent;
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

}
