package mprog.nl.mars_weather_explorer;

import org.json.JSONObject;

/**
 * Created by Nadeche on 14-6-2016.
 */
public class ReturnDataRequestModel {

    private JSONObject jsonObject;
    private HttpRequestModel requestModel;
    private boolean internetConnection;

    public ReturnDataRequestModel (HttpRequestModel requestModel, boolean internetConnection){
        this.requestModel = requestModel;
        this.internetConnection = internetConnection;
    }

    public HttpRequestModel getRequestModel() {
        return requestModel;
    }

    public void setRequestModel(HttpRequestModel requestModel) {
        this.requestModel = requestModel;
    }

    public JSONObject getJsonObject() {
        return jsonObject;
    }

    public void setJsonObject(JSONObject jsonObject) {
        this.jsonObject = jsonObject;
    }

    public boolean isInternetConnection() {
        return internetConnection;
    }

    public void setInternetConnection(boolean internetConnection) {
        this.internetConnection = internetConnection;
    }
}
