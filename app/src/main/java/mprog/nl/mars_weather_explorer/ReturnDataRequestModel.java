package mprog.nl.mars_weather_explorer;

/**
 * ReturnDataRequestModel.java
 *
 * Created by Nadeche Studer
 * */
import org.json.JSONObject;

/**
 * This class acts as a collector of information about a data request in progress.
 * During the execution of the data request, the gathered data is collected in this class
 * for later processing.
 */
public class ReturnDataRequestModel {

    private JSONObject jsonObject;          // contains the returned json data
    private HttpRequestModel requestModel;  // contains information about the kind of request
    private boolean internetConnection;     // is true if there is an internet connection available

    public ReturnDataRequestModel (HttpRequestModel requestModel, boolean internetConnection){
        this.requestModel = requestModel;
        this.internetConnection = internetConnection;
    }

    public HttpRequestModel getRequestModel() {
        return requestModel;
    }

    public JSONObject getJsonObject() {
        return jsonObject;
    }

    public void setJsonObject(JSONObject jsonObject) {
        this.jsonObject = jsonObject;
    }

    public boolean internetConnectionAvailable() {
        return internetConnection;
    }
}
