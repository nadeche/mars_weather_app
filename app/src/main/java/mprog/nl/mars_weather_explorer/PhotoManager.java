package mprog.nl.mars_weather_explorer;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.DisplayMetrics;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;


/**
 * Created by Nadeche
 */
public class PhotoManager {

    public static Bitmap downloadPhoto(Context context, String url){
        Bitmap photoBitMap = null;
        try {
            InputStream inputStream = new java.net.URL(url).openStream();
            photoBitMap = BitmapFactory.decodeStream(inputStream);
            photoBitMap = PhotoManager.resizePhoto(context, photoBitMap);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return photoBitMap;
    }

    public static Bitmap resizePhoto(Context context, Bitmap photoBitmap) {

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

    public static void saveToInternalStorage(Activity activity, Bitmap bitmapImage){

        ContextWrapper contextWrapper = new ContextWrapper(activity);
        // path to /data/user/0/mprog.nl.mars_weather_explorer/app_roverImageDir
        File directory = contextWrapper.getDir("roverImageDir", Context.MODE_PRIVATE);
        // Create image file
        File imgFile = new File(directory, "roverImage.jpg");

        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(imgFile);
            // Use the compress method on the BitMap object to write the image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        SharedPreferencesManager.getInstance(activity).setImageFilePath(imgFile.getAbsolutePath());
    }
}
