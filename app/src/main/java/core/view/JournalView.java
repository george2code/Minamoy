package core.view;

import android.content.Context;
import java.util.List;
import java.util.Map;
import core.model.EduGroup;
import core.model.Person;
import core.model.journal.Behaviour;
import core.model.journal.BehaviourStatus;
import core.model.journal.PresenceStatus;
import core.model.journal.JournalFinalMark;
import core.model.journal.Lesson;
import core.model.journal.Mark;
import core.model.journal.Presence;

public class JournalView
{
    private Context context;
    private List<Person> students;
    private List<JournalFinalMark> finalMarks;
    private Map<String, List<Lesson>> lessons;
    private List<Mark> marks;
    private List<Presence> presences;
    private List<String> presenceSchoolArray;
    private List<BehaviourStatus> behaviourSchoolList;
    private Map<Long, List<Behaviour>> behaviours;
    private List<EduGroup> groups;

    private Long subjectGroupId;
    private long periodId;

    public JournalView(Context context) {
        this.context = context;
    }

    //region Properties

    public Context getContext() { return context; }

    public List<Person> getStudents() {
        return students;
    }

    public void setStudents(List<Person> students) {
        this.students = students;
    }

    public Map<String, List<Lesson>> getLessons() {
        return lessons;
    }

    public void setLessons(Map<String, List<Lesson>> lessons) {
        this.lessons = lessons;
    }

    public List<Mark> getMarks() {
        return marks;
    }

    public void setMarks(List<Mark> marks) {
        this.marks = marks;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public List<Presence> getPresences() {
        return presences;
    }

    public void setPresences(List<Presence> presences) {
        this.presences = presences;
    }

    public Map<Long, List<Behaviour>> getBehaviours() {
        return behaviours;
    }

    public void setBehaviours(Map<Long, List<Behaviour>> behaviours) {
        this.behaviours = behaviours;
    }

    public List<BehaviourStatus> getBehaviourSchoolList() {
        return behaviourSchoolList;
    }

    public void setBehaviourSchoolList(List<BehaviourStatus> behaviourSchoolList) {
        this.behaviourSchoolList = behaviourSchoolList;
    }

    public List<EduGroup> getGroups() {
        return groups;
    }

    public void setGroups(List<EduGroup> groups) {
        this.groups = groups;
    }

    //

    public List<String> getPresenceSchoolArray() {
        return presenceSchoolArray;
    }

    public void setPresenceSchoolArray(List<String> presenceSchoolArray) {
        this.presenceSchoolArray = presenceSchoolArray;
    }

    public List<JournalFinalMark> getFinalMarks() {
        return finalMarks;
    }

    public void setFinalMarks(List<JournalFinalMark> finalMarks) {
        this.finalMarks = finalMarks;
    }

    public Long getSubjectGroupId() {
        return subjectGroupId;
    }

    public void setSubjectGroupId(Long subjectGroupId) {
        this.subjectGroupId = subjectGroupId;
    }

    public long getPeriodId() {
        return periodId;
    }

    public void setPeriodId(long periodId) {
        this.periodId = periodId;
    }
    //endregion
}