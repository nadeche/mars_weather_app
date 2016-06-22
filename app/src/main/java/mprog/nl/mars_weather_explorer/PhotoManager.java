package mprog.nl.mars_weather_explorer;

/**
 * PhotoManager.java
 *
 * Created by Nadeche Studer
 * */
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
 * This class provides methods to handle photo related operations:
 * download, resize and save.
 */
public class PhotoManager {

    /** This method downloads a photo from the given url and resizes it before returning the photo */
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

    /** This method resizes a photo according to the width of the screen from the device */
    public static Bitmap resizePhoto(Context context, Bitmap photoBitmap) {

        // get the width of the device screen
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        int screenWidth = metrics.widthPixels;

        // get the current dimensions of the photo
        int photoBitmapWidth = photoBitmap.getWidth();
        int photoBitmapHeight = photoBitmap.getHeight();

        if (photoBitmapWidth > screenWidth){

            // calculate the scale ratio to reduce the size by
            float scaleRatio = ((float) screenWidth) / photoBitmapWidth;

            // Create a matrix for the manipulation
            Matrix matrix = new Matrix();
            matrix.postScale(scaleRatio, scaleRatio);

            // Resize the bit map with height and width ratio conserved
            return Bitmap.createBitmap(photoBitmap, 0, 0, photoBitmapWidth, photoBitmapHeight, matrix, false);
        }
        else {
            return photoBitmap;
        }
    }

    /**
     * This method saves a photo to internal storage not accessible for the user
     * but to retrieve internally by the widget and the app to save data usage
     * */
    public static void saveToInternalStorage(Activity activity, Bitmap bitmapImage){

        ContextWrapper contextWrapper = new ContextWrapper(activity);

        // get the path to /data/user/0/mprog.nl.mars_weather_explorer/app_roverImageDir
        File directory = contextWrapper.getDir("roverImageDir", Context.MODE_PRIVATE);
        //  create the image file or overwrite if it exists
        File imgFile = new File(directory, "roverImage.jpg");

        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(imgFile);

            // compress the image and write the image to the OutputStream
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
        // save the filepath to where the image is saved in the sharedPreferences
        SharedPreferencesManager.getInstance(activity).setImageFilePath(imgFile.getAbsolutePath());
    }
}
