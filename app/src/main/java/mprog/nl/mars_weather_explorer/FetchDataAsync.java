package mprog.nl.mars_weather_explorer;

/**
 * Created by Nadeche
 */

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;
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
            Log.d("inputStream return", stream.toString());

            reader = new BufferedReader(new InputStreamReader(stream));
            StringBuilder builder = new StringBuilder();
            String line;

            // convert received data to a string
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }

            // convert complete data to Json object
            return new JSONObject(builder.toString());

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

        // display fetched weather data
        fragment.setJsonToView(jsonObject, requestModel);
    }
}