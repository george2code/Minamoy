package core.model;

import com.orm.SugarRecord;
import java.util.List;
import core.model.journal.PresenceStatus;
import core.model.journal.WorkType;


public class Authorization  extends SugarRecord<Authorization> {

    private String accessToken;
    private String refreshToken;
    private long personId;
    private String userFullName;
    //todo: add - schools
    // {"id":1000000009161,"name":"בית ספר המאה ה-21"}
    private List<PresenceStatus> presences;
    private List<WorkType> workTypes;
    //todo: add - editingProhibitoinCauses

    //region Property
    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public long getPersonId() {
        return personId;
    }

    public void setPersonId(long personId) {
        this.personId = personId;
    }

    public String getUserFullName() {
        return userFullName;
    }

    public void setUserFullName(String userFullName) {
        this.userFullName = userFullName;
    }

    public List<PresenceStatus> getPresences() {
        return presences;
    }

    public void setPresences(List<PresenceStatus> presences) {
        this.presences = presences;
    }

    public List<WorkType> getWorkTypes() {
        return workTypes;
    }

    public void setWorkTypes(List<WorkType> workTypes) {
        this.workTypes = workTypes;
    }
    //endregion
}