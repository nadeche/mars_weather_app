package mprog.nl.mars_weather_explorer;

/**
 * HttpRequestModel.java
 *
 * Created by Nadeche Studer
 * */

import java.net.MalformedURLException;
import java.net.URL;

/**
 * This class acts as a model that contains information about the type of data request to be done.
 */
public class HttpRequestModel {

    private URL url;                          // the url of the requested data
    public final int sol;                     // the martian solar date to find
    public final boolean latestWeatherData;   // is true if the latest weather data is requested
    public final boolean photoRequest;        // is true if a curiosity photo is requested
    public final String cameraName;           // the name of the camera to get a photo from

    // personal NASA API key to get rover photos from the NASA database
    private static final String NASAapiKEY = "it3AjXuLitEdyfChV0ramuFS0cq12GvJVKTjgllC";

    /** Constructor to get the latest weather data */
    HttpRequestModel() throws MalformedURLException {
        latestWeatherData = true;
        sol = -1;
        photoRequest = false;
        cameraName = null;
        url = new URL("http://marsweather.ingenology.com/v1/latest/?format=json");
    }

    /** Constructor to get weather data from a particular Martian solar day */
    HttpRequestModel(int sol) throws MalformedURLException {
        latestWeatherData = false;
        this.sol = sol;
        photoRequest = false;
        cameraName = null;
        url = new URL("http://marsweather.ingenology.com/v1/archive/?sol=" + sol + "&format=json");
    }

    /** Constructor to get weather data from an earth date till an earth date */
    HttpRequestModel (String from, String till) throws MalformedURLException {
        latestWeatherData = false;
        sol = -1;
        photoRequest = false;
        cameraName = null;
        url = new URL("http://marsweather.ingenology.com/v1/archive/?terrestrial_date_end="+till+"&terrestrial_date_start="+from+"&format=json");
    }

    /** Constructor to get a photo from Curiosity by Martian solar day and camera type */
    HttpRequestModel(int sol, String camera) throws MalformedURLException {
        latestWeatherData = false;
        this.sol = sol;
        photoRequest = true;
        this.cameraName = camera;
        // convert camera name to camera abbreviation to use in http request
        switch (camera) {
            case "Front Hazard Avoidance Camera":
                camera = "FHAZ";
                break;
            case "Rear Hazard Avoidance Camera":
                camera = "RHAZ";
                break;
            case "Mast Camera":
                camera = "MAST";
                break;
            case "Chemistry and Camera Complex":
                camera = "CHEMCAM";
                break;
            case "Mars Hand Lens Imager":
                camera = "MAHLI";
                break;
            case "Mars Descent Imager":
                camera = "MARDI";
                break;
            case "Navigation Camera":
                camera = "NAVCAM";
                break;
        }
        url = new URL("https://api.nasa.gov/mars-photos/api/v1/rovers/curiosity/" +
                "photos?sol=" + sol + "&camera=" + camera + "&api_key=" + NASAapiKEY);
    }

    public URL getUrl() {
        return url;
    }
}
