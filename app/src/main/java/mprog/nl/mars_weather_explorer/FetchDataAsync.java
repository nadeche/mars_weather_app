package mprog.nl.mars_weather_explorer;

/**
 * FetchDataAsync.java
 *
 * Created by Nadeche Studer
 * */

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
 * Before it tries to get data from internet it checks if there is a internet connection available.
 * If not the user gets notified in onPostExecute by Toast notification.
 * While fetching data it displays a progress dialog to the user saying "Loading data...".
 * The data is passed back to the calling fragment for further processing.
 * */
public class FetchDataAsync extends AsyncTask<HttpRequestModel, Void, ReturnDataRequestModel> {

    private BaseFragmentSuper fragment;         // fragment reference to the calling fragment
    private HttpRequestModel requestModel;      // reference to the request information
    private static int TASKCOUNT = 0;           // single instance representing how many times this task is called

    FetchDataAsync (BaseFragmentSuper context) {
        fragment = context;

        // keep up how many times this task is called
        TASKCOUNT++;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        // only on the first call to the asyncTask, show a progress dialog
        if (TASKCOUNT == 1 ){
            fragment.showProgressDialog();
        }
    }

    @Override
    protected ReturnDataRequestModel doInBackground(HttpRequestModel... requestModels) {

        // prepare a return object by passing it the requestModel and if there is a internet connection
        ReturnDataRequestModel returnDataRequest = new ReturnDataRequestModel(requestModels[0], hasInternetConnection());
        requestModel = requestModels[0];

        // when there is an internet connection, check what kind of request was made
        if (returnDataRequest.isInternetConnection()){

            /* when a photo, the latest weather data or weather data from a particular Martian solar day was made
            *  make a request to get data in a single return page*/
            if (requestModel.photoRequest || requestModel.latestWeatherData || requestModel.sol > -1) {
                returnDataRequest.setJsonObject(getSinglePageData(requestModel));
            }
            // when a weather data from a range of dates is requested make a request to gat data in multiple pages
            else {
                returnDataRequest.setJsonObject(getMultiplePagesData(requestModel));
            }
        }

        return returnDataRequest;
    }

    @Override
    protected void onPostExecute(ReturnDataRequestModel returnDataRequest) {
        super.onPostExecute(returnDataRequest);

        // sing this task out from taskCount and when it is the last running task close the progress dialog
        TASKCOUNT--;
        if (TASKCOUNT == 0 && fragment.isAdded()){
            fragment.hideProgressDialog();
        }

        // when there is no internet connection notify the user by toast
        if (!returnDataRequest.isInternetConnection()) {
            Toast.makeText(fragment.getActivity(), R.string.no_internet_message, Toast.LENGTH_SHORT).show();
            return;
        }

        // pass the collected data back to the calling fragment for further processing and display on screen
        fragment.setJsonToView(returnDataRequest);
    }

    /**
     * This method handles a data request that gets just one page of Json data in return.
     * */
    private JSONObject getSinglePageData(HttpRequestModel requestModel) {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        try {
            urlConnection = (HttpURLConnection) requestModel.getUrl().openConnection();
            urlConnection.connect();

            // only when the http response code equals 200 aka an ok response get the data
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

    /**
     * This method handles a data request that gets multiple pages of Json data in return.
     * All pages are collected in a JsonArray which is put in a JsonObject before return,
     * so all return objects are of tha same type.
     * */
    private JSONObject getMultiplePagesData(HttpRequestModel requestModel) {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // get the first url
        String urlString = requestModel.getUrl().toString();
        URL url = null;

        // construct an array to put all json object pages in
        JSONArray resultPages = new JSONArray();
        try {
            do {
                url = new URL(urlString);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.connect();

                // only when the http response code equals 200 aka an ok response get the data
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

                    // retrieve the url of the next page of data
                    urlString = page.getString("next");

                    // put the collected JsonObject in the array holding all pages
                    resultPages.put(page);
                }
            // keep requesting data until there is no new url in the last returned page
            }while (!urlString.equals("null"));

            // construct the return JsonObject and add the array holding all pages
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

    /** This method checks returns true if there is an internet connection available */
    private boolean hasInternetConnection() {

        ConnectivityManager connectivityManager = (ConnectivityManager)fragment.getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }
}