package mprog.nl.mars_weather_explorer;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import java.io.InputStream;

/**
 * Created by Nadeche
 */
public class DownloadPhotoAsync extends AsyncTask <String, Void, Bitmap> {

    ImageView roverPhoto;

    public DownloadPhotoAsync(ImageView roverImageView) {
        this.roverPhoto = roverImageView;
    }

    @Override
    protected Bitmap doInBackground(String... urls) {

        Bitmap photoBitMap = null;
        try {
            InputStream inputStream = new java.net.URL(urls[0]).openStream();
            photoBitMap = BitmapFactory.decodeStream(inputStream);
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }
        return photoBitMap;
    }

    protected void onPostExecute(Bitmap loadedPhoto) {
        roverPhoto.setImageBitmap(loadedPhoto);
    }
}
