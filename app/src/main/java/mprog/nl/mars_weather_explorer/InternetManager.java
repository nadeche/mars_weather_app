package mprog.nl.mars_weather_explorer;

/**
 * InternetManager.java
 *
 * Created by Nadeche Studer
 * */
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;

/**
 * This class handles internet related operations that occur in multiple points in the app and the widget.
 */
public class InternetManager {

    /** This method returns true if there is an internet connection available */
    public static boolean isInternetConnectionAvailable(Context context) {

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    /** This method is used to get data via the internet and convert it to a json object for further processing */
    public static JSONObject downloadDataToJson(HttpURLConnection urlConnection) {
        BufferedReader reader = null;
        try {
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

                // convert complete data to Json object
                return new JSONObject(builder.toString());
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}