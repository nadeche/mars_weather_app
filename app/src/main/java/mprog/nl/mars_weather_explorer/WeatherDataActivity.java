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

import java.text.SimpleDateFormat;
import java.util.Date;

// TODO Add menu for search on sol or date
// TODO Add dialog for search on date
// TODO Change actionbar for other fragments
// TODO Add actionbar function: icon and dialog for date search in graph data fragment
// TODO Create dot navigation at the bottom
// TODO Add dialog to change photo
// TODO Make internet connection > manifest and asyncTask class
// TODO Make request class
// TODO Make data model classes
// TODO Make method to handle Json to data models
// TODO Create load photo method (save?)
// TODO Handle the save of settings with sharedPref
// TODO Add the latest SOL to the sharedPref and check later to update

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
    private Dialog changeDateDialog;                    // contains dialog to change the date to view data from
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

        // initialize the dialogs
        changeDateDialog = new Dialog(this);
        setTemperatureUnitDialog = new Dialog(this);

        // get the latest weather data
        HttpRequestModel request= new HttpRequestModel();
        new FetchDataAsync(this).execute(request);



        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_weather_data, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch (item.getItemId()) {
            case R.id.action_date_picker:
                // let the user pick a different solar day
                showChooseNewSolDialog();
                return true;
            case R.id.action_home:
                // get latest weather data
                //getApiData(null);
                Toast.makeText(WeatherDataActivity.this,"Latest weather data fetched", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.action_photo_load:
                Toast.makeText(WeatherDataActivity.this,"Change the photo in the background", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.action_settings:
                showSetTemperatureUnitDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Placeholder fragment containing the weatherDataFragment
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        //private static final String ARG_SECTION_NUMBER = "section_number";
        private TextView earthDateTextView;     // contains the earth date of the data
        private TextView solTextView;           // contains the martian solar day of the data
        private TextView minTempTextView;       // contains the minimum temperature
        private TextView maxTempTextView;      // contains the maximum temperature
        private TextView windSpeedTextView;     // contains the wind speed (unknown scale)
        private TextView statusDataTextView;    // contains the status of the weather
        private TextView seasonDataTextView;    // contains the martian season
        private TextView sunriseTextView;       // contains the earth date and time on the martian sunrise
        private TextView sunsetTextView;        // contains the earth date and time on the martian sunset
        private TextView pressureTextView;      // contains the atmospheric pressure on mars

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            //Bundle args = new Bundle();
            //args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            //fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_weather_data, container, false);

            earthDateTextView = (TextView)rootView.findViewById(R.id.earthDateTextView);
            solTextView = (TextView)rootView.findViewById(R.id.solDateTextView);
            minTempTextView = (TextView)rootView.findViewById(R.id.minTempTextView);
            maxTempTextView = (TextView)rootView.findViewById(R.id.maxTempTextView);
            windSpeedTextView = (TextView)rootView.findViewById(R.id.windSpeedTextView);
            statusDataTextView = (TextView)rootView.findViewById(R.id.statusTextView);
            seasonDataTextView = (TextView)rootView.findViewById(R.id.seasonTextView);
            sunriseTextView = (TextView)rootView.findViewById(R.id.sunriseTextView);
            sunsetTextView = (TextView)rootView.findViewById(R.id.sunsetTextView);
            pressureTextView = (TextView)rootView.findViewById(R.id.pressureTextView);
            //TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            //textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
            return rootView;
        }
    }

    /**
     * This method displays a dialog where the user can choose
     * a solar day to see the weather data from.
     * When a day is confirmed the data is fetched directly.
     * */
    private void showChooseNewSolDialog() {

        changeDateDialog.setContentView(R.layout.dialog_change_date);
        changeDateDialog.setTitle("Change date");

        // initialize number picker with the latest solar day as a maximum
        final NumberPicker numberPicker = (NumberPicker)changeDateDialog.findViewById(R.id.solNumberPicker);
        numberPicker.setMaxValue(1355);
        numberPicker.setMinValue(15);
        numberPicker.setWrapSelectorWheel(true);

        Button cancelButton = (Button)changeDateDialog.findViewById(R.id.cancelButton);
        Button getButton = (Button)changeDateDialog.findViewById(R.id.getButton);

        getButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get the data from the entered solar day
                Toast.makeText(WeatherDataActivity.this,"New sol " + numberPicker.getValue(), Toast.LENGTH_SHORT).show();
                changeDateDialog.dismiss();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeDateDialog.dismiss();
            }
        });

        changeDateDialog.show();
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
