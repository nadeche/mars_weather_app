package mprog.nl.mars_weather_explorer;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.util.concurrent.CountDownLatch;


/**
 * Created by Nadeche
 */
public class UpdateWidgetService extends Service {

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this.getApplicationContext());

        int[] allWidgetIds = intent.getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS);

        for (int widgetId : allWidgetIds) {
            WeatherDataModel weatherData = null;
            try {
                HttpRequestModel request = new HttpRequestModel();
                Log.d(" onS reuest url", request.getUrl().toString());
                JSONObject jsonData = getData(request);
                Log.d("onS return json", jsonData.toString());
                WeatherDataManager manager = new WeatherDataManager();
                weatherData = manager.jsonToData(jsonData, request);

            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            RemoteViews widgetLayout = new RemoteViews(this.getApplicationContext().getPackageName(),
                    R.layout.widget_mars_weather);

            widgetLayout.setTextViewText(R.id.maxTemperatureTextView, String.valueOf(weatherData.getMax_temp_C()));
            widgetLayout.setTextViewText(R.id.minTemperatureTextView, String.valueOf(weatherData.getMin_temp_C()));

            // intent to lunch mars weather explorer activity on button click
            Intent intentButtonClick = new Intent(this.getApplicationContext(), WeatherDataActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this.getApplicationContext(), 0 , intentButtonClick, 0);
            widgetLayout.setOnClickPendingIntent(R.id.lunchAppButton, pendingIntent);

            appWidgetManager.updateAppWidget(widgetId, widgetLayout);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private JSONObject getData(final HttpRequestModel request){
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final JSONArray jsonArray = new JSONArray();
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d("Thread and run","entered");
                HttpURLConnection urlConnection = null;
                BufferedReader reader = null;
                try {
                    urlConnection = (HttpURLConnection)request.getUrl().openConnection();
                    urlConnection.connect();

                    // only when the http response code equals 200 aka ok process response
                    if(urlConnection.getResponseCode()  == 200) {
                        Log.d("Run url connection", "ok");
                        InputStream stream = urlConnection.getInputStream();

                        reader = new BufferedReader(new InputStreamReader(stream));
                        StringBuilder builder = new StringBuilder();
                        String line;

                        // convert received data to a string
                        while ((line = reader.readLine()) != null) {
                            builder.append(line);
                        }

                        // convert complete data to Json object
                        JSONObject jsonObject = new JSONObject(builder.toString());
                        jsonArray.put(jsonObject);
                        countDownLatch.countDown();
                        Log.d("Run countdown", "fineshed");
                    }
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }finally {
                    if (urlConnection != null) {
                        urlConnection.disconnect();
                    }
                    try {
                        if (reader != null) {
                            reader.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
        try {
            Log.d("GD waiting", "for countdown");
            countDownLatch.await();
            Log.d("GD countdown fineshed", jsonArray.toString());
            return jsonArray.getJSONObject(0);
        } catch (InterruptedException | JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
