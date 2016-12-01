package core.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

public class ImageUtils
{
    public static Bitmap StringBase64ToBitmap(String image) {
        try {
            byte[] imageAsBytes = Base64.decode(image.getBytes(), Base64.URL_SAFE);
            return BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
        } catch(Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}