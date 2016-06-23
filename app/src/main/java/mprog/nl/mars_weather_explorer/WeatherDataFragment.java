package mprog.nl.mars_weather_explorer;

/**
 * WeatherDataFragment.java
 *
 * Created by Nadeche Studer
 * */

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
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

/**
 * This class contains a fragment displaying weather data from Mars and a photo made on Mars by Curiosity.
 * The weather data is provided by the marsweather.ingenology api (http://marsweather.ingenology.com/).
 * The photos are provided by the Mars Photos api (https://api.nasa.gov/api.html#MarsPhotos).
 * The user can load weather data and photos from any Martian solar day since Curiosity's landing.
 */
public class WeatherDataFragment extends BaseFragmentSuper /*implements FragmentLifecycle*/ {

    private TextView earthDateTextView;                 // the earth date of the data
    private TextView solTextView;                       // the martian solar day of the data
    private TextView minTempTextView;                   // the minimum temperature
    private TextView maxTempTextView;                   // the maximum temperature
    private TextView windSpeedTextView;                 // the wind speed (unknown scale)
    private TextView statusDataTextView;                // the status of the weather
    private TextView seasonDataTextView;                // the martian season
    private TextView sunriseTextView;                   // the earth date and time on the martian sunrise
    private TextView sunsetTextView;                    // the earth date and time on the martian sunset
    private TextView pressureTextView;                  // the atmospheric pressure on mars
    private TextView photoTitleTextView;                // the camera name of the displayed photo
    private ImageView roverImageView;                   // the rover photo
    private Dialog changeDateDialog;                    // dialog to change the date to view data from
    private Dialog loadPhotoDialog;                     // dialog to load a particular photo from curiosity
    private WeatherDataModel weatherData;               // the collection of weather data
    private SharedPreferencesManager preferencesManager;// used to exchange information with the saves preferences

    // the only instance of this fragment to prevent loss of reference to the activity
    private static final WeatherDataFragment instance = new WeatherDataFragment();

    public static WeatherDataFragment getInstance() {
        // on construction this fragment is registered in the sharedPreferencesManager
        SharedPreferencesManager.getInstance(instance.getActivity()).registerBaseFragmentSuper(instance);
        return instance;
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

        preferencesManager = SharedPreferencesManager.getInstance(getActivity());

        // when weatherData is not yet initialised get the latest weather data and photo
        if (weatherData == null){
            // get the latest weather data
            getLatestWeatherData();

            // get the latest rover photo according to the latest sol and the last preferred rover camera
            photoTitleTextView.setText(preferencesManager.getCamera());
            requestPhoto(preferencesManager.getLatestSol(), preferencesManager.getCamera());
        }
        // when weather data is initialised recreate the data on screen and get the last loaded photo from internal storage
        else {
            setDataToView();
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
            case R.id.action_load_latest_data:
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

    /** This method gets the latest weather data from the API */
    private void getLatestWeatherData() {
        HttpRequestModel request= null;
        try {
            request = new HttpRequestModel();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        new FetchDataAsync(this).execute(request);
    }

    /** This method gets a photo url from the given sol and camera name */
    private void requestPhoto(int sol, String camera){
        try {
            HttpRequestModel request = new HttpRequestModel(sol, camera);
            new FetchDataAsync(WeatherDataFragment.this).execute(request);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
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

        // set selection on previous selected camera
        if (!preferencesManager.getCamera().isEmpty()){
            int spinnerPosition = cameraListAdapter.getPosition(preferencesManager.getCamera());
            camerasSpinner.setSelection(spinnerPosition);
        }

        // initialize the number picker with the latest solar day as a maximum
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

        // when the load photo button is clicked make a request with the selected camera and number from the number picker
        getButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestPhoto(numberPicker.getValue(), camera[0]);
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
                try {
                    // make a request to fetch data from the selected solar day
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
     * a minimum of the first sol when weather data is provided.
     * */
    private void numberPickerInit(NumberPicker numberPicker){
        numberPicker.setMaxValue(preferencesManager.getLatestSol());
        numberPicker.setMinValue(15);
        numberPicker.setWrapSelectorWheel(true);
    }

    /**
     * This method checks whether a photo request or a weather data request was made,
     * and calls the corresponding method to convert the JsonObject to data.
     * */
    @Override
    public void setJsonToView(ReturnDataRequestModel returnDataRequest) {
        if(returnDataRequest.getRequestModel().photoRequest){
            setJsonPhotoToView(returnDataRequest.getJsonObject(), returnDataRequest.getRequestModel().cameraName);
        }
        else {
            setJsonToWeatherData(returnDataRequest.getJsonObject(),returnDataRequest.getRequestModel());
        }
    }

    /** When the preferred temperature unit changes this method changes the data displayed on screen */
    @Override
    public void onTemperatureUnitChanged() {
        setTemperatureToTextViews();
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

                // take the first photo that is returned
                JSONObject firstPhotoJsonObject = photosJsonArray.getJSONObject(0);
                String photoLink = firstPhotoJsonObject.getString("img_src");
                new DownloadPhotoAsync(getActivity(), roverImageView).execute(photoLink);

                // save the camera name chosen as preference to load when the app opens and in the widget
                preferencesManager.setCamera(cameraName);
                photoTitleTextView.setText(cameraName);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else {
            Toast.makeText(getActivity(), R.string.no_photos_found_message, Toast.LENGTH_LONG).show();
        }
    }

    /**
     * This method fills a weatherDataModel with json data. If the json object has no data
     * the user gets notified with a toast
     * */
    private void setJsonToWeatherData(JSONObject jsonObject, HttpRequestModel requestModel){
        weatherData = new WeatherDataModel();

        if (jsonObject != null) {

            // convert the json to weather data and check if there are results
            boolean hasResults = WeatherDataManager.fillWeatherDataFromJson(jsonObject, requestModel, weatherData, preferencesManager);
            if (hasResults){
                setDataToView();
            }
            else {
                Toast.makeText(getActivity(), getText(R.string.toast_no_data), Toast.LENGTH_SHORT).show();
            }
        }
    }

    /** This method sets the corresponding temperature data to the textView according to the users preference */
    private void setTemperatureToTextViews(){
        if (SharedPreferencesManager.getInstance(getActivity()).isCelsiusUnit()) {
            // set a degrees celsius character behind the temperatures values
            maxTempTextView.setText(String.valueOf(weatherData.getMax_temp_C())+ (char) 0x00B0 + "C");
            minTempTextView.setText(String.valueOf(weatherData.getMin_temp_C()) + (char) 0x00B0 + "C");
        }
        else {
            maxTempTextView.setText(String.valueOf(weatherData.getMax_temp_F())+ (char) 0x00B0 + "F");
            minTempTextView.setText(String.valueOf(weatherData.getMin_temp_F()) + (char) 0x00B0 + "F");
        }
    }

    /** This method displays the received weather data to the screen */
    private void setDataToView() {

        solTextView.setText(String.valueOf(weatherData.getSol()));
        earthDateTextView.setText(weatherData.getTerrestrial_date());

        setTemperatureToTextViews();

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
        sunriseTextView.setText(weatherData.getSunrise());
        sunsetTextView.setText(weatherData.getSunset());
    }
}
