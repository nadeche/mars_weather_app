package mprog.nl.mars_weather_explorer;

/**
 * SwipeViewsAdapter.java
 *
 * Created by Android Studio
 *
 * Edited by Nadeche Studer
 * */

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * This class acts as an adapter between the fragment container in the activity layout
 * and the actual fragments to be displayed.
 * */
class SwipeViewsAdapter extends FragmentPagerAdapter {

    public SwipeViewsAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    /**
     * This method returns the instance of the fragment based on the position of the
     * swipe views
     * */
    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                return WeatherDataFragment.getInstance();
            case 1:
                return GraphDataFragment.getInstance();
        }
        return null;

    }

    @Override
    public int getCount() {
        // there are 2 swipe views
        return 2;
    }
}