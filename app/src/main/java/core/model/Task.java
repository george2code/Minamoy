package core.model;

import java.util.Date;


public class Task {
    private long id;          // идентификатор домашнего задания
    private long person;      // идентификатор персоны которая выполняет домашнее задание
    private String personName;
    private long work;        // идентификатор работы
    private String status;      // статус выполнения домашнего задания, например Sent
    private Date targetDate;    // дата проверки домашнего задания

    //region Properties

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getTargetDate() {
        return targetDate;
    }

    public void setTargetDate(Date targetDate) {
        this.targetDate = targetDate;
    }

    public String getPersonName() {
        return personName;
    }

    public void setPersonName(String personName) {
        this.personName = personName;
    }

    //endregion
}