package core.model;

import java.util.Date;


public class TimetableItem {
    private Date start;
    private Date finish;
    private String name;
    private String type;
    private String[] daysOfWeek;
    private int number;
    private Date lessonDate;
    private String place;


    public TimetableItem() {
    }

    public TimetableItem(Date lessonDate, String name, int number) {
        this.lessonDate = lessonDate;
        this.name = name;
        this.number = number;
    }


    //region Property
    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public Date getFinish() {
        return finish;
    }

    public void setFinish(Date finish) {
        this.finish = finish;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String[] getDaysOfWeek() {
        return daysOfWeek;
    }

    public void setDaysOfWeek(String[] daysOfWeek) {
        this.daysOfWeek = daysOfWeek;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public Date getLessonDate() {
        return lessonDate;
    }

    public void setLessonDate(Date lessonDate) {
        this.lessonDate = lessonDate;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    //endregion
}