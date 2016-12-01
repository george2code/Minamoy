package core.helpers.url;

import android.content.Context;
import java.text.MessageFormat;


public class UrlMarkHelper
{
    public static String getMarksByEduGroupAndPeriod(Context context, String accessToken,
                                                     long eduGroupId, String from, String to) {
        return UrlHelper.getBaseUrl(context, accessToken,
                MessageFormat.format("v1/edu-groups/{0}/marks/{1}/{2}",
                        String.valueOf(eduGroupId), from, to));
    }

    public static String getMarksByPersonSchoolAndPeriod(Context context, String token, long schoolId,
                                                         long personId, String from, String to) {
        return UrlHelper.getBaseUrl(context, token,
                MessageFormat.format("v1/persons/{1}/schools/{0}/marks/{2}/{3}",
                        String.valueOf(schoolId),
                        String.valueOf(personId),
                        from,
                        to));
    }

    public static String getMarksByEduGroupSubjectAndPeriod(Context context, String accessToken,
                                        long eduGroupId, long subjectId, String from, String to) {
        return UrlHelper.getBaseUrl(context, accessToken,
                MessageFormat.format("v1/edu-groups/{0}/subjects/{1}/marks/{2}/{3}",
                        String.valueOf(eduGroupId),
                        String.valueOf(subjectId),
                        from,
                        to));
    }


    //------------------------------------
    public static String deleteMark(Context context, String accessToken, long markId) {
        return UrlHelper.getBaseUrl(context, accessToken, MessageFormat.format("v1/marks/{0}", String.valueOf(markId)));
    }
    public static String putMark(Context context, String accessToken) {
        return UrlHelper.getBaseUrl(context, accessToken, "v1/marks");
    }
    public static String setMark(Context context, String accessToken) {
        return UrlHelper.getBaseUrl(context, accessToken, "v1/marks/process");
    }

    //new
    public static String getFinalMarksTeacherMulti(Context context, String accessToken, long subjectGroupId, long periodId) {
        return UrlHelper.getBaseUrl(context, accessToken,
                MessageFormat.format("v1/edu-groups/{0}/period/{1}/final-marks-teacher",
                        String.valueOf(subjectGroupId), String.valueOf(periodId)));

    }


    public static String setFinalMarkTeacherMulti(Context context, String accessToken, long personId, long subjectGroupId, long periodId, String markValue) {
        return UrlHelper.getBaseUrl(context, accessToken,
                MessageFormat.format("v1/final-marks/{0}/{1}/{2}/{3}",
                        String.valueOf(personId),
                        String.valueOf(subjectGroupId),
                        String.valueOf(periodId),
                        markValue
                ));
    }



    public static String getFinalMarksTeacherClassical(Context context, String accessToken, int gradeId, int subjectId, int periodId) {
        return UrlHelper.getBaseUrl(context, accessToken,
                MessageFormat.format("v1/edu-groups/{0}/subject/{1}/period/{2}/final-marks-teacher",
                        gradeId, subjectId, periodId));

    }








    public static String getTeacherCommentMarks(Context context, String accessToken, long subjectGroupId, int periodId) {
        return UrlHelper.getBaseUrl(context, accessToken,
                MessageFormat.format("v1/edu-group/{0}/period/{1}/teachercommentmarks",
                        String.valueOf(subjectGroupId), String.valueOf(periodId)));
    }

    public static String getTeacherCommentsBySchool(Context context, String accessToken, long schoolId) {
        return UrlHelper.getBaseUrl(context, accessToken,
                MessageFormat.format("v1/school/{0}/teachercomments", String.valueOf(schoolId)));
    }

}