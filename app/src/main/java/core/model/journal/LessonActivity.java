package core.model.journal;

import java.util.List;

public class LessonActivity {

    private Long work;
    private Long lesson;
    private long person;

    private Mark mark;
    private Presence lessonLogEntry;
    private List<Behaviour> behaviors;


    public Long getWorkId() {
        return work;
    }

    public void setWorkId(Long workId) {
        this.work = workId;
    }

    public Long getLesson() {
        return lesson;
    }

    public void setLesson(Long lesson) {
        this.lesson = lesson;
    }

    public long getPerson() {
        return person;
    }

    public void setPerson(long person) {
        this.person = person;
    }

    public Mark getResource() {
        return mark;
    }

    public void setResource(Mark resource) {
        this.mark = resource;
    }

    public Presence getLessonLogEntry() {
        return lessonLogEntry;
    }

    public void setLessonLogEntry(Presence lessonLogEntry) {
        this.lessonLogEntry = lessonLogEntry;
    }

    public List<Behaviour> getBehaviors() {
        return behaviors;
    }

    public void setBehaviors(List<Behaviour> behaviors) {
        this.behaviors = behaviors;
    }
}