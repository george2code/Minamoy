package il.co.yomanim.mobileyomanim.params;

public class GroupTaskParams {
    public String token;
    public long personId;
    public long schoolId;

    public GroupTaskParams(String token, long personId, long schoolId) {
        this.token = token;
        this.personId = personId;
        this.schoolId = schoolId;
    }
}