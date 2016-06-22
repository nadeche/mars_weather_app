package mprog.nl.mars_weather_explorer;

/**
 * FetchDataAsync.java
 *
 * Created by Nadeche Studer
 * */

import android.os.AsyncTask;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * This class runs in the background of the activity to get data from the APIs.
 * When constructed it needs the fragment context from where the call is made
 * to pass the data back later and handle the display of a progress dialog.
 * When called it needs to be passed a Request model to tell what kind of data to get.
 * Before it tries to get data from internet it checks if there is an internet connection available.
 * If not, the user gets notified in onPostExecute by Toast notification.
 * While fetching data it displays a progress dialog to the user saying "Loading data...".
 * The data is passed back to the calling fragment for further processing.
 * */
public class FetchDataAsync extends AsyncTask<HttpRequestModel, Void, ReturnDataRequestModel> {

    private BaseFragmentSuper fragment;         // reference to the calling fragment
    private static int TASKCOUNT = 0;           // single instance representing how many times this task is called

    FetchDataAsync (BaseFragmentSuper context) {
        fragment = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        // keep track how many times this task is called
        TASKCOUNT++;

        // only on the first call to the asyncTask, show a progress dialog
        if (TASKCOUNT == 1 ){
            fragment.showProgressDialog();
        }
    }

    @Override
    protected ReturnDataRequestModel doInBackground(HttpRequestModel... requestModels) {

        // prepare a return object by passing it the requestModel and if there is a internet connection
        ReturnDataRequestModel returnDataRequest = new ReturnDataRequestModel(requestModels[0],
                InternetManager.isInternetConnectionAvailable(fragment.getActivity()));
        HttpRequestModel requestModel = requestModels[0];

        // when there is an internet connection, check what kind of request was made
        if (returnDataRequest.internetConnectionAvailable()){

            /* when either a photo, the latest weather data or weather data from a particular Martian solar
             * day request was made, make a request to get data in a single return page */
            if (requestModel.photoRequest || requestModel.latestWeatherData || requestModel.sol > -1) {
                returnDataRequest.setJsonObject(getSinglePageData(requestModel));
            }
            // when weather data for a range of dates is requested, make a request to gat data in multiple pages
            else {
                returnDataRequest.setJsonObject(getMultiplePagesData(requestModel));
            }
        }

        return returnDataRequest;
    }

    @Override
    protected void onPostExecute(ReturnDataRequestModel returnDataRequest) {
        super.onPostExecute(returnDataRequest);

        // record that this task is finished and when it is the last running task close the progress dialog
        TASKCOUNT--;
        if (TASKCOUNT == 0 && fragment.isAdded()){
            fragment.hideProgressDialog();
        }

        // when there was no internet connection notify the user
        if (!returnDataRequest.internetConnectionAvailable()) {
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
        try {
            urlConnection = (HttpURLConnection) requestModel.getUrl().openConnection();
            urlConnection.connect();

            // retrieve the data in json format
            return InternetManager.downloadDataToJson(urlConnection);

        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(urlConnection != null){
                urlConnection.disconnect();
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

        // get the first url
        String urlString = requestModel.getUrl().toString();
        URL url;

        // construct an array to put all json object pages in
        JSONArray resultPages = new JSONArray();
        try {
            do {
                url = new URL(urlString);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.connect();

                // retrieve the data from internet
                JSONObject page = InternetManager.downloadDataToJson(urlConnection);

                // retrieve the url of the next page of data
                urlString = page.getString("next");

                // put the collected JsonObject in the array holding all pages
                resultPages.put(page);

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
        }
        return null;
    }
}