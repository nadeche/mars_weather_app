package mprog.nl.mars_weather_explorer;
/**
 * BaseFragmentSuper.java
 *
 * Created by Nadeche Studer
 * */
import android.app.ProgressDialog;
import android.support.v4.app.Fragment;

/**
 * This class acts as a base for all fragments in the app.
 * It handles the progressDialog when the fragment calls the FetchDataAsync task.
 * And has an abstract method setJsonToView(ReturnDataRequestModel returnDataRequest)
 * which is called in the FetchDataAsync task in order to return the fetched
 * data to the right fragment.
 * The abstract method onTemperatureUnitChanged() is called when the user changes
 * the preferred temperature unit. The sharedPreferencesManager calls this method to
 * let all fragments know the unit is changed and the views need to change with it.
 * */
public abstract class BaseFragmentSuper extends Fragment {

    /* The progressDialog that shows when async is busy.
    Static so there is only one progress dialog.
    When multiple tasks are running the first task can start the dialog
    and the last task can dismiss the same dialog.*/
    private static ProgressDialog progressDialog;

    /** Shows a progress dialog to the user with the text "loading data..." */
    public void showProgressDialog() {
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage(getText(R.string.loading));
        progressDialog.show();
    }

    /** Closes a progress dialog only when it is initiated and visible on screen */
    public void hideProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()){
            progressDialog.dismiss();
        }
    }

    /**
     * Called in FetchDataAsync in onPostExecute to return the fetched data
     * and information about the request to the fragment that made the request.
     * */
    public abstract void setJsonToView(ReturnDataRequestModel returnDataRequest);

    /**
     * Called in SharedPreferencesManager in setTemperatureUnit to let all fragments know the
     * preferred temperature unit has changed.
     * */
    public abstract void onTemperatureUnitChanged();
}
