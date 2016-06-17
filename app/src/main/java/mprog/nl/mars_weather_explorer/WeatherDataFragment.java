package mprog.nl.mars_weather_explorer;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Nadeche
 */
public class WeatherDataFragment extends BaseFragmentSuper implements FragmentLifecycle {
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
    private TextView photoTitleTextView;
    private ImageView roverImageView;       // view reference to rover photo in background of weather data
    private Dialog changeDateDialog;        // contains dialog to change the date to view data from
    private Dialog loadPhotoDialog;         // dialog to load a particular photo from curiosity
    private String TAG = "WeatherDataFragment";
    private WeatherDataModel weatherData;

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
        roverImageView = (ImageView)rootView.findViewById(R.id.roverImageView);
        photoTitleTextView = (TextView)rootView.findViewById(R.id.photoTitleTextView);
        //TextView textView = (TextView) rootView.findViewById(R.id.section_label);
        //textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));

        // when weather data is not yet initialised get the latest weather data and photo
        if (weatherData == null){
            // get the latest weather data
            getLatestWeatherData();
            // get the latest rover photo according to the latest sol and the last preferred rover camera
            SharedPreferencesManager preferencesManager = SharedPreferencesManager.getInstance(getActivity());
            photoTitleTextView.setText(preferencesManager.getCamera());
            try {
                HttpRequestModel request = new HttpRequestModel(preferencesManager.getLatestSol(), preferencesManager.getCamera());
                new FetchDataAsync(WeatherDataFragment.this).execute(request);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
        // when weather data is initialised reset the data to screen and get last loaded photo from internal storage
        else {
            setDataToView(weatherData);
            SharedPreferencesManager preferencesManager = SharedPreferencesManager.getInstance(getActivity());
            photoTitleTextView.setText(preferencesManager.getCamera());
            try {
                File imageFile = new File(preferencesManager.getImageFilePath());
                Bitmap bitmapImg = BitmapFactory.decodeStream(new FileInputStream(imageFile));
                roverImageView.setImageBitmap(bitmapImg);
            }
            catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        Log.d("onCreateView fragment", "0");

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
                // let the user load a photo from Curiosity
                showLoadPhotoDialog();
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

    /**
     * This method shows the user a dialog where they can choose a camera from Curiosity
     * and a Martian solar day. By clicking on the load photo button the photo is loaded with FetchDataAsync().
     * The cancel button closes the dialog and does nothing more.
     * */
    private void showLoadPhotoDialog(){
        loadPhotoDialog.setContentView(R.layout.dialog_load_photo);

        // initialise spinner to choose camera
        Spinner camerasSpinner = (Spinner)loadPhotoDialog.findViewById(R.id.camerasSpinner);
        ArrayAdapter<CharSequence> cameraListAdapter = ArrayAdapter.createFromResource(getActivity(),R.array.curiosity_cameras_list, android.R.layout.simple_spinner_item);
        cameraListAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        camerasSpinner.setAdapter(cameraListAdapter);

        // initialize number picker with the latest solar day as a maximum
        final NumberPicker numberPicker = (NumberPicker)loadPhotoDialog.findViewById(R.id.solNumberPicker);
        numberPickerInit(numberPicker);

        // retrieve the selected camera from the spinner
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

        // When load photo button is clicked make a FetchDataAsync request with the selected camera and number from the number picker
        getButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    HttpRequestModel request = new HttpRequestModel(numberPicker.getValue(), camera[0]);
                    new FetchDataAsync(WeatherDataFragment.this).execute(request);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                loadPhotoDialog.dismiss();
            }
        });
        // when the cancel button is clicked close the dialog
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
        numberPickerInit(numberPicker);

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
    /**
     * Initializes a numberPicker with a maximum of the latest requested sol and
     * a minimum of the first sol when weather data is provided
     * lets the wheel wrap around.
     * */
    private void numberPickerInit(NumberPicker numberPicker){
        numberPicker.setMaxValue(SharedPreferencesManager.getInstance(getActivity()).getLatestSol());
        numberPicker.setMinValue(15);
        numberPicker.setWrapSelectorWheel(true);
    }

    /**
     * This method is an implementation of the abstract method setJsonToView() in BaseFragmentSuper.
     * It is called by onPostExecute() in FetchDataAsync.
     * This implementation checks whether a photo request or a weather data request was made,
     * and calls the corresponding method to convert the JsonObject to data viewable on screen.
     * */
    @Override
    public void setJsonToView(ReturnDataRequestModel returnDataRequest) {
        if(returnDataRequest.getRequestModel().photoRequest){
            setJsonPhotoToView(returnDataRequest.getJsonObject(), returnDataRequest.getRequestModel().cameraName);
        }
        else {
            setJsonWeatherDataToView(returnDataRequest.getJsonObject(),returnDataRequest.getRequestModel());
        }
    }

    /**
     * This method converts a received JsonObject to a .jpg url and loads the photo async with DownloadPhotoAsync.
     * When the received object is empty it means there are not photos available for the requested camera and Martian solar day.
     * In that case the user gets notified by a toast.
     * */
    private void setJsonPhotoToView(JSONObject jsonObject, String cameraName) {

        if(jsonObject != null){
            try {
                JSONArray photosJsonArray = jsonObject.getJSONArray("photos");
                JSONObject firstPhotoJsonObject = photosJsonArray.getJSONObject(0);
                String photoLink = firstPhotoJsonObject.getString("img_src");
                new DownloadPhotoAsync(getActivity(), roverImageView).execute(photoLink);
                // save the camera name chosen as preference to load when the app opens
                SharedPreferencesManager preferencesManager = SharedPreferencesManager.getInstance(getActivity());
                preferencesManager.setCamera(cameraName);
                photoTitleTextView.setText(cameraName);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else {
            Toast.makeText(getActivity(), "No photos found for this camera and day", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * This method converts a received JsonObject to a weatherDataModel. If the received object is empty
     * it means there is no data available and the user gets notified by toast.
     * When all goes well setWeatherDataToView() gets called to display the weather data.
     * */
    private void setJsonWeatherDataToView(JSONObject jsonObject, HttpRequestModel requestModel){
        // TODO handle data from a particular earth date

        if (jsonObject != null) {
            try {
            /* when the returned Json object of a requested solar day is empty quit this action
             * and let the user know.*/
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

                weatherData = new WeatherDataModel();

                // convert the returned terrestrial date to a EU date format and save it in weatherData
                SimpleDateFormat originalDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                Date date = originalDateFormat.parse(weatherDataJsonObj.getString("terrestrial_date"));
                weatherData.setTerrestrial_date(dateFormat.format(date));


                weatherData.setSol(weatherDataJsonObj.getLong("sol"));
                // when the latest weather data where requested save the sol for other initialisations
                if (requestModel.latestWeatherData) {
                    SharedPreferencesManager.getInstance(getActivity()).setLatestSol((int)weatherData.getSol());
                }
                // save the other returned data in a weatherDataModel
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

    }

    /**
     * This method displays the received weather data to the screen.
     * It checks whether the weather status has a value.
     * */
    public void setDataToView(WeatherDataModel weatherData) {

        solTextView.setText(String.valueOf(weatherData.getSol()));
        earthDateTextView.setText(weatherData.getTerrestrial_date());

        if (SharedPreferencesManager.getInstance(getActivity()).isCelsiusUnit()) {
            // set a degrees celsius character behind the temperatures values
            maxTempTextView.setText(String.valueOf(weatherData.getMax_temp_C())+ (char) 0x00B0 + "C");
            minTempTextView.setText(String.valueOf(weatherData.getMin_temp_C()) + (char) 0x00B0 + "C");
        }
        else {
            maxTempTextView.setText(String.valueOf(weatherData.getMax_temp_F())+ (char) 0x00B0 + "F");
            minTempTextView.setText(String.valueOf(weatherData.getMin_temp_F()) + (char) 0x00B0 + "F");
        }


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

    @Override
    public void onPauseFragment() {
        Log.i(TAG, "onPauseFragment()");
    }

    @Override
    public void onResumeFragment() {
        Log.i(TAG, "onResumeFragment()");
    }
}
