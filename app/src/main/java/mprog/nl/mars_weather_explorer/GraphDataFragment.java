package mprog.nl.mars_weather_explorer;

/**
 * GraphDataFragment.java
 *
 * Created by Nadeche Studer
 * */
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * This class contains a fragment displaying a graph showing
 * the maximum and minimum temperature over time on Mars.
 * From the actionbar a calendar icon opens a dialog where the user can
 * select the period of time in earth dates from which to view temperature data about.
 * When the fragment is created data about the last two weeks are loaded and displayed as default.
 * To display the graph the library MPAndroidChart is used link: https://github.com/PhilJay/MPAndroidChart
 */

public class GraphDataFragment extends BaseFragmentSuper implements FragmentLifecycle{

    private String TAG = "GraphDataFragment";

    private LineChart temperatureGraph;         // the graph object displayed on screen
    private Calendar dateTwoWeeksAgo;           // the date from two weeks ago used to initiate the dialog
    private LineData graphLines = null;         // the object that contains all data represented by the graph
    private VerticalTextView yAxisTextView;     // the textView representing the title of the yAxis
    private String graphFromDate;               // the earth date from when the graph displays data
    private String graphTillDate;               // the earth date till when the graph displays data

    // date format how the weather api uses it
    private static final SimpleDateFormat dateFormatApi = new SimpleDateFormat("yyyy-MM-dd");

    // the only instance of this fragment to prevent loss of reference to the activity
    private static GraphDataFragment instance = new GraphDataFragment();

    public static GraphDataFragment getInstance(){
        // on construction this fragment is registered in the sharedPreferencesManager
        SharedPreferencesManager.getInstance(instance.getActivity()).registerBaseFragmentSuper(instance);
        return instance;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_graph_data, container, false);
        setHasOptionsMenu(true);
        temperatureGraph = (LineChart) rootView.findViewById(R.id.temperatureLineGraph);
        yAxisTextView = (VerticalTextView) rootView.findViewById(R.id.yAxisTitle);

        initiateGraph();

        // when created for the first time initiate the from and till dates and display the data from the last two weeks
        if (temperatureGraph.isEmpty()) {
            Calendar dateToDay = Calendar.getInstance();
            dateTwoWeeksAgo = Calendar.getInstance();
            dateTwoWeeksAgo.add(Calendar.DAY_OF_YEAR, -14);

            graphTillDate = dateFormatApi.format(dateToDay.getTime());
            graphFromDate = dateFormatApi.format(dateTwoWeeksAgo.getTime());
            loadGraphWithDates();
        }
        Log.d("onCreateView fragment", "1");

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_graph_data, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_date_range:
                showChooseDateRangeDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /** This method sets how the x and y axis and the legend of the graph are displayed */
    private void initiateGraph(){

        // make an empty description since there is already a title above the graph
        temperatureGraph.setDescription("");

        XAxis xAxis = temperatureGraph.getXAxis();
        initAxis(xAxis);
        xAxis.setAvoidFirstLastClipping(true);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        YAxis yAxis = temperatureGraph.getAxisLeft();
        initAxis(yAxis);
        yAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);

        // setup how the zero line is displayed
        yAxis.setDrawZeroLine(true);
        yAxis.setZeroLineWidth(0.4f);
        yAxis.setZeroLineColor(ContextCompat.getColor(getActivity(),R.color.zeroLine));

        // draw no right axis
        temperatureGraph.getAxisRight().setEnabled(false);

        Legend legend = temperatureGraph.getLegend();
        legend.setTextColor(ContextCompat.getColor(getActivity(),R.color.lightGray));
        legend.setTextSize(12);
        legend.setForm(Legend.LegendForm.CIRCLE);
        legend.setPosition(Legend.LegendPosition.ABOVE_CHART_RIGHT);
    }

    /** This method initiates an axis to be drawn as a line with a scale in light gray */
    private void initAxis(AxisBase axis) {
        axis.setEnabled(true);
        axis.setDrawLabels(true);
        axis.setDrawAxisLine(true);
        axis.setTextColor(ContextCompat.getColor(getActivity(), R.color.lightGray));
    }

    /**
     * This method makes a request to the FetchDataAsync to get weather data from and till
     * a particular earth date
     *  */
    private void loadGraphWithDates(){
        try {
            HttpRequestModel request = new HttpRequestModel(graphFromDate, graphTillDate);
            new FetchDataAsync(GraphDataFragment.this).execute(request);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method displays a dialog to the user with two date pickers.
     * The user can set from and till when to view data about in the graph.
     * */
    private void showChooseDateRangeDialog() {
        final AlertDialog.Builder dateRangeDialog = new AlertDialog.Builder(getActivity());
        dateRangeDialog.setTitle(R.string.choose_date_range_message);

        // get a root element to display the custom layout for this dialog in
        final FrameLayout basicFrameLayout = new FrameLayout(getActivity());
        dateRangeDialog.setView(basicFrameLayout);

        dateRangeDialog.setPositiveButton(R.string.load_graph_message, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                DatePicker fromDatePicker = (DatePicker) basicFrameLayout.findViewById(R.id.fromDatePicker);
                DatePicker tillDatePicker = (DatePicker)basicFrameLayout.findViewById(R.id.tillDatePicker);

                Calendar fromCalender = Calendar.getInstance();
                fromCalender.set(fromDatePicker.getYear(), fromDatePicker.getMonth(), fromDatePicker.getDayOfMonth());
                Calendar tillCalender = Calendar.getInstance();
                tillCalender.set(tillDatePicker.getYear(), tillDatePicker.getMonth(), tillDatePicker.getDayOfMonth());

                // when the user has selected a from date later than the till date sent a notification
                if (fromCalender.after(tillCalender)){
                    Toast.makeText(getActivity(), R.string.negative_date_range_message, Toast.LENGTH_SHORT).show();
                    //dialog.dismiss();
                    return;
                }

                // convert both dates to a format recognised by the api (yyyy-MM-dd)
                graphFromDate = dateFormatApi.format(fromCalender.getTime());
                graphTillDate = dateFormatApi.format(tillCalender.getTime());

                // sent a data request and load the new graph
                loadGraphWithDates();
                dialog.dismiss();
            }
        });
        dateRangeDialog.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        // put the custom layout with the date pickers in the dialog
        AlertDialog dialog = dateRangeDialog.create();
        LayoutInflater inflater = dialog.getLayoutInflater();
        inflater.inflate(R.layout.dialog_choose_date_range, basicFrameLayout);

        DatePicker fromDatePicker = (DatePicker) basicFrameLayout.findViewById(R.id.fromDatePicker);
        DatePicker tillDatePicker = (DatePicker)basicFrameLayout.findViewById(R.id.tillDatePicker);

        // set the maximum date
        Calendar dateNow = Calendar.getInstance();
        fromDatePicker.setMaxDate(dateNow.getTimeInMillis());
        tillDatePicker.setMaxDate(dateNow.getTimeInMillis());

        // set from datePicker standard 14 days back and the till date to today
        fromDatePicker.updateDate(dateTwoWeeksAgo.get(Calendar.YEAR), dateTwoWeeksAgo.get(Calendar.MONTH), dateTwoWeeksAgo.get(Calendar.DAY_OF_MONTH));
        tillDatePicker.updateDate(dateNow.get(Calendar.YEAR),dateNow.get(Calendar.MONTH), dateNow.get(Calendar.DAY_OF_MONTH));

        // set the minimum date
        Calendar dateMinimum = Calendar.getInstance();
        dateMinimum.set(2012, Calendar.AUGUST, 22);
        fromDatePicker.setMinDate(dateMinimum.getTimeInMillis());
        tillDatePicker.setMinDate(dateMinimum.getTimeInMillis());

        dialog.show();
    }

    /** This method converts the fetched weather data to lists that can be put in the graph */
    @Override
    public void setJsonToView(ReturnDataRequestModel returnDataRequest) {
        JSONObject jsonObject = returnDataRequest.getJsonObject();
        if ( jsonObject!= null){
            // temporary hash maps that hold the temperature and the solar day
            Map<Integer, Double> tempMax = new HashMap<>();
            Map<Integer, Double> tempMin = new HashMap<>();

            // the lists to be passed to the graph
            ArrayList<Double> maxTemperature = new ArrayList<>();
            ArrayList<Double> minTemperature = new ArrayList<>();
            ArrayList<String> solarDay = new ArrayList<>();

            // the first and last sol in the returned data
            int maxSol = 0;
            int minSol = 0;
            try {
                JSONArray pagesJsonArray = jsonObject.getJSONArray("pages");

                // iterate backwards over the results so the first data come first in the result lists
                int pagesArrayLength = pagesJsonArray.length()-1;
                for (int i = pagesArrayLength ; i >= 0; i--) {

                    // the data per page
                    JSONObject pageJsonObject = pagesJsonArray.getJSONObject(i);

                    // the days of the page
                    JSONArray resultDaysJsonArray = pageJsonObject.getJSONArray("results");

                    // iterate backward again
                    int resultDaysArrayLength = resultDaysJsonArray.length()-1;
                    for (int j = resultDaysArrayLength; j >= 0 ; j--) {

                        // data of one day
                        JSONObject dailyDataJsonObject = resultDaysJsonArray.getJSONObject(j);

                        int sol = dailyDataJsonObject.getInt("sol");

                        // check what temperature unit is preferred and save the corresponding data
                        if (SharedPreferencesManager.getInstance(getActivity()).isCelsiusUnit()){
                            tempMax.put(sol, dailyDataJsonObject.getDouble("max_temp"));
                            tempMin.put(sol, dailyDataJsonObject.getDouble("min_temp"));
                        }
                        else {
                            tempMax.put(sol, dailyDataJsonObject.getDouble("max_temp_fahrenheit"));
                            tempMin.put(sol, dailyDataJsonObject.getDouble("min_temp_fahrenheit"));
                        }

                        // check if the last or first day is reached and save that solar value
                        if (i == pagesArrayLength && j == resultDaysArrayLength){
                            minSol = dailyDataJsonObject.getInt("sol");
                        }
                        else if (i == 0 && j == 0) {
                            maxSol = dailyDataJsonObject.getInt("sol");
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }finally {

                // loop over the all solar days in range
                for (int i = minSol; i <= maxSol; i++){

                    // check if there is data for that day otherwise put null to handle gaps in the data
                    if (tempMax.containsKey(i)){
                        maxTemperature.add(tempMax.get(i));
                    }
                    else {
                        maxTemperature.add(null);
                    }

                    if (tempMin.containsKey(i)){
                        minTemperature.add(tempMin.get(i));
                    }
                    else {
                        minTemperature.add(null);
                    }

                    // add every solar day to the list destined for the xAxis
                    solarDay.add(String.valueOf(i));
                }
                // pass the data to the graph for display
                setupTemperatureGraph(temperatureGraph, maxTemperature, minTemperature, solarDay);
            }
        }
    }

    /** When the preferred temperature unit changes reload the graph */
    @Override
    public void onTemperatureUnitChanged() {
        loadGraphWithDates();
    }

    /** This method converts the actual data to the graph on display */
    private void setupTemperatureGraph(LineChart temperatureGraph,
                                       ArrayList<Double> maxTemperature,
                                       ArrayList<Double> minTemperature,
                                       ArrayList<String> xValues) {

        // put the temperature data in a list from the library
        ArrayList<Entry> maxTemp = new ArrayList<>();
        ArrayList<Entry> minTemp = new ArrayList<>();
        createEntries(maxTemp, maxTemperature);
        createEntries(minTemp, minTemperature);

        // put the library list in a object representing one line
        LineDataSet maxTempSet = new LineDataSet(maxTemp, "Maximum temperature");
        styleLine(maxTempSet, ContextCompat.getColor(getActivity(),R.color.colorAccent));

        LineDataSet minTempSet = new LineDataSet(minTemp, "Minimum temperature");
        styleLine(minTempSet, ContextCompat.getColor(getActivity(),R.color.minimumTempColor));

        // add both lines to the collection of lines
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(maxTempSet);
        dataSets.add(minTempSet);

        // when a graph was drawn before: clear all data before adding new data
        if (graphLines != null){
            temperatureGraph.clear();
        }

        // put the collection of lines with the xValues in the graph
        graphLines = new LineData(xValues, dataSets);

        // re-render the graph on display
        temperatureGraph.setData(graphLines);
        temperatureGraph.notifyDataSetChanged();
        temperatureGraph.invalidate();

        // display the correct y axis title
        setYAxisTitleTempUnit();
    }

    /** This method creates a list of data points the graph library can handle */
    private void createEntries(ArrayList<Entry> entries, ArrayList<Double> yData) {
        for (int i = 0; i < yData.size(); i++) {

            // only add a data point when there is data available but with the correct index to the x axis
            if (yData.get(i) != null){
                entries.add(new Entry(yData.get(i).floatValue(), i));
            }
        }
    }

    /** This method gives the line in the graph general layout parameters */
    private void styleLine(LineDataSet lineSet, int color){
        lineSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        lineSet.setLineWidth(2f);
        lineSet.setDrawValues(false);
        lineSet.setDrawCircles(false);
        lineSet.setColor(color);
    }

    /** This method displays the title of the yAxis according to the preferred temperature unit */
    private void setYAxisTitleTempUnit(){
        if (SharedPreferencesManager.getInstance(getActivity()).isCelsiusUnit()){
            yAxisTextView.setText(getString(R.string.yaxis_title) + (char) 0x00B0 + "C");
        }
        else {
            yAxisTextView.setText(getString(R.string.yaxis_title) + (char) 0x00B0 + "F");
        }
    }

    @Override
    public void onPauseFragment() {
        Log.i(TAG, "onPauseFragment()");
    }

    @Override
    public void onResumeFragment() {
        Log.i(TAG, "onResumeFragment()");
    }
}
