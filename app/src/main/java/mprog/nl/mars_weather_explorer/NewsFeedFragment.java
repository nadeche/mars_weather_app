package mprog.nl.mars_weather_explorer;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * Created by Nadeche
 */

// TODO Add custom list adapter and layout
// TODO Add onListItemClickListener to new activity to view news item
// TODO Add home and back function in the actionbar from newsItem
// TODO Add update functionality

public class NewsFeedFragment extends Fragment {

    public static NewsFeedFragment newInstance (){
        NewsFeedFragment fragment = new NewsFeedFragment();
        return fragment;
    }

    private ListView newsFeedListView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_news_feed, container, false);

        String[]dummyListItems = {"News item 1", "News item 2", "News item 3", "News item 4"};
        newsFeedListView = (ListView)rootView.findViewById(R.id.newsFeedListView);
        newsFeedListView.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, dummyListItems));
        return rootView;
    }
}
