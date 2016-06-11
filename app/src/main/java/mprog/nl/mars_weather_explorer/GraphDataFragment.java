package mprog.nl.mars_weather_explorer;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Nadeche
 */
// TODO Add graphView library and incorporate
// TODO Add to actionbar search date range function

public class GraphDataFragment extends BaseFragmentSuper implements FragmentLifecycle{

    //private Dialog dateRangeDialog; // dialog where the user can select from when till when to view temperature data from
    private String TAG = "GraphDataFragment";

    public static GraphDataFragment newInstance(){
        GraphDataFragment fragment = new GraphDataFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_graph_data, container, false);
        setHasOptionsMenu(true);
        //dateRangeDialog = new Dialog(getActivity());
        LineChart temperatureGraph = (LineChart) rootView.findViewById(R.id.temperatureLineGraph);

        temperatureGraph.setDescription("Min and Max temperature");

        XAxis xAxis = temperatureGraph.getXAxis();
        initAxis(xAxis);
        xAxis.setAvoidFirstLastClipping(true);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        YAxis yAxis = temperatureGraph.getAxisLeft();
        yAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        yAxis.setDrawZeroLine(true);

        setupTemperatureGraph(temperatureGraph);

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
        //Dialog dateRangeDialog = new Dialog(getActivity());
        //dateRangeDialog.setContentView(R.layout.dialog_choose_date_range);
        //dateRangeDialog.setTitle("Choose a date range");

        //DatePickerFragment testDatePicker = new DatePickerFragment();
        //testDatePicker.show(getFragmentManager() , "datePicker");
        //dateRangeDialog.show();

        final AlertDialog.Builder dateRangeDialog = new AlertDialog.Builder(getActivity());
        dateRangeDialog.setTitle("Choose a date range");
        FrameLayout fl = new FrameLayout(getActivity());
        dateRangeDialog.setView(fl);

        //LayoutInflater inflater = getActivity().getLayoutInflater();
        //View dialogLayout = inflater.inflate(R.layout.dialog_choose_date_range, null);
        //dateRangeDialog.setView(dialogLayout);
        dateRangeDialog.setPositiveButton("Load Graph", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

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
        inflater.inflate(R.layout.dialog_choose_date_range, fl);
        dialog.show();
    }

    private void initAxis(AxisBase axis) {
        // enable axis to be drawn
        axis.setEnabled(true);
        // draw axis labels
        axis.setDrawLabels(true);
        // draw axis line
        axis.setDrawAxisLine(true);
    }

    @Override
    public void setJsonToView(JSONObject jsonObject, HttpRequestModel requestModel) {

    }

    private void setupTemperatureGraph(LineChart temperatureGraph) {
        // setting up line data
        ArrayList<Entry> maxTemp = new ArrayList<Entry>();
        ArrayList<Entry> minTemp = new ArrayList<Entry>();
        int[] max = {-12, -14, -20, -12};
        createEntries(maxTemp,max);
        int[] min = {-72, -78, -65};
        createEntries(minTemp, min);

        // setting up line sets
        LineDataSet maxTempSet = new LineDataSet(maxTemp, "Maximum temperature");
        styleLine(maxTempSet);
        maxTempSet.setColor(getResources().getColor(R.color.colorAccent));

        LineDataSet minTempSet = new LineDataSet(minTemp, "Minimum temperature");
        styleLine(minTempSet);

        // setting up graph
        ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
        dataSets.add(maxTempSet);
        dataSets.add(minTempSet);

        ArrayList<String> xValues = new ArrayList<String>();
        String[] xLabels = {"3", "4", "5", "6"};
        for (int i = 0; i < xLabels.length; i++) {
            xValues.add(xLabels[i]);
        }

        // setting lines to graph
        LineData graphLines = new LineData(xValues, dataSets);
        temperatureGraph.setData(graphLines);
        temperatureGraph.invalidate();
    }

    private void createEntries(ArrayList<Entry> entries, int[] yData) {
        for (int i = 0; i < yData.length; i++) {
            entries.add(new Entry((float) yData[i], i));
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
