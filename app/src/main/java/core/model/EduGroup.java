package core.model;

import java.util.List;
import core.IDialogItems;

public class EduGroup implements IDialogItems {
    private long id;
    private long[] parentIds;
    private String type;
    private String name;
    private String fullName;
    private int parallel;
    private long timetable;
    private List<Subject> subjects;
    private long periodId;

    public EduGroup() {}

    public EduGroup(long id, String fullName) {
        this.id = id;
        this.fullName = fullName;
    }

    //region Properties

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long[] getParentIds() {
        return parentIds;
    }

    public void setParentIds(long[] parentIds) {
        this.parentIds = parentIds;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public int getParallel() {
        return parallel;
    }

    public void setParallel(int parallel) {
        this.parallel = parallel;
    }

    public long getTimetable() {
        return timetable;
    }

    public void setTimetable(long timetable) {
        this.timetable = timetable;
    }

    public List<Subject> getSubjects() {
        return subjects;
    }

    public void setSubjects(List<Subject> subjects) {
        this.subjects = subjects;
    }

    public long getPeriodId() {
        return periodId;
    }

    public void setPeriodId(long periodId) {
        this.periodId = periodId;
    }

    @Override
    public String getItemName() {
        return getName();
    }

    @Override
    public long getItemValue() {
        return getId();
    }

    //endregion
}