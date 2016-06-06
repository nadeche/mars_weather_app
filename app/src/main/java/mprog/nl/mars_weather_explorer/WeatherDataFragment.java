package mprog.nl.mars_weather_explorer;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
        HttpRequestModel request= null;
        try {
            request = new HttpRequestModel();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        new FetchDataAsync(this).execute(request);

        return rootView;
    }

    @Override
    public void setJsonToView(JSONObject jsonObject) {
        // TODO handle empty Json object
        // TODO handle data from a particular date

        JSONObject weatherDataJsonObj;
        // when the latest data was requested the weather data can be found in the report object

        try {
            weatherDataJsonObj = jsonObject.getJSONObject("report");

            WeatherDataModel weatherData = new WeatherDataModel();

            // convert the returned terrestrial date to a EU date format and save it in weatherData
            SimpleDateFormat originalDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
            Date date = originalDateFormat.parse(weatherDataJsonObj.getString("terrestrial_date"));
            weatherData.setTerrestrial_date(dateFormat.format(date));

            // save the other returned data in a weatherDataModel
            weatherData.setSol(weatherDataJsonObj.getLong("sol"));
            weatherData.setMax_temp_C(weatherDataJsonObj.getLong("max_temp"));
            weatherData.setMin_temp_C(weatherDataJsonObj.getLong("min_temp"));
            weatherData.setMax_temp_F(weatherDataJsonObj.getLong("max_temp_fahrenheit"));
            weatherData.setMin_temp_F(weatherDataJsonObj.getLong("min_temp_fahrenheit"));
            weatherData.setPressure(weatherDataJsonObj.getLong("pressure"));
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
    }
}
