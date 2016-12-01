package core.model;

import android.net.Uri;

public class Person {
    private long id;
    private long userId;
    private String firstName;
    private String lastName;
    private String middleName;
    private String sex;
    private long parentPersonId;
    private String className;

    private String avatarUrl;
    //region Properties

    public Person() {}


    public Long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public long getParentPersonId() {
        return parentPersonId;
    }

    public void setParentPersonId(long parentPersonId) {
        this.parentPersonId = parentPersonId;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }
    //endregion
}