package core.utils;

import android.content.res.AssetManager;
import android.graphics.Typeface;

public class FontUtils {

    public static Typeface getRobotoMedium(AssetManager assetManager) {
        return Typeface.createFromAsset(assetManager, "Roboto-Medium.ttf");
    }

    public static Typeface getRobotoLight(AssetManager assetManager) {
        return Typeface.createFromAsset(assetManager, "Roboto-Light.ttf");
    }
}