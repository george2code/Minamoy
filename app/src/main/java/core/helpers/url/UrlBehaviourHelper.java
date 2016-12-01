package core.helpers.url;

import android.content.Context;
import java.text.MessageFormat;

public class UrlBehaviourHelper {
    public static String getBehaviourForTeacher(Context context, String accessToken, long groupId, String from, String to) {
        return UrlHelper.getBaseUrl(context, accessToken,
                MessageFormat.format("v1/behaviour/groups/{0}/{1}/{2}",
                        String.valueOf(groupId),
                        from,
                        to));
    }

    public static String getBehaviourForStudent(Context context, String accessToken, long personId, String from, String to) {
        return UrlHelper.getBaseUrl(context, accessToken,
                MessageFormat.format("v1/behaviour/persons/{0}/{1}/{2}",
                        String.valueOf(personId),
                        from,
                        to));
    }

    public static String getBehaviourStatusList(Context context, String accessToken, long schoolId) {
        return UrlHelper.getBaseUrl(context, accessToken,
                MessageFormat.format("v1/behaviour-statuses/schools/{0}",
                        String.valueOf(schoolId)));
    }
}