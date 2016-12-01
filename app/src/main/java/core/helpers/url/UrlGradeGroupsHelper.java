package core.helpers.url;

import android.content.Context;
import java.text.MessageFormat;


public class UrlGradeGroupsHelper
{
    //Classical Grades
    public static String getGradesByTeacherAndSchool(Context context, String accessToken, long personId, long schoolId) {
        return UrlHelper.getBaseUrl(context, accessToken,
                MessageFormat.format("v1/teacher/{0}/schools/{1}/edu-groups",
                        String.valueOf(personId).replace(",", ""),
                        String.valueOf(schoolId).replace(",", "")));
    }

    public static String getGradesByStudent(Context context, String accessToken, long personId, long schoolId) {
        return UrlHelper.getBaseUrl(context, accessToken,
                MessageFormat.format("v1/persons/{0}/schools/{1}/edu-groups",
                            String.valueOf(personId).replace(",", ""),
                            String.valueOf(schoolId).replace(",", "")
                        ));
    }


    //Classical Subjects
    public static String getSubjectByGradeForTeacher(Context context, String accessToken, long gradeId, long personId) {
        return UrlHelper.getBaseUrl(context, accessToken,
                MessageFormat.format("v1/edu-groups/{0}/teacher/{1}/subjects",
                        String.valueOf(gradeId).replace(",", ""),
                        String.valueOf(personId).replace(",", "")));
    }

    public static String getSubjectByGradeForStudent(Context context, String accessToken, long gradeId) {
        return UrlHelper.getBaseUrl(context, accessToken,
                MessageFormat.format("v1/edu-groups/{0}/subjects",
                        String.valueOf(gradeId).replace(",", "")));
    }


    //Multiclasks
    public static String getSubjectGroupsByStudentAndSchool(Context context, String accessToken, long personId, long schoolId) {
//        persons/{person}/schools/{school}/edu-subjectgroups
        return UrlHelper.getBaseUrl(context, accessToken,
                MessageFormat.format("v1/persons/{0}/schools/{1}/edu-subjectgroups",
                        String.valueOf(personId).replace(",", ""),
                        String.valueOf(schoolId).replace(",", "")));
    }
}