package core.model.journal;

import android.util.Log;

import java.util.List;
import core.model.Task;


public class Work {
    private long id;                  // идентификатор работы на уроке
    private String type;                // тип работы
    private String markType;            // система оценивания работы, например Mark5
    private int markCount;              // количество оценок за работу
    private long lesson;              // идентификатор урока
    private Boolean displayInJournal;   // отображать в журнале
    private String status;              // статус работы, например Sent
    private long eduGroup;            // идентификатор класса
    private List<Task> tasks;           // индивидуальные задания
    private String text;                // описание работы
    private int periodNumber;        // номер отчетного периода
    private String periodType;          // тип отчетного периода

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

    public String getMarkType() {
        return markType;
    }

    public void setMarkType(String markType) {
        this.markType = markType;
    }

    public int getMarkCount() {
        return markCount;
    }

    public void setMarkCount(int markCount) {
        this.markCount = markCount;
    }

    public long getLesson() {
        return lesson;
    }

    public void setLesson(long lesson) {
        this.lesson = lesson;
    }

    public Boolean getDisplayInJournal() {
        return displayInJournal;
    }

    public void setDisplayInJournal(Boolean displayInJournal) {
        this.displayInJournal = displayInJournal;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getEduGroup() {
        return eduGroup;
    }

    public void setEduGroup(long eduGroup) {
        this.eduGroup = eduGroup;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getPeriodNumber() {
        return periodNumber;
    }

    public void setPeriodNumber(int periodNumber) {
        this.periodNumber = periodNumber;
    }

    public String getPeriodType() {
        return periodType;
    }

    public void setPeriodType(String periodType) {
        this.periodType = periodType;
    }

    //endregion

    public String getJournalWorkType() {
        String result = "N/A";
        switch (this.type) {
            case "LessonCopyBook":
                result = "LCB";
                break;
            case "LessonPractical":
                result = "LP";
                break;
            case "LessonControlWork":
                result = "LCW";
            break;
           case "LessonComposition":
               result = "LC";
           break;
            case "LessonExpositionComposition":
                result = "LEC";
            break;
           case "LessonEssay":
               result = "LE";
           break;
           case "LessonHomework":
               result = "LH";
           break;
           case "LessonFinalControlDictation":
               result = "LFCD";
           break;
           case "LessonReport":
               result = "LR";
           break;
           case "LessonWork":
               result = "LC";
           break;
        }
        return result;
    }
}