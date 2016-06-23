package mprog.nl.mars_weather_explorer;

/**
 * UpdateWidgetService.java
 *
 * Created by Nadeche Studer
 * */

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.widget.RemoteViews;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.util.concurrent.CountDownLatch;

/**
 * This service provides the widget with data.
 * It collects data about the weather on Mars and loads a photo from memory
 * or from internet.
 * */
public class UpdateWidgetService extends Service {

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this.getApplicationContext());

        SharedPreferencesManager preferencesManager = SharedPreferencesManager.getInstance(getApplicationContext());

        // check to make sure it is not a call from when the app force closes
        if (intent != null) {

            // collect all widget ids of all installed widget instances and update them all
            int[] allWidgetIds = intent.getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS);

            // retrieve the latest weather data
            WeatherDataModel weatherData = getWeatherData();

            for (int widgetId : allWidgetIds) {

                RemoteViews widgetLayout = new RemoteViews(this.getApplicationContext().getPackageName(),
                        R.layout.widget_mars_weather);

                if (weatherData != null) {
                    // set the weather data to textViews
                    setDataToTextViews(widgetLayout, preferencesManager, weatherData);

                    // load a photo for display on the background
                    Bitmap bitmapImg = loadPhoto(preferencesManager, weatherData);

                    // display the photo in the widget
                    widgetLayout.setImageViewBitmap(R.id.backgroundImgView, bitmapImg);
                }

                // intent to launch mars weather explorer app on click on the widget
                Intent intentWidgetClick = new Intent(this.getApplicationContext(), WeatherDataActivity.class);
                PendingIntent pendingIntent = PendingIntent.getActivity(this.getApplicationContext(), 0 , intentWidgetClick, 0);
                widgetLayout.setOnClickPendingIntent(R.id.widgetContainer, pendingIntent);

                // update the widget individual widget
                appWidgetManager.updateAppWidget(widgetId, widgetLayout);
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * This method makes a request to get weather data and converts it to usable fields
     * which are returned in a weatherDataModel
     * */
    private WeatherDataModel getWeatherData(){
        try {
            HttpRequestModel request = new HttpRequestModel();
            JSONObject jsonData = getData(request);
            if (jsonData != null){
                return WeatherDataManager.jsonToWeatherData(jsonData);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /** This method sets the collected weather data to the textViews of the widget*/
    private void setDataToTextViews(RemoteViews widgetLayout, SharedPreferencesManager preferencesManager, WeatherDataModel weatherData) {
        if (preferencesManager.isCelsiusUnit()) {
            widgetLayout.setTextViewText(R.id.maxTemperatureTextView,
                    String.valueOf(String.valueOf(weatherData.getMax_temp_C()) + (char) 0x00B0 + "C"));
            widgetLayout.setTextViewText(R.id.minTemperatureTextView,
                    String.valueOf(String.valueOf(weatherData.getMin_temp_C()) + (char) 0x00B0 + "C"));
        } else {
            widgetLayout.setTextViewText(R.id.maxTemperatureTextView,
                    String.valueOf(String.valueOf(weatherData.getMax_temp_F()) + (char) 0x00B0 + "F"));
            widgetLayout.setTextViewText(R.id.minTemperatureTextView,
                    String.valueOf(String.valueOf(weatherData.getMin_temp_F()) + (char) 0x00B0 + "F"));
        }

        widgetLayout.setTextViewText(R.id.solDateTextViewWidget, String.valueOf(weatherData.getSol()));
    }

    /** This method loads a photo either from memory or from internet */
    private Bitmap loadPhoto(SharedPreferencesManager preferencesManager, WeatherDataModel weatherData) {
        Bitmap bitmapImg = null;

        // load a photo from internet when there is new weather data available since the app was opened
        if (preferencesManager.getLatestSol() < weatherData.getSol()) {
            try {
                HttpRequestModel request = new HttpRequestModel((int) weatherData.getSol(), preferencesManager.getCamera());
                JSONObject jsonPhoto = getData(request);
                bitmapImg = getPhoto(jsonPhoto);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
        // when there is no new data available load the photo from memory
        else {
            try {
                File imageFile = new File(preferencesManager.getImageFilePath());
                bitmapImg = BitmapFactory.decodeStream(new FileInputStream(imageFile));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        return bitmapImg;
    }

    /** This method gets api data on a separate thread */
    private JSONObject getData(final HttpRequestModel request){

        // only get data if there is an internet connection
        if (InternetManager.isInternetConnectionAvailable(this.getApplicationContext())){

            // initiate a countdown mechanism to await the thread to finish before the code gets further executed
            final CountDownLatch countDownLatch = new CountDownLatch(1);

            // this array gets filled on the tread and can be retrieved after the thread finishes
            final JSONArray jsonArray = new JSONArray();

            new Thread(new Runnable() {
                @Override
                public void run() {
                    HttpURLConnection urlConnection = null;
                    try {
                        urlConnection = (HttpURLConnection)request.getUrl().openConnection();
                        urlConnection.connect();

                        // retrieve the data from internet and save it in json
                        JSONObject jsonObject = InternetManager.downloadDataToJson(urlConnection);
                        jsonArray.put(jsonObject);

                        // let the awaiting code know the data is available
                        countDownLatch.countDown();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }finally {
                        if (urlConnection != null) {
                            urlConnection.disconnect();
                        }
                    }
                }
            }).start();
            try {
                // wait un till the thread is finished before continuing
                countDownLatch.await();
                return jsonArray.getJSONObject(0);
            } catch (InterruptedException | JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /** This method downloads a photo on a separate thread */
    private Bitmap getPhoto(JSONObject jsonPhoto){
        if(jsonPhoto != null){
            try {
                JSONArray photosJsonArray = jsonPhoto.getJSONArray("photos");
                // pick the first photo that is returned to download
                JSONObject firstPhotoJsonObject = photosJsonArray.getJSONObject(0);
                final String photoLink = firstPhotoJsonObject.getString("img_src");

                // initiate countdown mechanism to to await the thread to finish before the code gets further executed
                final CountDownLatch countDownLatch = new CountDownLatch(1);

                // this array gets filled on the tread and can be retrieved after the thread finishes
                final Bitmap[] bitmaps = new Bitmap[1];

                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        // download the photo
                        bitmaps[0] = PhotoManager.downloadPhoto(getApplicationContext(),photoLink);

                        // let the awaiting code know the data is available
                        countDownLatch.countDown();
                    }
                }).start();

                // wait un till the thread is finished before continuing
                countDownLatch.await();
                return bitmaps[0];
            } catch (JSONException | InterruptedException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
