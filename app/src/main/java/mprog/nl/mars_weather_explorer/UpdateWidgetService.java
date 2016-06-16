package mprog.nl.mars_weather_explorer;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.IBinder;
import android.widget.RemoteViews;

import java.net.MalformedURLException;


/**
 * Created by Nadeche
 */
public class UpdateWidgetService extends Service {

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this.getApplicationContext());

        int[] allWidgetIds = intent.getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS);

        for (int widgetId : allWidgetIds) {

            try {
                HttpRequestModel request = new HttpRequestModel();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            RemoteViews widgetLayout = new RemoteViews(this.getApplicationContext().getPackageName(),
                    R.layout.widget_mars_weather);

            widgetLayout.setTextViewText(R.id.maxTemperatureTextView, "-1");

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
}
