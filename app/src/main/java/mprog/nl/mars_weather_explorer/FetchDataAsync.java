package mprog.nl.mars_weather_explorer;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import java.net.URL;

/**
 * This class runs in the background of the activity to get data from the API's.
 * When constructed it needs the fragment context from where the call is made
 * to pass the data back later and handle the display of a progress dialog.
 * When called it needs to be passed a Request model to tell what kind of data to get.
 * While fetching data it displays a progress dialog to the user.
 * The data is passed back to the calling fragment for further processing.
 * */
public class FetchDataAsync extends AsyncTask<HttpRequestModel, Void, ReturnDataRequestModel> {

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
        if (TaskCount == 1 && fragment.isAdded()){
            fragment.showProgressDialog();
        }
    }

    @Override
    protected ReturnDataRequestModel doInBackground(HttpRequestModel... requestModels) {
        ReturnDataRequestModel returnDataRequest = new ReturnDataRequestModel(requestModels[0], hasInternetConnection());
        requestModel = requestModels[0];

        if (returnDataRequest.isInternetConnection()){

            if (requestModel.photoRequest || requestModel.latestWeatherData || requestModel.sol > -1) {
                returnDataRequest.setJsonObject(getSmallData(requestModel));
            }
            else {
                returnDataRequest.setJsonObject(getBigData(requestModel));
                            }
        }

        return returnDataRequest;
    }

    @Override
    protected void onPostExecute(ReturnDataRequestModel returnDataRequest) {
        super.onPostExecute(returnDataRequest);

        TaskCount--;
        if (TaskCount == 0 && fragment.isAdded()){
            fragment.hideProgressDialog();
        }

        if (!returnDataRequest.isInternetConnection()) {
            Toast.makeText(fragment.getActivity(), "No internet connection available", Toast.LENGTH_SHORT).show();
            return;
        }

        // pass fetched Json back to the calling fragment for display on screen
        fragment.setJsonToView(returnDataRequest);
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

    private boolean hasInternetConnection() {

        ConnectivityManager connectivityManager = (ConnectivityManager)fragment.getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }
}