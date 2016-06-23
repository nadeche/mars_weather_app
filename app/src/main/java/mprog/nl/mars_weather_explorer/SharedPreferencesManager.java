package mprog.nl.mars_weather_explorer;

/**
 * SharedPreferencesManager.java
 *
 * Created by Nadeche Studer
 * */
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is a singleton that updates the SharedPreferences whenever they change.
 * It provides information to the app and to the widget about the user's preferences.
 * */
public class SharedPreferencesManager{

    private SharedPreferences.Editor sharedPreferencesEditor;           // can make alterations to the saved preferences
    private boolean celsiusUnit;                                        // true if the user prefers to use the Celsius scale
    private String camera;                                              // the name of the camera the user has last loaded a photo from
    private int latestSol;                                              // the last Martian solar day there is data about
    private String imageFilePath;                                       // the file path where the last downloaded photo is stored
    private List<BaseFragmentSuper> baseFragments = new ArrayList<>();  // a list of all fragments in the swipe views

    private static SharedPreferencesManager instance = null;

    private SharedPreferencesManager(Context context){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPreferencesEditor = sharedPreferences.edit();
        celsiusUnit = sharedPreferences.getBoolean("celsiusUnit", true);
        camera = sharedPreferences.getString("camera", "Front Hazard Avoidance Camera");
        latestSol = sharedPreferences.getInt("latestSol", 1377);
        imageFilePath = sharedPreferences.getString("imageFilePath", "");
    }

    public static SharedPreferencesManager getInstance(Context context){
        if(instance == null){
            instance = new SharedPreferencesManager(context);
        }
        return instance;
    }

    public String getCamera() {
        return camera;
    }

    public void setCamera(String camera) {
        this.camera = camera;
        sharedPreferencesEditor.putString("camera", camera);
        sharedPreferencesEditor.commit();
    }

    public boolean isCelsiusUnit() {
        return celsiusUnit;
    }

    /** This method loops through all fragments when the temperature unit changes
     * to let them know the unit is changed and they should act accordingly */
    public void setCelsiusUnit(boolean celsiusUnit) {
        this.celsiusUnit = celsiusUnit;
        sharedPreferencesEditor.putBoolean("celsiusUnit", celsiusUnit);
        sharedPreferencesEditor.commit();

        for (BaseFragmentSuper baseFragment:baseFragments){
            baseFragment.onTemperatureUnitChanged();
        }
    }

    public int getLatestSol() {
        return latestSol;
    }

    public void setLatestSol(int latestSol) {
        this.latestSol = latestSol;
        sharedPreferencesEditor.putInt("latestSol", latestSol);
        sharedPreferencesEditor.commit();
    }

    public String getImageFilePath() {
        return imageFilePath;
    }

    public void setImageFilePath(String imageFilePath) {
        this.imageFilePath = imageFilePath;
        sharedPreferencesEditor.putString("imageFilePath", imageFilePath);
        sharedPreferencesEditor.commit();
    }

    /** This method checks the list of fragments and adds the fragment if it is not in the list */
    public void registerBaseFragmentSuper(BaseFragmentSuper baseFragment){
        boolean classExists = false;
        for (BaseFragmentSuper fragment: baseFragments){
            if (fragment.getClass() == baseFragment.getClass()){
                classExists = true;
                break;
            }
        }
        if (!classExists){
            baseFragments.add(baseFragment);
        }
    }
}
