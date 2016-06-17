package mprog.nl.mars_weather_explorer;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.RemoteViews;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;


/**
 * Created by Nadeche
 */
public class MarsWeatherWidgetProvider extends AppWidgetProvider {

    public static final String ACTION_PHOTO_CHANGED = "mprog.nl.mars_weather_explorer.PHOTO_CHANGED";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        // Get all ids
        ComponentName thisWidget = new ComponentName(context, MarsWeatherWidgetProvider.class);
        int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);

        // Build the intent to call the service
        Intent intent = new Intent(context.getApplicationContext(), UpdateWidgetService.class);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, allWidgetIds);

        // Update the widgets via the service
        context.startService(intent);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        Log.d("onReceive", "is called");
        if (intent.getAction().equals(ACTION_PHOTO_CHANGED)){
            Log.d("intent equals", ACTION_PHOTO_CHANGED);
            try {
                File imageFile = new File(SharedPreferencesManager.getInstance(context).getImageFilePath());
                Bitmap bitmapImg = BitmapFactory.decodeStream(new FileInputStream(imageFile));
                Log.d("image decoded", "succes");
                RemoteViews widgetLayout = new RemoteViews(context.getPackageName(),
                        R.layout.widget_mars_weather);
                widgetLayout.setImageViewBitmap(R.id.backgroundImgView, bitmapImg);
                Log.d("background photo", "should have changed");
                ComponentName thisWidget = new ComponentName(context, MarsWeatherWidgetProvider.class);
                AppWidgetManager.getInstance(context).updateAppWidget(thisWidget, widgetLayout);
                Log.d("widget layout", "updated");
            }
            catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}
