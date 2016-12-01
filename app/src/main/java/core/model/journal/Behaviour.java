package core.model.journal;

import java.util.List;

public class Behaviour {
    private long person;
    private long lesson;
    private long behaviour;
    private String comment;
    private boolean justified;
    private String behaviourName;

    private List<Long> behaviours;
    private LessonLogEntry lessonlogentry;


    public long getPersonId() {
        return person;
    }

    public void setPersonId(long personId) {
        this.person = personId;
    }

    public long getLessonId() {
        return lesson;
    }

    public void setLessonId(long lessonId) {
        this.lesson = lessonId;
    }

    public long getBehaviourId() {
        return behaviour;
    }

    public void setBehaviourId(long behaviourId) {
        this.behaviour = behaviourId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public boolean isJustified() {
        return justified;
    }

    public void setJustified(boolean justified) {
        this.justified = justified;
    }

    public String getBehaviourName() {
        return behaviourName;
    }

    public void setBehaviourName(String behaviourName) {
        this.behaviourName = behaviourName;
    }

    public List<Long> getBehaviours() {
        return behaviours;
    }

    public void setBehaviours(List<Long> behaviours) {
        this.behaviours = behaviours;
    }

    public LessonLogEntry getLessonlogentry() {
        return lessonlogentry;
    }

    public void setLessonlogentry(LessonLogEntry lessonlogentry) {
        this.lessonlogentry = lessonlogentry;
    }
}