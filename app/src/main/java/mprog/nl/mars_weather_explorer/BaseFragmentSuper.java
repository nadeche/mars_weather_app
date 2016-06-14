package mprog.nl.mars_weather_explorer;

import android.app.ProgressDialog;
import android.support.v4.app.Fragment;
import android.util.Log;

import org.json.JSONObject;

/**
 * Created by Nadeche
 */
public abstract class BaseFragmentSuper extends Fragment {

    private static ProgressDialog progressDialog;  // contains the progressDialog that shows when async is busy

    public void showProgressDialog() {
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage(getText(R.string.loading));
        progressDialog.show();
    }

    public void hideProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()){
            progressDialog.dismiss();
        }
    }

    public abstract void setJsonToView(JSONObject jsonObject, HttpRequestModel requestModel);
}
