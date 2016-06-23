package mprog.nl.mars_weather_explorer;

/**
 * WeatherDataManager.java
 *
 * Created by Nadeche Studer
 * */
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * This class manages the weather data from how it is received to a form that can be handled in the application.
 * It can convert data for the app and for the widget.
 */
public class WeatherDataManager {

    /**
     * This method converts a received JsonObject to a weatherDataModel to be used in the app. If the received object is empty
     * it means there is no data available and false is returned. If all goes well true is returned.
     * */
    public static boolean fillWeatherDataFromJson(JSONObject jsonObject, HttpRequestModel requestModel, WeatherDataModel weatherData,
                                                  SharedPreferencesManager preferencesManager){

        try {
            // when the returned Json object of a requested solar day is empty quit this action
            if(!requestModel.latestWeatherData && jsonObject.getInt("count") == 0) {
                return false;
            }

            JSONObject weatherDataJsonObj;
            // when the latest data was requested the weather data can be found in the report object
            if(requestModel.latestWeatherData) {
                weatherDataJsonObj = jsonObject.getJSONObject("report");
            }

            // when a particular solar day was requested there is an extra Json array to get
            else {
                JSONArray resultArrayJsonObject = jsonObject.getJSONArray("results");
                weatherDataJsonObj = resultArrayJsonObject.getJSONObject(0);
            }

            // convert the returned terrestrial date to a EU date format and save it in weatherData
            SimpleDateFormat originalDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
            Date date = originalDateFormat.parse(weatherDataJsonObj.getString("terrestrial_date"));
            weatherData.setTerrestrial_date(dateFormat.format(date));

            weatherData.setSol(weatherDataJsonObj.getLong("sol"));
            // when the latest weather data where requested save the sol for other initialisations
            if (requestModel.latestWeatherData) {
                preferencesManager.setLatestSol((int)weatherData.getSol());
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
            // convert time and dates to device time and date
            weatherData.setSunrise(convertUTCtoLocalTime(weatherDataJsonObj.getString("sunrise")));
            weatherData.setSunset(convertUTCtoLocalTime(weatherDataJsonObj.getString("sunset")));


        } catch (JSONException | ParseException e) {
            e.printStackTrace();
        }

        return true;
    }

    /** This method converts json to weather data used by the widget */
    public static WeatherDataModel jsonToWeatherData(JSONObject jsonObject){
        if (jsonObject != null) {
            try {
                JSONObject weatherDataJsonObj;
                // when the latest data was requested the weather data can be found in the report object
                weatherDataJsonObj = jsonObject.getJSONObject("report");

                WeatherDataModel weatherData = new WeatherDataModel();

                weatherData.setSol(weatherDataJsonObj.getLong("sol"));

                // save the other returned data in a weatherDataModel
                weatherData.setMax_temp_C(weatherDataJsonObj.getDouble("max_temp"));
                weatherData.setMin_temp_C(weatherDataJsonObj.getDouble("min_temp"));
                weatherData.setMax_temp_F(weatherDataJsonObj.optDouble("max_temp_fahrenheit"));
                weatherData.setMin_temp_F(weatherDataJsonObj.optDouble("min_temp_fahrenheit"));

                return weatherData;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * Method to convert the sunrise and set time to local time.
     * It takes a dateTime string in format: year-month-dayThour:minute:secondZ.
     * It converts the date to: num-day text-month hour:minute
     * when the parse was un success full it returns a string containing "None".
     * */
    private static String convertUTCtoLocalTime(String originalDate) {
        SimpleDateFormat dateFormatIn = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        dateFormatIn.setTimeZone(TimeZone.getTimeZone("UTC"));
        SimpleDateFormat dateFormatOut = new SimpleDateFormat("dd MMM HH:mm");
        dateFormatOut.setTimeZone(TimeZone.getDefault());
        try {
            // convert string to date in UTC
            Date dateUTC = dateFormatIn.parse(originalDate);
            // convert UTC date to local time in right format
            return dateFormatOut.format(dateUTC);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "None";
    }
}
