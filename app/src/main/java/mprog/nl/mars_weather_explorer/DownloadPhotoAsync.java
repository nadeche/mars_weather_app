package mprog.nl.mars_weather_explorer;

/**
 * DownloadPhotoAsync.java
 *
 * Created by Nadeche Studer
 * */

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.ImageView;

/**
 * This class contains an AsyncTask implementation in order to download a photo.
 * On construction it needs the Activity and the ImageView where the photo will be displayed.
 * When called it takes a string containing the url where the photo can be found.
 * In the background the photo gets downloaded and resized to fit the dimension in width of the
 * used screen device.
 * Finally the photo is displayed in the given imageView and the photo is saved to internal storage.
 * During these operation a progress dialog is displayed to the user saying "Loading photo..."
 * */
public class DownloadPhotoAsync extends AsyncTask <String, Void, Bitmap> {

    private Activity activity;               // the reference to the activity from the calling fragment
    private ImageView roverPhoto;            // the reference to imageView where the downloaded photo will be displayed
    private ProgressDialog progressDialog;   // the progress dialog displayed while the task is busy

    public DownloadPhotoAsync(Activity activity, ImageView roverImageView) {
        this.activity = activity;
        roverPhoto = roverImageView;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog = new ProgressDialog(activity);
        progressDialog.setMessage(activity.getString(R.string.loading_photo));
        progressDialog.show();
    }

    @Override
    protected Bitmap doInBackground(String... urls) {

        // let the photoManager download the photo and resize it before return
        return PhotoManager.downloadPhoto(activity, urls[0]);
    }

    @Override
    protected void onPostExecute(Bitmap loadedPhoto) {

        // set the downloaded photo to the imageView
        roverPhoto.setImageBitmap(loadedPhoto);

        // the photoManager saves the photo
        PhotoManager.saveToInternalStorage(activity, loadedPhoto);
        progressDialog.dismiss();
    }
}
