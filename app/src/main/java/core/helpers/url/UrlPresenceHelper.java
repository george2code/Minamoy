package core.helpers.url;

import android.content.Context;
import java.text.MessageFormat;

public class UrlPresenceHelper {
    public static String getPresenceStatuses(Context context, String accessToken) {
        return UrlHelper.getBaseUrl(context, accessToken, "v1/lesson-log-entries/statuses");
    }

    public static String getPresenceByGroupAndPeriod(Context context, String accessToken, long groupId, String from, String to) {
        return UrlHelper.getBaseUrl(context, accessToken,
                MessageFormat.format("v1/lesson-log-entries/groups/{0}/subjects/{1}/{2}/{3}",
                        String.valueOf(groupId),
                        0,
                        from,
                        to));
    }

    public static String getPresenceBySchoolPersonAndPeriod(Context context, String token, long schoolId, long personId, String from, String to) {
        return UrlHelper.getBaseUrl(context, token,
                MessageFormat.format("v1/lesson-log-entries/schools/{0}/persons/{1}/{2}/{3}",
                        String.valueOf(schoolId),
                        String.valueOf(personId),
                        from,
                        to));
    }
}