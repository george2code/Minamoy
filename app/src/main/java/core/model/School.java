package core.model;

import android.graphics.Bitmap;

public class School {

    private long id;
    private String name;
    private String fullName;
    private String avatarSmall;
    private String city;
    private Bitmap bitmap;
    private boolean isClassicSystem;

    //region Properties Get/Set

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getAvatarSmall() {
        return avatarSmall;
    }

    public void setAvatarSmall(String avatarSmall) {
        this.avatarSmall = avatarSmall;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public boolean isClassicSystem() {
        return isClassicSystem;
    }

    public void setIsClassicSystem(boolean isClassicSystem) {
        this.isClassicSystem = isClassicSystem;
    }
    //endregion
}