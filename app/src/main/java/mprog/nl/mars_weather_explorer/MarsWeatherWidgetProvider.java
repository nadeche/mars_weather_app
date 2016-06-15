package mprog.nl.mars_weather_explorer;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;


/**
 * Created by Nadeche
 */
public class MarsWeatherWidgetProvider extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        final int numberOfWidgets = appWidgetIds.length;

        // Perform this loop procedure for each App Widget that belongs to this provider
        for (int i = 0; i < numberOfWidgets; i++){
            int appWidgetId = appWidgetIds[i];

            RemoteViews widgetLayout = new RemoteViews(context.getPackageName(), R.layout.widget_mars_weather);

            // intent to lunch mars weather explorer activity
            Intent intent = new Intent(context, WeatherDataActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0 , intent, 0);
            widgetLayout.setOnClickPendingIntent(R.id.lunchAppButton, pendingIntent);

            appWidgetManager.updateAppWidget(appWidgetId, widgetLayout);
        }
    }
}
