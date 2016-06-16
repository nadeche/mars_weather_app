package mprog.nl.mars_weather_explorer;

import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Nadeche
 */
public class WeatherDataManager {

    public WeatherDataModel jsonToData(JSONObject jsonObject, HttpRequestModel request){
        if (jsonObject != null) {
            try {
                /* when the returned Json object of a requested solar day is empty quit this action
                 * and let the user know.*/
                /*if(!request.latestWeatherData && jsonObject.getInt("count") == 0) {
                    Toast.makeText(getActivity(), getText(R.string.toast_no_data), Toast.LENGTH_SHORT).show();
                    return;
                }*/

                JSONObject weatherDataJsonObj;
                // when the latest data was requested the weather data can be found in the report object
                if(request.latestWeatherData) {
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


                weatherData.setSol(weatherDataJsonObj.getLong("sol"));
                // when the latest weather data where requested save the sol for other initialisations
                /*if (request.latestWeatherData) {
                    SharedPreferencesManager.getInstance(getActivity()).setLatestSol((int)weatherData.getSol());
                }*/
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

                return weatherData;
            } catch (JSONException | ParseException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
