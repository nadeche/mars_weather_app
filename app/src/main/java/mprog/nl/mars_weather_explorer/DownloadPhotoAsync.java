package mprog.nl.mars_weather_explorer;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Nadeche
 */
public class DownloadPhotoAsync extends AsyncTask <String, Void, Bitmap> {

    private Activity context;
    ImageView roverPhoto;

    public DownloadPhotoAsync(Activity context, ImageView roverImageView) {
        this.context = context;
        this.roverPhoto = roverImageView;
    }

    @Override
    protected Bitmap doInBackground(String... urls) {

        Bitmap photoBitMap = null;
        try {
            InputStream inputStream = new java.net.URL(urls[0]).openStream();
            photoBitMap = BitmapFactory.decodeStream(inputStream);
            PhotoManager photoManager = new PhotoManager();
            photoBitMap = photoManager.getPhoto(context, photoBitMap);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return photoBitMap;
    }

    protected void onPostExecute(Bitmap loadedPhoto) {
        roverPhoto.setImageBitmap(loadedPhoto);
        saveToInternalStorage(loadedPhoto);
    }



    private void saveToInternalStorage(Bitmap bitmapImage){

        ContextWrapper contextWrapper = new ContextWrapper(context);
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
                fileOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        SharedPreferencesManager.getInstance(context).setImageFilePath(imgFile.getAbsolutePath());
    }
}
