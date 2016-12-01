package core.model;

import core.IDialogItems;

public class Subject implements IDialogItems
{
    private long id;
    private String name;

    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getItemName() {
        return getName();
    }

    @Override
    public long getItemValue() {
        return getId();
    }
}