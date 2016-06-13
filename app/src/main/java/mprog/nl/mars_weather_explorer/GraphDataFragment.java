package mprog.nl.mars_weather_explorer;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
//import android.support.v7.app.AlertDialog;
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
import java.util.GregorianCalendar;

/**
 * Created by Nadeche
 */

public class GraphDataFragment extends BaseFragmentSuper implements FragmentLifecycle{

    private String TAG = "GraphDataFragment";
    private ArrayList<Double> maxCelsius = new ArrayList<>();
    private ArrayList<Double> minCelsius = new ArrayList<>();
    private ArrayList<String> solarDay = new ArrayList<>();
    private LineChart temperatureGraph;
    private Calendar dateToDay;
    private Calendar dateLastWeek;
    private LineData graphLines = null;

    public static GraphDataFragment newInstance(){
        GraphDataFragment fragment = new GraphDataFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_graph_data, container, false);
        setHasOptionsMenu(true);
        temperatureGraph = (LineChart) rootView.findViewById(R.id.temperatureLineGraph);

        temperatureGraph.setDescription("");

        XAxis xAxis = temperatureGraph.getXAxis();
        initAxis(xAxis);
        xAxis.setAvoidFirstLastClipping(true);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        YAxis yAxis = temperatureGraph.getAxisLeft();
        initAxis(yAxis);
        yAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        yAxis.setDrawZeroLine(true);
        YAxis rightYAxis = temperatureGraph.getAxisRight();
        rightYAxis.setEnabled(false);

        Legend legend = temperatureGraph.getLegend();
        legend.setTextColor(ContextCompat.getColor(getActivity(),R.color.lightGray));
        legend.setTextSize(12);
        legend.setForm(Legend.LegendForm.CIRCLE);
        legend.setPosition(Legend.LegendPosition.ABOVE_CHART_RIGHT);

        // when created for the first time initiate dates and display date from the last week
        if (dateToDay == null) {
            dateToDay = Calendar.getInstance();
            dateLastWeek = Calendar.getInstance();
            dateLastWeek.add(Calendar.DAY_OF_YEAR, -7);

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String dataTill = dateFormat.format(dateToDay.getTime());
            String dataFrom = dateFormat.format(dateLastWeek.getTime());
            try {
                HttpRequestModel request = new HttpRequestModel(dataFrom, dataTill);
                new FetchDataAsync(GraphDataFragment.this).execute(request);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
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

    private void showChooseDateRangeDialog() {
        final AlertDialog.Builder dateRangeDialog = new AlertDialog.Builder(getActivity());
        dateRangeDialog.setTitle("Choose a date range");
        final FrameLayout basicFrameLayout = new FrameLayout(getActivity());
        dateRangeDialog.setView(basicFrameLayout);

        dateRangeDialog.setPositiveButton("Load Graph", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DatePicker fromDatePicker = (DatePicker) basicFrameLayout.findViewById(R.id.fromDatePicker);
                DatePicker tillDatePicker = (DatePicker)basicFrameLayout.findViewById(R.id.tillDatePicker);

                Calendar fromCalender = Calendar.getInstance();
                fromCalender.set(fromDatePicker.getYear(), fromDatePicker.getMonth(), fromDatePicker.getDayOfMonth());
                Calendar tillCalender = Calendar.getInstance();
                tillCalender.set(tillDatePicker.getYear(), tillDatePicker.getMonth(), tillDatePicker.getDayOfMonth());
                // check if the user has selected a viable date range
                if (fromCalender.after(tillCalender)){
                    Toast.makeText(getActivity(),"A negative date range was selected", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    return;
                }

                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                String fromDate = dateFormat.format(fromCalender.getTime());
                String tillDate = dateFormat.format(tillCalender.getTime());

                try {
                    HttpRequestModel request = new HttpRequestModel(fromDate, tillDate);
                    new FetchDataAsync(GraphDataFragment.this).execute(request);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                dialog.dismiss();
            }
        });
        dateRangeDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = dateRangeDialog.create();
        LayoutInflater inflater = dialog.getLayoutInflater();
        inflater.inflate(R.layout.dialog_choose_date_range, basicFrameLayout);

        DatePicker fromDatePicker = (DatePicker) basicFrameLayout.findViewById(R.id.fromDatePicker);
        DatePicker tillDatePicker = (DatePicker)basicFrameLayout.findViewById(R.id.tillDatePicker);

        // set maximum date
        fromDatePicker.setMaxDate(dateToDay.getTimeInMillis());
        tillDatePicker.setMaxDate(dateToDay.getTimeInMillis());
        // set from datePicker standard 7 days back
        fromDatePicker.updateDate(dateLastWeek.get(Calendar.YEAR),dateLastWeek.get(Calendar.MONTH), dateLastWeek.get(Calendar.DAY_OF_MONTH));
        // set minimum date
        Calendar calendar = Calendar.getInstance();
        calendar.set(2012, Calendar.AUGUST, 22);
        fromDatePicker.setMinDate(calendar.getTimeInMillis());
        tillDatePicker.setMinDate(calendar.getTimeInMillis());

        dialog.show();
    }

    private void initAxis(AxisBase axis) {
        // enable axis to be drawn
        axis.setEnabled(true);
        // draw axis labels
        axis.setDrawLabels(true);
        // draw axis line
        axis.setDrawAxisLine(true);
        axis.setTextColor(ContextCompat.getColor(getActivity(), R.color.lightGray));
    }

    @Override
    public void setJsonToView(JSONObject jsonObject, HttpRequestModel requestModel) {
        maxCelsius.removeAll(maxCelsius);
        minCelsius.removeAll(minCelsius);
        solarDay.removeAll(solarDay);
        try {
            JSONArray pagesJsonArray = jsonObject.getJSONArray("pages");
            // iterate backwards so the first data come first in the result lists
            for (int i = pagesJsonArray.length()-1 ; i >= 0; i--) {
                JSONObject pageJsonObject = pagesJsonArray.getJSONObject(i);
                JSONArray resultDaysJsonArray = pageJsonObject.getJSONArray("results");
                for (int j = resultDaysJsonArray.length()-1; j >= 0 ; j--) {
                    JSONObject dailyDataJsonObject = resultDaysJsonArray.getJSONObject(j);
                    maxCelsius.add(dailyDataJsonObject.getDouble("max_temp"));
                    minCelsius.add(dailyDataJsonObject.getDouble("min_temp"));
                    solarDay.add(String.valueOf(dailyDataJsonObject.getLong("sol")));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }finally {
            setupTemperatureGraph(temperatureGraph, maxCelsius, minCelsius, solarDay);
        }
    }

    private void setupTemperatureGraph(LineChart temperatureGraph,
                                       ArrayList<Double> maxCelsius,
                                       ArrayList<Double> minCelsius,
                                       ArrayList<String> xValues) {

        // setting up line data
        ArrayList<Entry> maxTemp = new ArrayList<Entry>();
        ArrayList<Entry> minTemp = new ArrayList<Entry>();
        createEntries(maxTemp,maxCelsius);
        createEntries(minTemp, minCelsius);

        // setting up line sets
        LineDataSet maxTempSet = new LineDataSet(maxTemp, "Maximum temperature");
        styleLine(maxTempSet);
        maxTempSet.setColor(ContextCompat.getColor(getActivity(),R.color.colorAccent));

        LineDataSet minTempSet = new LineDataSet(minTemp, "Minimum temperature");
        styleLine(minTempSet);
        minTempSet.setColor(ContextCompat.getColor(getActivity(),R.color.minimumTempColor));

        // setting up graph
        ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
        dataSets.add(maxTempSet);
        dataSets.add(minTempSet);

        // when a graph was drawn before: clear all data from that graph before adding new data
        if (graphLines != null){
            temperatureGraph.clear();
        }
        // setting lines to graph
        graphLines = new LineData(xValues, dataSets);
        temperatureGraph.setData(graphLines);
        temperatureGraph.notifyDataSetChanged();
        temperatureGraph.invalidate();
    }

    private void createEntries(ArrayList<Entry> entries, ArrayList<Double> yData) {
        for (int i = 0; i < yData.size(); i++) {
            entries.add(new Entry(yData.get(i).floatValue(), i));
        }
    }

    private void styleLine(LineDataSet lineSet){
        lineSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        lineSet.setLineWidth(2f);
        lineSet.setDrawValues(false);
        lineSet.setDrawCircles(false);
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
