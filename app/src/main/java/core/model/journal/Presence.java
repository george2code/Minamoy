package core.model.journal;

import android.graphics.Color;


public class Presence {
    private long person;
    private long lesson;
    private String comment;
    private String status;


    public long getPerson() {
        return person;
    }

    public void setPerson(long person) {
        this.person = person;
    }

    public long getLesson() {
        return lesson;
    }

    public void setLesson(long lesson) {
        this.lesson = lesson;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getPresenceBackground() {
        String presenceStatus = this.status;
        int result = Color.parseColor("#ffffff");


        switch (presenceStatus) {
            case "Attend":
                break;
            case "Pass":
                result = Color.parseColor("#ff6666");
                break;
            case "Late":
                result = Color.parseColor("#f9a23b");
                break;
            case "Ill":
            case "LateJustified":
                result = Color.parseColor("#459dd6");
                break;
            case "Absent":
            case "NotSet":
                result = Color.parseColor("#116ea9");
                break;
            default:
                break;
        }

        return result;
    }
}