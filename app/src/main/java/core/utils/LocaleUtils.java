package core.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import java.util.Locale;
import core.helpers.ConstantsHelper;

/**
 * Language utilities
 */
public class LocaleUtils {

    public static boolean isRtl(Context context) {
        //if Hebrew or Arabic - use RTL
        String code = getCurrentLocale(context);
        return !(code.equals("en") || code.equals("ru"));
    }

    public static boolean changeLang(String lang, Context context)
    {
        if (lang.equalsIgnoreCase(""))
            lang = ConstantsHelper.LOCALIZATION_ENGLISH;

        try {
            Locale myLocale = new Locale(lang);
            saveLocale(lang, context);
            Locale.setDefault(myLocale);
            android.content.res.Configuration config = new android.content.res.Configuration();
            config.locale = myLocale;
            context.getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());
        } catch (Exception ex) {
            Log.e("LocaleUtils", ex.getMessage());
            return false;
        }

        return true;
    }

    public static void saveLocale(String lang, Context context)
    {
        String langPref = "Language";
        SharedPreferences prefs = context.getSharedPreferences(
                ConstantsHelper.PREF_NAME,
                Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(langPref, lang);
        editor.apply();
    }

    public static void loadLocale(Context context)
    {
        String langPref = "Language";
        SharedPreferences prefs = context.getSharedPreferences(
                ConstantsHelper.PREF_NAME,
                Activity.MODE_PRIVATE);
        String language = prefs.getString(langPref, "");
        changeLang(language, context);
    }

    public static String getCurrentLocale(Context context) {
        String langPref = "Language";
        SharedPreferences prefs = context.getSharedPreferences(
                ConstantsHelper.PREF_NAME,
                Activity.MODE_PRIVATE);
        String language = prefs.getString(langPref, "");

        if (language.equals("")) {
            language = ConstantsHelper.LOCALIZATION_ENGLISH;
            changeLang(language, context);
        }

        return language;
    }
}