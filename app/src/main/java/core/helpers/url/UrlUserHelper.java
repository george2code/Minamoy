package core.helpers.url;

import android.content.Context;
import java.text.MessageFormat;
import il.co.yomanim.mobileyomanim.R;


public class UrlUserHelper
{
    public static String loginUrl(Context context, String username, String password) {
        return MessageFormat.format("{0}://{1}?scope={2}&client_id={3}&username={4}&password={5}",
                context.getResources().getString(R.string.protocol),
                context.getResources().getString(R.string.host_login),
                context.getResources().getString(R.string.scope),
                context.getResources().getString(R.string.client_id),
                username,
                password);
    }

    public static String loginUrl(Context context) {
        return MessageFormat.format("{0}://{1}/mobileJournal/authorizations",
                context.getResources().getString(R.string.protocol),
                context.getResources().getString(R.string.host));
    }

    public static String getUserSchoolMemberships(Context context, String accessToken) {
        return UrlHelper.getBaseUrl(context, accessToken, "v1/users/me/school-memberships");
    }

    public static String getUsersMe(Context context, String accessToken) {
        return UrlHelper.getBaseUrl(context, accessToken, "/v1/users/me");
    }


    //Children
    public static String getUserChildListUrl(Context context, String accessToken, long personId) {
        return UrlHelper.getBaseUrl(context, accessToken,
                MessageFormat.format("v1/person/{0}/children", String.valueOf(personId).replace(",", "")));
    }
}