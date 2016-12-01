package core.model;

import java.util.Date;
import java.util.List;


public class Timetable
{
    private String name;
    private Date start;
    private Date end;
    private int firstLessonNumber;  // 0 or 1
    private List<TimetableItem> items;

    //region Property
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }

    public int getFirstLessonNumber() {
        return firstLessonNumber;
    }

    public void setFirstLessonNumber(int firstLessonNumber) {
        this.firstLessonNumber = firstLessonNumber;
    }

    public List<TimetableItem> getItems() {
        return items;
    }

    public void setItems(List<TimetableItem> items) {
        this.items = items;
    }
    //end
}