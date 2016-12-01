package core.model;

import java.util.List;

public class EducationalMemebership {

    private long person;
    private List<SchoolMembership> schoolMemberships;

    public long getPerson() {
        return person;
    }

    public void setPerson(long person) {
        this.person = person;
    }

    public List<SchoolMembership> getSchools() {
        return schoolMemberships;
    }

    public void setSchools(List<SchoolMembership> schoolMemberships) {
        this.schoolMemberships = schoolMemberships;
    }
}