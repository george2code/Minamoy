package core.model;

public enum TaskStatus {
    New(1),
    Working(2),
    Discuss(3),
    Reopened(4),
    Completed(5),
    Closed(6),
    Cancelled(7);

    TaskStatus(int i) {
        this.type = i;
    }

    private int type;

    public int getNumericType() {
        return type;
    }

//    private String stringValue;
//    private int intValue;
//    private Gender(String toString, int value) {
//        stringValue = toString;
//        intValue = value;
//    }
//
//    @Override
//    public String toString() {
//        return stringValue;
//    }
}