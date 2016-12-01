package core.model;

import java.util.Date;
import core.IDialogItems;

public class ReportingPeriod implements IDialogItems {
    private long id;
    private Date start;
    private Date finish;
    private int Number;
    private String type;
    private String name;
    private long year;

    //region Properties
    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
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

    public long getNumber() {
        return Number;
    }
    public void setNumber(int number) {
        Number = number;
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

    public long getYear() {
        return year;
    }
    public void setYear(long year) {
        this.year = year;
    }
    //endregion

    @Override
    public String getItemName() {
        return getName();
    }

    @Override
    public long getItemValue() {
        return getId();
    }
}