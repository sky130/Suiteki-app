package ml.sky233.suiteki.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;

public class ImageUtils {


    public static Bitmap tgaToPng(byte[] tga) {
        try {
            Bitmap tgaBitmap = BitmapFactory.decodeByteArray(tga, 0, tga.length);
            Bitmap pngBitmap = Bitmap.createBitmap(tgaBitmap.getWidth(), tgaBitmap.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(pngBitmap);
            Paint paint = new Paint();
            canvas.drawBitmap(tgaBitmap, 0, 0, paint);
            return pngBitmap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
