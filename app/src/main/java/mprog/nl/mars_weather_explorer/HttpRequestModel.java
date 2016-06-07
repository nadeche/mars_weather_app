package mprog.nl.mars_weather_explorer;


import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Nadeche
 *
 * This class contains information about the type of date request to be done.
 */
public class HttpRequestModel {

    private URL url;                    // contains the url of the requested data
    public int sol;                     // contains the martian solar date to find
    public boolean latestWeatherData;   // is true if the latest data is requested

    // constructor to get the latest weather data
    HttpRequestModel() throws MalformedURLException {
        latestWeatherData = true;
        sol = -1;
        url = new URL("http://marsweather.ingenology.com/v1/latest/?format=json");
    }

    // constructor to get weather data from a particular Martian solar day
    HttpRequestModel(int sol) throws MalformedURLException {
        latestWeatherData = false;
        this.sol = sol;
        url = new URL("http://marsweather.ingenology.com/v1/archive/?sol=" + sol + "&format=json");
    }

    public URL getUrl() {
        return url;
    }
}
