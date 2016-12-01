package core.helpers.url;

import android.content.Context;
import java.text.MessageFormat;


public class UrlLessonHelper
{
    public static String getLessonsByEduGroupAndPeriod(Context context, String accessToken,
                                                       long eduGroupId, String from, String to) {
        return UrlHelper.getBaseUrl(context, accessToken,
                MessageFormat.format("v1/edu-groups/{0}/lessons/{1}/{2}",
                        String.valueOf(eduGroupId), from, to));
    }

    public static String getLessonsByPersonAndPeriod(Context context, String accessToken,
                                                       long personId, String from, String to) {
        return UrlHelper.getBaseUrl(context, accessToken,
                MessageFormat.format("v1/person/{0}/lessons/{1}/{2}",
                        String.valueOf(personId), from, to));
    }



    public static String getLessonsByEduGroupAndSubjectAndPeriod(Context context, String accessToken,
                                         long eduGroupId, long subjectId, String from, String to) {
        return UrlHelper.getBaseUrl(context, accessToken,
                MessageFormat.format("v1/edu-groups/{0}/subjects/{1}/lessons/{2}/{3}",
                        String.valueOf(eduGroupId),
                        String.valueOf(subjectId),
                        from, to));
    }

    //NOTE: for Homeworks we need class with works and tasks included
//    public static String getLessonsWithTasksByEduGroupAndPeriod(Context context, String accessToken,
//                                                         long eduGroupId, String from, String to) {
//        return UrlHelper.getBaseUrl(context, accessToken,
//                MessageFormat.format("v1/edu-groups/{0}/lessons-with-tasks/{1}/{2}",
//                        String.valueOf(eduGroupId),
//                        from,
//                        to));
//    }
//
//    public static String getLessonsWithTasksByEduGroupAndPersonAndPeriod(Context context, String accessToken,
//                                            long eduGroupId, long personId, String from, String to) {
//        return UrlHelper.getBaseUrl(context, accessToken,
//                MessageFormat.format("v1/edu-groups/{0}/person/{1}/lessons-with-tasks/{2}/{3}",
//                        String.valueOf(eduGroupId),
//                        String.valueOf(personId),
//                        from,
//                        to));
//    }

    public static String getLessonsWithoutTasksGetByGroupAndPeriod(Context context, String accessToken,
                                                                long eduGroupId, String from, String to) {
        return UrlHelper.getBaseUrl(context, accessToken,
                MessageFormat.format("v1/edu-groups/{0}/lessons-without-tasks/{1}/{2}",
                        String.valueOf(eduGroupId),
                        from,
                        to));
    }


    public static String getLessonsWithoutTasksGetByGroupAndPeriodAndPerson(Context context, String accessToken,
                                                 long eduGroupId, long personId, String from, String to) {
        return UrlHelper.getBaseUrl(context, accessToken,
                MessageFormat.format("v1/edu-groups/{0}/person/{1}/lessons-without-tasks/{2}/{3}",
                        String.valueOf(eduGroupId),
                        String.valueOf(personId),
                        from,
                        to));
    }



    public static String getTasksByLessonAndWork(Context context, String accessToken, long workId) {
        return UrlHelper.getBaseUrl(context, accessToken, MessageFormat.format("v1/works/{0}/tasks", String.valueOf(workId)));
    }

    public static String getHomeworkMarks(Context context, String accessToken, long workId) {
        return UrlHelper.getBaseUrl(context, accessToken, MessageFormat.format("v1/works/{0}/marks", String.valueOf(workId)));
    }

    public static String getWorkTasksAndMarks(Context context, String accessToken, long workId) {
        return UrlHelper.getBaseUrl(context, accessToken, MessageFormat.format("v1/edu-work/{0}/tasks-with-marks", String.valueOf(workId)));
    }



    public static String getTasksByWork(Context context, String accessToken, long workId) {
        return UrlHelper.getBaseUrl(context, accessToken,
                MessageFormat.format("v1/tasks/work/{0}", String.valueOf(workId)));
    }



    public static String getTeacherHomeworksMulticlass(Context context, String accessToken, long groupId,
                                                       String from, String to, String status) {
        return UrlHelper.getBaseUrl(context, accessToken,
                MessageFormat.format("v1/edu-group/{0}/homeworks/{1}/{2}/{3}",
                        String.valueOf(groupId), from, to, status));
    }

    public static String getTeacherHomeworksClassical(Context context, String accessToken, long gradeId,
                                                      long subjectId, long personId, String from, String to, String status) {
        return UrlHelper.getBaseUrl(context, accessToken,
                MessageFormat.format("v1/edu-group/{0}/subject/{1}/teacher/{2}/homeworks/{3}/{4}/{5}",
                        String.valueOf(gradeId), String.valueOf(subjectId), String.valueOf(personId), from, to, status));
    }

    public static String getStudentHomeworks(Context context, String accessToken, long personId, String from, String to) {
        return UrlHelper.getBaseUrl(context, accessToken,
                MessageFormat.format("v1/person/{0}/lessons/{1}/{2}", String.valueOf(personId), from, to));
    }
}