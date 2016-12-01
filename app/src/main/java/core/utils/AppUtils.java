package core.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.widget.Toast;
import core.handlers.parser.ErrorJsonParser;
import core.model.ServerError;


public class AppUtils {
    public static boolean findErrors(final Context context, String content) {
        if (content.contains("apiUnknownError")) {
            final ServerError serverError = ErrorJsonParser.parseError(content);
            if (serverError != null && !TextUtils.isEmpty(serverError.getDescription())) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, serverError.getDescription(), Toast.LENGTH_LONG).show();
                    }
                });
            }
            return true;
        }
        return false;
    }

    public static boolean checkInvalidParams(final Context context, String content) {
        // {"type":"parameterInvalid","description":"Unsupported action"}
        if (!TextUtils.isEmpty(content) && content.contains("parameterInvalid")) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, "Unsupported action", Toast.LENGTH_SHORT).show();
                }
            });
            return true;
        }
        return false;
    }
}