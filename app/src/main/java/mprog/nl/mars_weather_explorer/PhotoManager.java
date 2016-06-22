package mprog.nl.mars_weather_explorer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.util.DisplayMetrics;


/**
 * Created by Nadeche
 */
public class PhotoManager {

    // TODO make static
    public Bitmap getPhoto(Context context, Bitmap photoBitmap) {

        // get the with of the device screen
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();

        int screenWidth = metrics.widthPixels;

        int photoBitmapWidth = photoBitmap.getWidth();
        int photoBitmapHeight = photoBitmap.getHeight();

        if (photoBitmapWidth > screenWidth){

            float scaleRatio = ((float) screenWidth) / photoBitmapWidth;

            // Create a matrix for the manipulation
            Matrix matrix = new Matrix();

            // Resize the bit map
            matrix.postScale(scaleRatio, scaleRatio);

            return Bitmap.createBitmap(photoBitmap, 0, 0, photoBitmapWidth, photoBitmapHeight, matrix, false);
        }
        else {
            return photoBitmap;
        }
    }
}
