package mprog.nl.mars_weather_explorer;

/**
 * MarsWeatherWidgetProvider.java
 *
 * Created by Nadeche Studer
 * */
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;


/**
 * This class extends an AppWidgetProvider that calls the UpdateWidgetService
 * when the widget receives an update call.
 * */
public class MarsWeatherWidgetProvider extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        // collect all widget ids
        ComponentName thisWidget = new ComponentName(context, MarsWeatherWidgetProvider.class);
        int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);

        // build the intent to call the service
        Intent intent = new Intent(context.getApplicationContext(), UpdateWidgetService.class);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, allWidgetIds);

        // update all installed widgets via the service
        context.startService(intent);
    }
}
