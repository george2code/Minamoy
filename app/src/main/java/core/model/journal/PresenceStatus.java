package core.model.journal;

//[{"id":2,"status":"Absent","shortName":"ה","fullName":"היעדרות","color":"#116ea9"}

public class PresenceStatus {
    private long id;
    private String status;
    private String shortName;
    private String fullName;
    private String color;

    public PresenceStatus() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}