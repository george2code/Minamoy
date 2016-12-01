package core.helpers;

import android.content.Context;
import android.util.Log;
import java.util.List;
import core.handlers.HttpManager;
import core.handlers.SessionManager;
import core.handlers.parser.EduGroupJSONParser;
import core.model.EduGroup;
import core.model.ReportingPeriod;


public class FilterHelper {
    //region Fields
    private String TAG = "FilterHelper";
    private Context context;
    private String token;
    private SessionManager sessionManager;
    //endregion

    public FilterHelper(Context context, String token) {
        this.context = context;
        this.token = token;
        sessionManager = new SessionManager(context);
    }


    public List<ReportingPeriod> getReportingPeriods(long schoolId) {
        Log.e(TAG, "getReportingPeriods");
        List<ReportingPeriod> reportingPeriods = sessionManager.getReportingPeriodList();
        if (reportingPeriods == null) {
            String contentPeriods = HttpManager.getReportingPeriods(context, token, schoolId);
            if (contentPeriods != null) {
                reportingPeriods = EduGroupJSONParser.parseReportingPeriods(contentPeriods);
                sessionManager.setReportingPeriods(reportingPeriods);
            }
        }
        return reportingPeriods;
    }

    //Multiclass
    public List<EduGroup> getEduSubjectGroups(long personId, long schoolId) {
        // persons/{person}/schools/{school}/edu-subjectgroups
        Log.e(TAG, "getEduSubjectGroups");
        List<EduGroup> subjectGroups = sessionManager.getSubjectGroupList();
        if (subjectGroups == null) {
            Log.e(TAG, schoolId + "");
            String contentSubjectGroups = HttpManager.getSubjectGroupsByStudentAndSchool(context, token, personId, schoolId);
            if (contentSubjectGroups != null) {
                subjectGroups = EduGroupJSONParser.parseEduGroupList(contentSubjectGroups, false);
                sessionManager.updateSubjectGroups(subjectGroups);
            }
        }
        return subjectGroups;
    }

    //Grade, Classical
    public List<EduGroup> getGrades(long personId, long schoolId, boolean isTeacher) {
        Log.e(TAG, "getGrades");
        List<EduGroup> grades = sessionManager.getGradeList();
        if (grades == null) {
            String contentGrades = (isTeacher) ?
                    HttpManager.getGradesByTeacherAndSchool(context, token, personId, schoolId) :
                    HttpManager.getGradesByStudent(context, token, personId, schoolId);
            if (contentGrades != null) {
                grades = EduGroupJSONParser.parseEduGroupList(contentGrades, false);
                sessionManager.updateGrades(grades);
            }
        }
        return grades;
    }
}