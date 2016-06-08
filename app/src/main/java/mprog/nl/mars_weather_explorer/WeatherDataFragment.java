package mprog.nl.mars_weather_explorer;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Nadeche
 */
public class WeatherDataFragment extends BaseFragmentSuper {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    //private static final String ARG_SECTION_NUMBER = "section_number";
    private TextView earthDateTextView;     // contains the earth date of the data
    private TextView solTextView;           // contains the martian solar day of the data
    private TextView minTempTextView;       // contains the minimum temperature
    private TextView maxTempTextView;       // contains the maximum temperature
    private TextView windSpeedTextView;     // contains the wind speed (unknown scale)
    private TextView statusDataTextView;    // contains the status of the weather
    private TextView seasonDataTextView;    // contains the martian season
    private TextView sunriseTextView;       // contains the earth date and time on the martian sunrise
    private TextView sunsetTextView;        // contains the earth date and time on the martian sunset
    private TextView pressureTextView;      // contains the atmospheric pressure on mars
    private Dialog changeDateDialog;        // contains dialog to change the date to view data from
    private Dialog loadPhotoDialog;         // dialog to load a particular photo from curiosity

    public WeatherDataFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static WeatherDataFragment newInstance(int sectionNumber) {
        WeatherDataFragment fragment = new WeatherDataFragment();
        //Bundle args = new Bundle();
        //args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        //fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_weather_data, container, false);
        setHasOptionsMenu(true);

        changeDateDialog = new Dialog(getActivity());
        loadPhotoDialog = new Dialog(getActivity());
        loadPhotoDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

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

        // get the latest weather data
        getLatestWeatherData();

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_weather_data, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_date_picker:
                // let the user pick a different solar day
                showChooseNewSolDialog();
                return true;
            case R.id.action_home:
                // get the latest weather data
                getLatestWeatherData();
                return true;
            case R.id.action_photo_load:
                showLoadPhotoDialog();
                //Toast.makeText(getActivity(),"Change the photo in the background", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    /**
     * This method gets the latest weather data from the API
     * */
    private void getLatestWeatherData() {
        HttpRequestModel request= null;
        try {
            request = new HttpRequestModel();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        new FetchDataAsync(this).execute(request);
    }

    private void showLoadPhotoDialog(){
        loadPhotoDialog.setContentView(R.layout.dialog_load_photo);

        // initialise spinner to choose camera
        Spinner camerasSpinner = (Spinner)loadPhotoDialog.findViewById(R.id.camerasSpinner);
        ArrayAdapter<CharSequence> cameraListAdapter = ArrayAdapter.createFromResource(getActivity(),R.array.curiosity_cameras_list, android.R.layout.simple_spinner_item);
        cameraListAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        camerasSpinner.setAdapter(cameraListAdapter);

        // TODO make init number picker method
        // initialize number picker with the latest solar day as a maximum
        final NumberPicker numberPicker = (NumberPicker)loadPhotoDialog.findViewById(R.id.solNumberPicker);
        // TODO handle latest sol as maximum
        numberPicker.setMaxValue(1362);
        numberPicker.setMinValue(15);
        numberPicker.setWrapSelectorWheel(true);

        final String[] camera = new String[1];
        camerasSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                camera[0] = (String) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Button cancelButton = (Button)loadPhotoDialog.findViewById(R.id.cancelButton);
        Button getButton = (Button)loadPhotoDialog.findViewById(R.id.loadPhotoButton);

        getButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getActivity(), camera[0] + String.valueOf(numberPicker.getValue()),Toast.LENGTH_SHORT).show();
                Log.d("camera", camera[0]);

                try {
                    HttpRequestModel request = new HttpRequestModel(numberPicker.getValue(), camera[0]);
                    new FetchDataAsync(WeatherDataFragment.this).execute(request);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                loadPhotoDialog.dismiss();
            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadPhotoDialog.dismiss();
            }
        });
        loadPhotoDialog.show();
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
        // TODO handle latest sol as maximum
        numberPicker.setMaxValue(1355);
        numberPicker.setMinValue(15);
        numberPicker.setWrapSelectorWheel(true);

        Button cancelButton = (Button)changeDateDialog.findViewById(R.id.cancelButton);
        Button getButton = (Button)changeDateDialog.findViewById(R.id.loadPhotoButton);

        getButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get the data from the entered solar day
                try {
                    HttpRequestModel request = new HttpRequestModel(numberPicker.getValue());
                    new FetchDataAsync(WeatherDataFragment.this).execute(request);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
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

    @Override
    public void setJsonToView(JSONObject jsonObject, HttpRequestModel requestModel) {
        if(requestModel.photoRequest){
            setJsonPhotoToView(jsonObject, requestModel);
        }
        else {
            setJsonWeatherDataToView(jsonObject,requestModel);
        }
    }

    private void setJsonPhotoToView(JSONObject jsonObject, HttpRequestModel requestModel) {

        try {
            JSONArray photosJsonArray = jsonObject.getJSONArray("photos");
            JSONObject firstPhotoJsonObject = photosJsonArray.getJSONObject(0);
            String photoLink = firstPhotoJsonObject.getString("img_src");
            Log.d("img_src", photoLink);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setJsonWeatherDataToView(JSONObject jsonObject, HttpRequestModel requestModel){
        // TODO handle data from a particular earth date

        try {
            // when the returned Json object of a requested solar day is empty quit this action
            if(!requestModel.latestWeatherData && jsonObject.getInt("count") == 0) {
                Toast.makeText(getActivity(), getText(R.string.toast_no_data), Toast.LENGTH_SHORT).show();
                return;
            }

            JSONObject weatherDataJsonObj;
            // when the latest data was requested the weather data can be found in the report object
            if(requestModel.latestWeatherData) {
                weatherDataJsonObj = jsonObject.getJSONObject("report");
            }
            /* when a particular solar day was requested there is an extra Json array to get
             * which resides in the results object*/
            else {
                JSONArray resultArrayJsonObject = jsonObject.getJSONArray("results");
                weatherDataJsonObj = resultArrayJsonObject.getJSONObject(0);
            }

            WeatherDataModel weatherData = new WeatherDataModel();

            // convert the returned terrestrial date to a EU date format and save it in weatherData
            SimpleDateFormat originalDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
            Date date = originalDateFormat.parse(weatherDataJsonObj.getString("terrestrial_date"));
            weatherData.setTerrestrial_date(dateFormat.format(date));

            // save the other returned data in a weatherDataModel
            weatherData.setSol(weatherDataJsonObj.getLong("sol"));
            weatherData.setMax_temp_C(weatherDataJsonObj.getDouble("max_temp"));
            weatherData.setMin_temp_C(weatherDataJsonObj.getDouble("min_temp"));
            weatherData.setMax_temp_F(weatherDataJsonObj.optDouble("max_temp_fahrenheit"));
            weatherData.setMin_temp_F(weatherDataJsonObj.optDouble("min_temp_fahrenheit"));
            weatherData.setPressure(weatherDataJsonObj.getDouble("pressure"));
            weatherData.setAtmo_opacity(weatherDataJsonObj.getString("atmo_opacity"));
            weatherData.setWind_speed(weatherDataJsonObj.optLong("wind_speed"));
            weatherData.setSeason(weatherDataJsonObj.getString("season"));
            // TODO Handle date and time format
            weatherData.setSunrise(weatherDataJsonObj.getString("sunrise"));
            weatherData.setSunset(weatherDataJsonObj.getString("sunset"));

            setDataToView(weatherData);
        } catch (JSONException | ParseException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method displays the received weather data to the screen.
     * It checks whether the weather status has a value.
     * It gets the current date and time to display as update time.
     * */
    public void setDataToView(WeatherDataModel weatherData) {

        solTextView.setText(String.valueOf(weatherData.getSol()));
        earthDateTextView.setText(weatherData.getTerrestrial_date());

        // set a degrees celsius character behind the temperatures values
        maxTempTextView.setText(String.valueOf(weatherData.getMax_temp_C())+ (char) 0x00B0 + "C");
        minTempTextView.setText(String.valueOf(weatherData.getMin_temp_C()) + (char) 0x00B0 + "C");

        // display "no data" when there is no weather status
        if(weatherData.getAtmo_opacity().equals("null")) {
            statusDataTextView.setText(getText(R.string.no_data));
        }
        else {
            statusDataTextView.setText(weatherData.getAtmo_opacity());
        }

        windSpeedTextView.setText(String.valueOf(weatherData.getWind_speed()));
        seasonDataTextView.setText(weatherData.getSeason());
        pressureTextView.setText(String.valueOf(weatherData.getPressure()));
        // TODO handle layout for date and time to fit gridLayout
        //sunriseTextView.setText(weatherData.getSunrise());
        //sunsetTextView.setText(weatherData.getSunset());
    }
}
