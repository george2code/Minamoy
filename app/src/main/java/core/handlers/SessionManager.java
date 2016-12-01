package core.handlers;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.util.HashMap;
import java.util.List;

import core.helpers.ConstantsHelper;
import core.model.EduGroup;
import core.model.Person;
import core.model.ReportingPeriod;
import core.model.SchoolMembership;
import core.model.TimetableItem;
import core.model.journal.BehaviourStatus;
import core.model.journal.PresenceStatus;
import core.model.journal.TeacherComment;
import core.model.journal.WorkType;
import il.co.yomanim.mobileyomanim.activities.LoginActivity;


public class SessionManager {

    // Shared Preferences
    public SharedPreferences pref;

    // Editor for Shared preferences
    Editor editor;

    // Context
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // All Shared Preferences Keys
    private static final String IS_LOGIN = "IsLoggedIn";

    // Mode
    private static final String IS_ONLINE_MODE = "IsOnlineMode";

    // User name (make variable public to access from outside)
    public static final String KEY_USER_ID = "userId";
    public static final String KEY_USER_NAME = "userName";


    public static final String KEY_FILTER_GROUP_INDEX = "defGroup";
    public static final String KEY_FILTER_PERIOD_INDEX = "defPeriod";
    public static final String KEY_FILTER_WEEK_INDEX = "defWeek";


    // Email address (make variable public to access from outside)
    public static final String KEY_TOKEN = "token";

    public static final String KEY_SCHOOL_ID = "schoolId";
    public static final String KEY_SCHOOL_NAME = "schoolName";
    public static final String KEY_SCHOOL_SYSTEM = "schoolSystem";
    public static final String KEY_PROFILE_ID = "profileId";
    public static final String KEY_CHILD_PROFILE_ID = "childProfileId";
    public static final String KEY_ROLE = "role";
    public static final String KEY_AVATAR = "avatar";

//    public static final String KEY_SCHOOL_COUNT = "schoolCount";
//    public static final String KEY_CHILD_COUNT = "childCount";

    public static final String KEY_LANGUAGE = "Language";


    public void setPersonId(long profileId) {
        editor.putLong(KEY_PROFILE_ID, profileId);
        editor.commit();
    }
    public void setChildPersonId(long profileId) {
        editor.putLong(KEY_CHILD_PROFILE_ID, profileId);
        editor.commit();
    }
    public void setRole(String role) {
        editor.putString(KEY_ROLE, role);
        editor.commit();
    }
    public void setSchoolId(long schoolId) {
        editor.putLong(KEY_SCHOOL_ID, schoolId);
        editor.commit();
    }
    public void setSchoolName(String name) {
        editor.putString(KEY_SCHOOL_NAME, name);
        editor.commit();
    }

    public void setSchoolSystem(boolean isClassical) {
        editor.putBoolean(KEY_SCHOOL_SYSTEM, isClassical);
        editor.commit();
    }

    public boolean isSchoolClassicalSystem() {
        return pref.getBoolean(KEY_SCHOOL_SYSTEM, true);
    }


    public void setUserName(String name) {
        editor.putString(KEY_USER_NAME, name);
        editor.commit();
    }
    public void setAvatar(String imagePath) {
        editor.putString(KEY_AVATAR, imagePath);
        editor.commit();
    }


    // Constructor
    public SessionManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(ConstantsHelper.PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void createLoginSession(long userId, String token){
        // Storing login value as TRUE
        editor.putBoolean(IS_LOGIN, true);

        // Storing name in pref
        editor.putLong(KEY_USER_ID, userId);

        // Storing email in pref
        editor.putString(KEY_TOKEN, token);

        // commit changes
        editor.commit();
    }

//    Check login method wil check user login status
//    If false it will redirect user to login page
//    Else won't do anything
    public void checkLogin(){
        // Check login status
        if(!this.isLoggedIn()){
            // user is not logged in redirect him to Login Activity
            Intent i = new Intent(_context, LoginActivity.class);
            // Closing all the Activities
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            // Add new Flag to start new Activity
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            // Staring Login Activity
            _context.startActivity(i);
        }
    }


    public HashMap<String, String> getUserDetails(){
        HashMap<String, String> user = new HashMap<String, String>();
        // user id
        user.put(KEY_USER_ID, String.valueOf(pref.getLong(KEY_USER_ID, 0)));
        // user token
        user.put(KEY_USER_NAME, pref.getString(KEY_USER_NAME, null));
        // user avatar
        user.put(KEY_AVATAR, pref.getString(KEY_AVATAR, null));
        // user token
        user.put(KEY_TOKEN, pref.getString(KEY_TOKEN, null));
        // school id
        user.put(KEY_SCHOOL_ID, String.valueOf(pref.getLong(KEY_SCHOOL_ID, 0)));
        // school name
        user.put(KEY_SCHOOL_NAME, pref.getString(KEY_SCHOOL_NAME, null));
        // school system
        user.put(KEY_SCHOOL_SYSTEM, String.valueOf(pref.getBoolean(KEY_SCHOOL_SYSTEM, true)));
        // profile id
        user.put(KEY_PROFILE_ID, String.valueOf(pref.getLong(KEY_PROFILE_ID, 0)));
        // profile id
        user.put(KEY_CHILD_PROFILE_ID, String.valueOf(pref.getLong(KEY_CHILD_PROFILE_ID, 0)));
        // rolle
        user.put(KEY_ROLE, pref.getString(KEY_ROLE, null));

//        user.put(KEY_SCHOOL_COUNT, String.valueOf(pref.getInt(KEY_SCHOOL_COUNT, 0)));
//        user.put(KEY_CHILD_COUNT, String.valueOf(pref.getInt(KEY_CHILD_COUNT, 0)));

        // return user
        return user;
    }


    // Clear session details
    public void logoutUser() {
        String saveLocale = pref.getString(KEY_LANGUAGE, "");
        String login = pref.getString("login", "");
        String password = pref.getString("password", "");
        Boolean saveInfo = pref.getBoolean("saveInfo", false);


        // Clearing all data from Shared Preferences
        editor.clear();
        if (!saveLocale.equals("")) {
            editor.putString(KEY_LANGUAGE, saveLocale);
        }


        editor.putBoolean("saveInfo", saveInfo);
        editor.putString("login", login);
        editor.putString("password", password);

        editor.commit();


        // After logout redirect user to Loing Activity
        Intent i = new Intent(_context, LoginActivity.class);
        // Closing all the Activities
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // Add new Flag to start new Activity
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        // Staring Login Activity
        _context.startActivity(i);
    }


    public void clearSession() {
        editor.clear();
        editor.commit();
    }


    // Quick check for login
    // Get Login State
    public boolean isLoggedIn(){
        return pref.getBoolean(IS_LOGIN, false);
    }

    public boolean isOnlineMode(){
        return pref.getBoolean(IS_ONLINE_MODE, true);
    }


    ////////////////////////////////////////////
    private Gson getGson() {
        GsonBuilder gsonb = new GsonBuilder();
        return gsonb.create();
    }

    public void updateChild(List<Person> childList) {
        editor.putString("yom_child", getGson().toJson(childList));
        editor.apply();
    }
    public List<Person> getChildList() {
        String profilesContent = pref.getString("yom_child", null);
        if (profilesContent != null) {
            return getGson().fromJson(profilesContent, new TypeToken<List<Person>>(){}.getType());
        }
        return null;
    }
    public int getChildCount() {
        int result = 0;
        String profilesContent = pref.getString("yom_child", null);
        if (profilesContent != null) {
            result = ((List<Person>)getGson().fromJson(profilesContent, new TypeToken<List<Person>>(){}.getType())).size();
        }
        return result;
    }



    public void updateSchools(List<SchoolMembership> schoolList) {
        editor.putString("yom_school", getGson().toJson(schoolList));
        editor.apply();
    }
    public List<SchoolMembership> getSchoolList() {
        String profilesContent = pref.getString("yom_school", null);
        if (profilesContent != null) {
            return getGson().fromJson(profilesContent, new TypeToken<List<SchoolMembership>>(){}.getType());
        }
        return null;
    }
    public int getSchoolCount() {
        int result = 0;
        String profilesContent = pref.getString("yom_school", null);
        if (profilesContent != null) {
            result = ((List<Person>)getGson().fromJson(profilesContent, new TypeToken<List<SchoolMembership>>(){}.getType())).size();
        }
        return result;
    }


    ////////////////////////////////////////////////////////////////////multiclass subject groups
    public void updateSubjectGroups(List<EduGroup> subjectGroupList) {
        editor.putString("yom_subjgroup", getGson().toJson(subjectGroupList));
        editor.apply();
    }
    public List<EduGroup> getSubjectGroupList() {
        String profilesContent = pref.getString("yom_subjgroup", null);
        if (profilesContent != null) {
            return getGson().fromJson(profilesContent, new TypeToken<List<EduGroup>>(){}.getType());
        }
        return null;
    }


    ////////////////////////////////////////////////////////////////////GRADES CLASSICAL
    public void updateGrades(List<EduGroup> gradeList) {
        editor.putString("yom_grade", getGson().toJson(gradeList));
        editor.apply();
    }
    public List<EduGroup> getGradeList() {
        String gradeContent = pref.getString("yom_grade", null);
        if (gradeContent != null) {
            return getGson().fromJson(gradeContent, new TypeToken<List<EduGroup>>(){}.getType());
        }
        return null;
    }




    // Presence Status ////////////////////////////////////////////////////////////////////
    public void updatePresenceStatuses(List<PresenceStatus> list) {
        editor.putString("yom_presence_status", getGson().toJson(list));
        editor.apply();
    }
    public List<PresenceStatus> getPresenceStatuseList() {
        String content = pref.getString("yom_presence_status", null);
        if (content != null) {
            return getGson().fromJson(content, new TypeToken<List<PresenceStatus>>(){}.getType());
        }
        return null;
    }

    // Work Type ////////////////////////////////////////////////////////////////////
    public void updateWorkTypes(List<WorkType> list) {
        editor.putString("yom_work_type", getGson().toJson(list));
        editor.apply();
    }
    public List<WorkType> getWorkTypeList() {
        String content = pref.getString("yom_work_type", null);
        if (content != null) {
            return getGson().fromJson(content, new TypeToken<List<WorkType>>(){}.getType());
        }
        return null;
    }




    public void updateTeacherComments(List<TeacherComment> list) {
        editor.putString("yom_teacher_comments", getGson().toJson(list));
        editor.apply();
    }
    public List<TeacherComment> getTeacherCommentList() {
        String content = pref.getString("yom_teacher_comments", null);
        if (content != null) {
            return getGson().fromJson(content, new TypeToken<List<TeacherComment>>(){}.getType());
        }
        return null;
    }




    //region School depended methods
    public void setReportingPeriods(List<ReportingPeriod> reportingPeriodList) {
        editor.putString("yom_period", getGson().toJson(reportingPeriodList));
        editor.apply();
    }
    public List<ReportingPeriod> getReportingPeriodList() {
        String profilesContent = pref.getString("yom_period", null);
        if (profilesContent != null) {
            return getGson().fromJson(profilesContent, new TypeToken<List<ReportingPeriod>>(){}.getType());
        }
        return null;
    }

    public void setSchoolTimetable(List<TimetableItem> list) {
        editor.putString("yom_timetable", getGson().toJson(list));
        editor.apply();
    }
    public List<TimetableItem> getSchoolTimetableList() {
        String content = pref.getString("yom_timetable", null);
        if (content != null) {
            return getGson().fromJson(content, new TypeToken<List<TimetableItem>>(){}.getType());
        }
        return null;
    }

    public void setBehaviourStatus(List<BehaviourStatus> list) {
        editor.putString("yom_behaviour", getGson().toJson(list));
        editor.apply();
    }
    public List<BehaviourStatus> getBehaviourStatusList() {
        String content = pref.getString("yom_behaviour", null);
        if (content != null) {
            return getGson().fromJson(content, new TypeToken<List<BehaviourStatus>>(){}.getType());
        }
        return null;
    }

    public void clearSchoolMethods() {
        editor.remove("yom_period");
        editor.remove("yom_timetable");
        editor.remove("yom_behaviour");
        editor.apply();
    }
    //endregion



//    public void updateFilterReportingPeriod(ReportingPeriod reportingPeriod) {
//        editor.putString(KEY_DEFAULT_PERIOD, getGson().toJson(reportingPeriod));
//        editor.apply();
//    }
//    public ReportingPeriod getFilterReportingPeriod() {
//        String profilesContent = pref.getString(KEY_DEFAULT_PERIOD, null);
//        if (profilesContent != null) {
//            return getGson().fromJson(profilesContent, new TypeToken<ReportingPeriod>(){}.getType());
//        }
//        return null;
//    }
//    public void updateFilterGroupIdAndName(long groupId, String groupName) {
//        editor.putLong(KEY_DEFAULT_GROUP_ID, groupId);
//        editor.putString(KEY_DEFAULT_GROUP_NAME, groupName);
//        editor.apply();
//    }
//    public long getFilterGroupId() {
//        return pref.getLong(KEY_DEFAULT_GROUP_ID, 0);
//    }
//    public String getFilterGroupName() {
//        return pref.getString(KEY_DEFAULT_GROUP_NAME, null);
//    }


    public void updateFilterSelectedIndex(String key, int value) {
        editor.putInt(key, value);
        editor.apply();
    }
    public int getFilterSelectedIndex(String key) {
        return pref.getInt(key, 0);
    }
}