package core.helpers.url;

import android.content.Context;
import java.text.MessageFormat;


public class UrlTimetableHelper {

//    public static String getTeacherTimetable(Context context, String accessToken, long schoolId, long personId, String date) {
//        return UrlHelper.getBaseUrl(context, accessToken,
//                MessageFormat.format("v1/schools/{0}/teacher/{1}/date/{2}/timetable",
//                        String.valueOf(schoolId).replace(",", ""),
//                        String.valueOf(personId).replace(",", ""),
//                        date));
//    }
//
//    public static String getStudentTimetable(Context context, String accessToken, long schoolId, long personId, String date) {
//        return UrlHelper.getBaseUrl(context, accessToken,
//                MessageFormat.format("v1/schools/{0}/student/{1}/date/{2}/timetable",
//                        String.valueOf(schoolId).replace(",", ""),
//                        String.valueOf(personId).replace(",", ""),
//                        date));
//    }

    public static String getSchoolTimetable(Context context, String accessToken, long schoolId) {
        return UrlHelper.getBaseUrl(context, accessToken,
                MessageFormat.format("v1/schools/{0}/timetables",
                        String.valueOf(schoolId).replace(",", "")));
    }

    public static String getTimetableStudentLessons(Context context, String accessToken,
                                                    long personId, String from, String to) {
        return UrlHelper.getBaseUrl(context, accessToken,
                MessageFormat.format("v1/person/{0}/lessons/{1}/{2}",
                        String.valueOf(personId).replace(",", ""),
                        from,
                        to));
    }

    public static String getTimetableTeacherLessons(Context context, String accessToken,
                                            long schoolId, long personId, String from, String to) {
        return UrlHelper.getBaseUrl(context, accessToken,
                MessageFormat.format("v1/schools/{0}/person/{1}/lessons/{2}/{3}",
                        String.valueOf(schoolId).replace(",", ""),
                        String.valueOf(personId).replace(",", ""),
                        from,
                        to));
    }
}