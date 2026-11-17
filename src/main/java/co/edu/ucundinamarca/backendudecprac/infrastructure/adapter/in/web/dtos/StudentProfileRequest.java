package co.edu.ucundinamarca.backendudecprac.infrastructure.adapter.in.web.dtos;

import co.edu.ucundinamarca.backendudecprac.domain.model.StudentProfile;

/** Cuerpo de POST /api/registro/estudiantes/perfil (mismos nombres de campo que envía el frontend). */
public class StudentProfileRequest {
    private String documentStudent;
    private String aboutMe;
    private String skills;
    private String englishLevel;
    private String githubUrl;
    private String linkLifesheet;
    private String photoUrl;

    public StudentProfile toDomain() {
        StudentProfile profile = new StudentProfile();
        profile.setDocumentStudent(documentStudent);
        profile.setAboutMe(aboutMe);
        profile.setSkills(skills);
        profile.setEnglishLevel(englishLevel);
        profile.setGithubUrl(githubUrl);
        profile.setLinkLifesheet(linkLifesheet);
        profile.setPhotoUrl(photoUrl);
        return profile;
    }

    public String getDocumentStudent() { return documentStudent; }
    public void setDocumentStudent(String documentStudent) { this.documentStudent = documentStudent; }
    public String getAboutMe() { return aboutMe; }
    public void setAboutMe(String aboutMe) { this.aboutMe = aboutMe; }
    public String getSkills() { return skills; }
    public void setSkills(String skills) { this.skills = skills; }
    public String getEnglishLevel() { return englishLevel; }
    public void setEnglishLevel(String englishLevel) { this.englishLevel = englishLevel; }
    public String getGithubUrl() { return githubUrl; }
    public void setGithubUrl(String githubUrl) { this.githubUrl = githubUrl; }
    public String getLinkLifesheet() { return linkLifesheet; }
    public void setLinkLifesheet(String linkLifesheet) { this.linkLifesheet = linkLifesheet; }
    public String getPhotoUrl() { return photoUrl; }
    public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }
}
