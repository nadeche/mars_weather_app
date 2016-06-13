package mprog.nl.mars_weather_explorer;

import android.app.ProgressDialog;
import android.support.v4.app.Fragment;
import android.util.Log;

import org.json.JSONObject;

/**
 * Created by Nadeche on 6-6-2016.
 */
public abstract class BaseFragmentSuper extends Fragment {

    private ProgressDialog progressDialog;  // contains the progressDialog that shows when async is busy

    public void showProgressDialog() {
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage(getText(R.string.loading));
        progressDialog.show();
    }

    public void hideProgressDialog() {
        if (progressDialog.isShowing()){
            progressDialog.dismiss();
            Log.d("progressDialog", "dismissed");
        }
    }

    public abstract void setJsonToView(JSONObject jsonObject, HttpRequestModel requestModel);
}
