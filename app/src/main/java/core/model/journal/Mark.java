package core.model.journal;

import android.graphics.Color;
import java.util.Date;


public class Mark {
    //TODO: add fields description
    private long id;
    private String type;
    private String value;
    private String textValue;
    private long person;
    private long work;
    private long lesson;
    private int number;
    private Date date;
    private String workType;

    //region Properties

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getTextValue() {
        return textValue;
    }

    public void setTextValue(String textValue) {
        this.textValue = textValue;
    }

    public long getPerson() {
        return person;
    }

    public void setPerson(long person) {
        this.person = person;
    }

    public long getWork() {
        return work;
    }

    public void setWork(long work) {
        this.work = work;
    }

    public long getLesson() {
        return lesson;
    }

    public void setLesson(long lesson) {
        this.lesson = lesson;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getWorkType() {
        return workType;
    }

    public void setWorkType(String workType) {
        this.workType = workType;
    }

    //endregion

    public int getMarkBackground()
    {
        int markValue = Integer.valueOf(this.value);

        if (markValue > 85) {
            return Color.parseColor("#a9bd15");
        }
        else if (markValue <= 85 && markValue > 60) {
            return Color.parseColor("#f3931a");
        }
        else if (markValue <= 60) {
            return Color.parseColor("#ff6666");
        }
        else {
            return Color.parseColor("#000000");
        }
    }
}