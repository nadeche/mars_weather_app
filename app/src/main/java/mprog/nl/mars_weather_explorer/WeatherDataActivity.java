package mprog.nl.mars_weather_explorer;

import android.app.Dialog;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.matthewtamlin.sliding_intro_screen_library.indicators.DotIndicator;

// TODO Add menu for search on sol or date
// TODO Add dialog for search on earth date
// TODO Make data model classes
// TODO Make method to handle Json to data models
// TODO Handle fragment lifecycle loading data only when needed.
// TODO wait screen while startup app
// TODO save photo function?
// TODO replace dialogs to alertDialogs
// TODO make appIcon and widgetPreview
// TODO enhance widget layout

public class WeatherDataActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter; // contains the adapter used to swipe through fragments
    private Dialog setTemperatureUnitDialog;            // contains dialog to change the temperature unit used in the app
    private SharedPreferencesManager preferencesManager;
    private DotIndicator dotIndicator;
    private static final String USE_CELSIUS = "Use Celsius scale";
    private static final String USE_FAHRENHEIT = "Use Fahrenheit scale";

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_data);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.addOnPageChangeListener(pageChangeListener);

        // initialize the dialog
        setTemperatureUnitDialog = new Dialog(this);

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

            dotIndicator.setSelectedItem(newPosition, true);

            FragmentLifecycle fragmentToShow = (FragmentLifecycle)mSectionsPagerAdapter.getItem(newPosition);
            fragmentToShow.onResumeFragment();

            FragmentLifecycle fragmentToHide = (FragmentLifecycle)mSectionsPagerAdapter.getItem(currentPosition);
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
        if (preferencesManager.isCelsiusUnit()){
            useScale.setTitle(USE_FAHRENHEIT);
        }
        else {
            useScale.setTitle(USE_CELSIUS);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_base, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_change_unit:
                if (item.getTitle().equals(USE_FAHRENHEIT)){
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
        // call update widget
        Intent updateWidgetIntent = new Intent(this, MarsWeatherWidgetProvider.class);
        updateWidgetIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        int[] ids = {R.layout.widget_mars_weather};
        updateWidgetIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
        sendBroadcast(updateWidgetIntent);

        super.onStop();
    }
}
