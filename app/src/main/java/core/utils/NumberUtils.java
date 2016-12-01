package core.utils;

import android.content.Context;
import android.util.DisplayMetrics;

import java.util.Random;

public class NumberUtils {
    public static int GetRandomNumber(int min, int max) {
        Random random = new Random();
        return random.nextInt(max - min + 1) + min;
    }

    public static int dpToPx(Context context, int dp) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
    }

    public static int dpToPixels(Context context, int dp) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
//        float dp = 20f;
        float fpixels = metrics.density * dp;
        return (int) (fpixels + 0.5f);
    }

    public static int pxToDp(Context context, int px) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int dp = Math.round(px / (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return dp;
    }
}