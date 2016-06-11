package mprog.nl.mars_weather_explorer;

import android.app.Dialog;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.NumberPicker;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.Date;

// TODO Add menu for search on sol or date
// TODO Add dialog for search on earth date
// TODO Add actionbar function: icon and dialog for date search in graph data fragment
// TODO Create dot navigation at the bottom
// TODO Make data model classes
// TODO Make method to handle Json to data models
// TODO Create load photo save in memory
// TODO Handle the save of settings with sharedPref
// TODO Add the latest SOL to the sharedPref and check later to update
// TODO Fix FragmentManager warning "not updated inline; expected state 3 found 2"
// TODO Handle fragment lifecycle loading data only when needed.

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

        // initialize the dialogs
        setTemperatureUnitDialog = new Dialog(this);





        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

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
            case R.id.action_settings:
                showSetTemperatureUnitDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void showSetTemperatureUnitDialog(){
        setTemperatureUnitDialog.setContentView(R.layout.dialog_temperature_setting);
        setTemperatureUnitDialog.setTitle("Setting");

        Button cancelButton = (Button)setTemperatureUnitDialog.findViewById(R.id.cancelButton);
        Button saveButton = (Button)setTemperatureUnitDialog.findViewById(R.id.saveButton);
        Switch temperatureSwitch = (Switch)setTemperatureUnitDialog.findViewById(R.id.temperatureSwitch);
        final String[] temperatureUnit = new String[1];
        temperatureUnit[0] = "Nothing changed";
        temperatureSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    temperatureUnit[0] = "Fahrenheit";
                }
                else{
                    temperatureUnit[0] = "Celsius";
                }
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(WeatherDataActivity.this,"Saved " + temperatureUnit[0], Toast.LENGTH_SHORT).show();
                setTemperatureUnitDialog.dismiss();
            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(WeatherDataActivity.this,"Canceled", Toast.LENGTH_SHORT).show();
                setTemperatureUnitDialog.dismiss();
            }
        });

        setTemperatureUnitDialog.show();
    }

}
