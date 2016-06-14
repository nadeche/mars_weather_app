package mprog.nl.mars_weather_explorer;


import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * This class runs in the background of the activity to get data from the API's.
 * When constructed it needs the fragment context from where the call is made
 * to pass the data back later and handle the display of a progress dialog.
 * When called it needs to be passed a Request model to tell what kind of data to get.
 * While fetching data it displays a progress dialog to the user.
 * The data is passed back to the calling fragment for further processing.
 * */
public class FetchDataAsync extends AsyncTask<HttpRequestModel, Void, JSONObject> {

    BaseFragmentSuper fragment;
    HttpRequestModel requestModel;
    public static int TaskCount = 0;

    FetchDataAsync (BaseFragmentSuper context) {
        this.fragment = context;
        TaskCount++;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (TaskCount == 1){
            fragment.showProgressDialog();
        }
    }

    @Override
    protected JSONObject doInBackground(HttpRequestModel... requestModels) {
        requestModel = requestModels[0];

        if (requestModel.photoRequest || requestModel.latestWeatherData || requestModel.sol > -1) {
            return getSmallData(requestModel);
        }
        else {
            return getBigData(requestModel);
        }
    }

    @Override
    protected void onPostExecute(JSONObject jsonObject) {
        super.onPostExecute(jsonObject);

        TaskCount--;
        if (TaskCount == 0){
            fragment.hideProgressDialog();
        }

        // pass fetched Json back to the calling fragment for display on screen
        fragment.setJsonToView(jsonObject, requestModel);
    }

    private JSONObject getSmallData(HttpRequestModel requestModel) {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        try {
            urlConnection = (HttpURLConnection) requestModel.getUrl().openConnection();
            urlConnection.connect();

            // only when the http response code equals 200 aka ok process response
            if(urlConnection.getResponseCode()  == 200) {
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
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }finally {
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

    private JSONObject getBigData(HttpRequestModel requestModel) {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String urlString = requestModel.getUrl().toString();
        Log.d("urlString before do", urlString);
        URL url = null;
        JSONArray resultPages = new JSONArray();
        try {
            do {
                url = new URL(urlString);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.connect();

                // only when the http response code equals 200 aka ok process response
                if (urlConnection.getResponseCode() == 200) {
                    InputStream stream = urlConnection.getInputStream();

                    reader = new BufferedReader(new InputStreamReader(stream));
                    StringBuilder builder = new StringBuilder();
                    String line;

                    // convert received data to a string
                    while ((line = reader.readLine()) != null) {
                        builder.append(line);
                    }

                    JSONObject page = new JSONObject(builder.toString());
                    urlString = page.getString("next");
                    resultPages.put(page);
                    Log.d("urlString end of do", urlString);
                }
            }while (!urlString.equals("null"));

            JSONObject returnObject = new JSONObject();
            returnObject.put("pages", resultPages);
            return returnObject;
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
}