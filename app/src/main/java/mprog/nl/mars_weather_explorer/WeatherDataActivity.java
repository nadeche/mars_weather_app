package mprog.nl.mars_weather_explorer;

/**
 * WeatherDataFragment.java
 *
 * Created by Nadeche Studer
 * */
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.matthewtamlin.sliding_intro_screen_library.indicators.DotIndicator;

// TODO Handle fragment lifecycle loading data only when needed.
/**
 * This class contains the activity that starts when the app starts.
 * It handles the display of the actionbar and the dots at the bottom
 * indicating which fragment is currently in view.
 * */
public class WeatherDataActivity extends AppCompatActivity {

    private SwipeViewsAdapter swipeViewsAdapter;            // contains the adapter used to swipe through fragments
    private SharedPreferencesManager preferencesManager;    // used to exchange information with the saves preferences
    private DotIndicator dotIndicator;                      // used to display the dot navigation at the bottom of the screen

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_data);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        swipeViewsAdapter = new SwipeViewsAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the swipeViewsAdapter so the fragments are chained to the activity
        ViewPager viewPager = (ViewPager) findViewById(R.id.container);
        viewPager.setAdapter(swipeViewsAdapter);
        viewPager.addOnPageChangeListener(pageChangeListener);

        preferencesManager = SharedPreferencesManager.getInstance(this);

        dotIndicator = (DotIndicator) findViewById(R.id.dotNavigation);
    }

    /**
     * This will menage the lifecycle of the fragment pages.
     * When in view it will call the fragments onResumeFragment implementation.
     * When moving out of view it will call the fragments implementation of onPauseFragment.
     * */
    private ViewPager.OnPageChangeListener pageChangeListener = new ViewPager.OnPageChangeListener() {

        int currentPosition = 0;

        @Override
        public void onPageSelected(int newPosition) {

            // change the highlighted dot to the new position in the navigation
            dotIndicator.setSelectedItem(newPosition, true);

            FragmentLifecycle fragmentToShow = (FragmentLifecycle) swipeViewsAdapter.getItem(newPosition);
            fragmentToShow.onResumeFragment();

            FragmentLifecycle fragmentToHide = (FragmentLifecycle) swipeViewsAdapter.getItem(currentPosition);
            fragmentToHide.onPauseFragment();

            currentPosition = newPosition;
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }
    };

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem useScale = menu.findItem(R.id.action_change_unit);

        // show the correct menu title according to which temperature unit is preferred
        if (preferencesManager.isCelsiusUnit()){
            useScale.setTitle(WeatherDataActivity.this.getString(R.string.menu_title_use_fahrenheit));
        }
        else {
            useScale.setTitle(WeatherDataActivity.this.getString(R.string.menu_title_use_celsius));
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_base, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_change_unit:

                // save the new preferred temperature unit
                if (item.getTitle().equals(WeatherDataActivity.this.getString(R.string.menu_title_use_fahrenheit))){
                    preferencesManager.setCelsiusUnit(false);
                }
                else {
                    preferencesManager.setCelsiusUnit(true);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onStop() {
        // call the update of the widget provider when the app is no longer visible
        Intent updateWidgetIntent = new Intent(this, MarsWeatherWidgetProvider.class);
        updateWidgetIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        int[] ids = {R.layout.widget_mars_weather};
        updateWidgetIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
        sendBroadcast(updateWidgetIntent);

        super.onStop();
    }
}
