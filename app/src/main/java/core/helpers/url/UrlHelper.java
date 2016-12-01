package core.helpers.url;

import android.content.Context;
import java.text.MessageFormat;
import il.co.yomanim.mobileyomanim.R;

public class UrlHelper {

    //region base url

    public static String getBaseUrl(Context context, String accessToken, String apiUrl) {
        return MessageFormat.format("{0}://{1}/{2}?access_token={3}",
                context.getResources().getString(R.string.protocol),
                context.getResources().getString(R.string.host),
                apiUrl,
                accessToken);
    }

    public static String getBaseParamsUrl(Context context, String accessToken, String apiUrl) {
        return MessageFormat.format("{0}://{1}/{2}&access_token={3}",
                context.getResources().getString(R.string.protocol),
                context.getResources().getString(R.string.host),
                apiUrl,
                accessToken);
    }

    //endregion

    //region User url


    //endregion

    //region School url

    public static String getSchoolMembershipsUrl(Context context, String accessToken) {
        return getBaseUrl(context, accessToken, "v1/users/me/school-memberships");
    }

    public static  String getUserSchoolListUrl(Context context, String accessToken) {
        return getBaseUrl(context, accessToken, "v1/users/me/schools");
    }

    public static String getSchoolListByUserIdUrl(Context context, String accessToken, long userId) {
        return getBaseUrl(context, accessToken, MessageFormat.format("v1/users/{0}/schools",
                String.valueOf(userId).replace(",", "")));
    }

    public static String getSchoolListUrl(Context context, String accessToken) {
        return MessageFormat.format("{0}://{1}?access_token=={2}",
                context.getResources().getString(R.string.protocol),
                context.getResources().getString(R.string.host),
                accessToken);
    }

    public static String getSchoolByIdUrl(Context context, long schoolId, String accessToken) {
//        v1/schools/{school}
        return "";
    }

    //endregion

    //region EduGroup url

    public static String getEduGroups(Context context, String accessToken, long personId, long schoolId) {
        return getBaseParamsUrl(context, accessToken,
                MessageFormat.format("v1/edu-groups?person={0}&school={1}",
                        String.valueOf(personId).replace(",", ""),
                        String.valueOf(schoolId).replace(",", "")
                ));
    }

    public static String getEduGroups(Context context, String accessToken, long personId) {
        return getBaseParamsUrl(context, accessToken,
                MessageFormat.format("v1/edu-groups?person={0}", String.valueOf(personId).replace(",", "")));
    }

    //students
    //todo: to remove
    public static String getEduGroupStudents(Context context, String accessToken, long eduGroupId) {
        return getBaseParamsUrl(context, accessToken,
                MessageFormat.format("v1/persons?eduGroup={0}",
                        String.valueOf(eduGroupId).replace(",", "")));
    }

    public static String getStudents(Context context, String accessToken, long eduGroupId) {
        return getBaseUrl(context, accessToken, MessageFormat.format("v1/edu-groups/{0}/students", String.valueOf(eduGroupId).replace(",", "")));
    }

    //periods
    //todo: remove
    public static String getEduGroupReportingPeriods(Context context, String accessToken, long eduGroupId) {
        return getBaseUrl(context, accessToken, MessageFormat.format("v1/edu-groups/{0}/reporting-periods",
                String.valueOf(eduGroupId).replace(",", "")));
    }

    public static String getReportingPeriods(Context context, String accessToken, long schoolId) {
        return getBaseUrl(context, accessToken, MessageFormat.format("v1/edu-school/{0}/reporting-periods",
                String.valueOf(schoolId).replace(",", "")));
    }



    //set mark
    public static String getSetMark(Context context, String accessToken) {
        return getBaseUrl(context, accessToken, "v1/marks/complex");
    }


    //homework
    public static String changeHomeworkTaskStatus(Context context, String accessToken) {
        return getBaseUrl(context, accessToken, "v1/tasks");
    }
}