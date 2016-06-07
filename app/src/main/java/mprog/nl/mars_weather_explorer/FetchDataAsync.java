package mprog.nl.mars_weather_explorer;

/**
 * Created by Nadeche
 */

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * This class runs in the background of the activity to get the weather data from the api.
 * When called it needs to be passed a Request model to tell what kind of data to get.
 * While fetching data it displays a progress dialog to the user.
 * The data is saved in a WeatherDataModel from where the data is displayed on screen.
 * */
public class FetchDataAsync extends AsyncTask<HttpRequestModel, Void, JSONObject> {

    BaseFragmentSuper fragment;
    HttpRequestModel requestModel;


    FetchDataAsync (BaseFragmentSuper context) {
        this.fragment = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        fragment.showProgressDialog();
    }

    @Override
    protected JSONObject doInBackground(HttpRequestModel... requestModels) {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        requestModel = requestModels[0];

        try {
            urlConnection = (HttpURLConnection) requestModel.getUrl().openConnection();
            urlConnection.connect();

            InputStream stream = urlConnection.getInputStream();

            reader = new BufferedReader(new InputStreamReader(stream));
            StringBuilder builder = new StringBuilder();
            String line;

            // convert received data to a string
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }

            // convert complete data to Json object
            return new JSONObject(builder.toString());

            /*// when the returned Json object of a requested solar day is empty quit this action
            if(!requestModels[0].latestWeatherData && reportJsonObject.getInt("count") == 0) {
                return null;
            }

            JSONObject weatherDataJsonObj;
            // when the latest data was requested the weather data can be found in the report object
            if(requestModels[0].latestWeatherData) {
                weatherDataJsonObj = reportJsonObject.getJSONObject("report");
            }
            /* when a particular solar day was requested there is an extra Json array to get
             * which resides in the results object
            else {
                JSONArray resultArrayJsonObject = reportJsonObject.getJSONArray("results");
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

            return weatherData;*/

        } catch (IOException | JSONException e) {
            e.printStackTrace();
        } finally {
            if(urlConnection != null){
                urlConnection.disconnect();
            }
            try {
                if(reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(JSONObject jsonObject) {
        super.onPostExecute(jsonObject);

        fragment.hideProgressDialog();

        // when doInBackground has quit because there was no data, let the user know
        /*if(weatherData == null){
            Toast.makeText(fragment, fragment.getText(R.string.toast_no_data), Toast.LENGTH_SHORT).show();
            return;
        }*/

        // display fetched weather data
        fragment.setJsonToView(jsonObject, requestModel);
    }
}