package core.model;

import java.util.List;


public class SchoolMembership {

    private School school;
    private String[] roles;
    private List<EduGroup> eduGroups;

    public School getSchool() {
        return school;
    }

    public void setSchool(School school) {
        this.school = school;
    }

    public String[] getRoles() {
        return roles;
    }

    public void setRoles(String[] roles) {
        this.roles = roles;
    }

    public List<EduGroup> getEduGroups() {
        return eduGroups;
    }

    public void setEduGroups(List<EduGroup> eduGroups) {
        this.eduGroups = eduGroups;
    }
}