package core.model;

import android.graphics.Bitmap;
import java.util.List;


public class User {
    private long person;
    private String name;
    private List<SchoolMembership> schoolMemberships;
    private Bitmap photo;
    private String avatar;

    public long getPerson() {
        return person;
    }

    public void setPerson(long person) {
        this.person = person;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<SchoolMembership> getSchoolMemberships() {
        return schoolMemberships;
    }

    public void setSchoolMemberships(List<SchoolMembership> schoolMemberships) {
        this.schoolMemberships = schoolMemberships;
    }

    public Bitmap getPhoto() {
        return photo;
    }

    public void setPhoto(Bitmap photo) {
        this.photo = photo;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}