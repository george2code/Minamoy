package core.model.journal;

import java.util.Date;
import java.util.List;
import core.model.Subject;


public class Lesson implements Comparable {
    private long id;                // идентификатор урока
    private String title;           // тема урока
    private Date date;              // дата проведения урока
    private int number;             // номер урока в расписани
    private Subject subject;        // предмет урока
    private List<Work> works;       // список работ на уроке
    private String groupNames;
    private long[] teachers;        // список идентификаторов учителей

    private String place;
    private Date start;
    private Date finish;

    //region Properties

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    public List<Work> getWorks() {
        return works;
    }

    public void setWorks(List<Work> works) {
        this.works = works;
    }

    public long[] getTeachers() {
        return teachers;
    }

    public void setTeachers(long[] teachers) {
        this.teachers = teachers;
    }

    public String getGroupNames() {
        return groupNames;
    }

    public void setGroupNames(String groupNames) {
        this.groupNames = groupNames;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

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

    //endregion

    public Work getJournalWork() {
        Work resultWork = null;

        if (this.works != null && !this.works.isEmpty()) {
            for(Work work : this.works) {
                if (work.getDisplayInJournal()) {
                    resultWork = work;
                    break;
                }
            }
        }

        return resultWork;
    }

    @Override
    public int compareTo(Object o)
    {
        return (date.after(((Lesson)o).date)) ? 1 : 0;
    }
}