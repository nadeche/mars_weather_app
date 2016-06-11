package mprog.nl.mars_weather_explorer;

/**
 * Created by Nadeche
 */

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

/**
 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {

    public SectionsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.

        switch (position) {
            case 0:
                Log.d("pagerAdapter 0 position", String.valueOf(position));
                // Return a PlaceholderFragment (defined as a static inner class below).
                return WeatherDataFragment.newInstance(position + 1);
            case 1:
                Log.d("pagerAdapter 1 position", String.valueOf(position));
                return GraphDataFragment.newInstance();
            case 2:
                Log.d("pagerAdapter 2 position", String.valueOf(position));
                return NewsFeedFragment.newInstance();
        }
        return null;

    }

    @Override
    public int getCount() {
        // Show 3 total pages.
        return 3;
    }

        /*@Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "SECTION 1";
                case 1:
                    return "SECTION 2";
                case 2:
                    return "SECTION 3";
            }
            return null;
        }*/
}