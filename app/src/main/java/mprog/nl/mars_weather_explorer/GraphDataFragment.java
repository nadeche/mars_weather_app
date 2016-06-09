package mprog.nl.mars_weather_explorer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

public class GraphDataFragment extends BaseFragmentSuper{

    public static GraphDataFragment newInstance(){
        GraphDataFragment fragment = new GraphDataFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_graph_data, container, false);
        LineChart temperatureGraph = (LineChart) rootView.findViewById(R.id.temperatureLineGraph);

        temperatureGraph.setDescription("Min and Max temperature");

        XAxis xAxis = temperatureGraph.getXAxis();
        initAxis(xAxis);
        xAxis.setAvoidFirstLastClipping(true);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM_INSIDE);
        YAxis yAxis = temperatureGraph.getAxisLeft();
        yAxis.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
        yAxis.setDrawZeroLine(true);

        setupTemperatureGraph(temperatureGraph);
        //setupDummyGraph(temperatureGraph);

        return rootView;
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
        Entry max0 = new Entry(-12.000f, 0);
        Entry max1 = new Entry(-14.000f, 1);
        Entry max2 = new Entry(-20.000f, 2);
        maxTemp.add(max0);
        maxTemp.add(max1);
        maxTemp.add(max2);

        Entry min0 = new Entry(-72.000f, 0);
        Entry min1 = new Entry(-78.000f, 1);
        Entry min2 = new Entry(-65.000f, 2);
        minTemp.add(min0);
        minTemp.add(min1);
        minTemp.add(min2);

        // setting up line sets
        LineDataSet maxTempSet = new LineDataSet(maxTemp, "Maximum temperature");
        maxTempSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        //maxTempSet.setLineWidth(2f);
        //maxTempSet.setDrawValues(true);
        //maxTempSet.setDrawCircles(true);
        LineDataSet minTempSet = new LineDataSet(minTemp, "Minimum temperature");
        minTempSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        //minTempSet.setLineWidth(5f);
        //minTempSet.setDrawValues(true);
        //minTempSet.setDrawCircles(true);

        // setting up graph
        ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
        dataSets.add(maxTempSet);
        dataSets.add(minTempSet);

        ArrayList<String> xValues = new ArrayList<String>();
        xValues.add("3");xValues.add("4");xValues.add("5");xValues.add("6");

        // setting lines to graph
        LineData graphLines = new LineData(xValues, dataSets);
        temperatureGraph.setData(graphLines);
        temperatureGraph.invalidate();
    }

    private void setupDummyGraph(LineChart graph){
        ArrayList<Entry> valsComp1 = new ArrayList<Entry>();
        ArrayList<Entry> valsComp2 = new ArrayList<Entry>();

        Entry c1e1 = new Entry(100.000f, 0); // 0 == quarter 1
        valsComp1.add(c1e1);
        Entry c1e2 = new Entry(50.000f, 1); // 1 == quarter 2 ...
        valsComp1.add(c1e2);
        // and so on ...

        Entry c2e1 = new Entry(120.000f, 0); // 0 == quarter 1
        valsComp2.add(c2e1);
        Entry c2e2 = new Entry(110.000f, 1); // 1 == quarter 2 ...
        valsComp2.add(c2e2);

        LineDataSet setComp1 = new LineDataSet(valsComp1, "Company 1");
        setComp1.setAxisDependency(YAxis.AxisDependency.LEFT);
        LineDataSet setComp2 = new LineDataSet(valsComp2, "Company 2");
        setComp2.setAxisDependency(YAxis.AxisDependency.LEFT);

        // use the interface ILineDataSet
        ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
        dataSets.add(setComp1);
        dataSets.add(setComp2);

        ArrayList<String> xVals = new ArrayList<String>();
        xVals.add("1.Q"); xVals.add("2.Q"); xVals.add("3.Q"); xVals.add("4.Q");

        LineData data = new LineData(xVals, dataSets);
        graph.setData(data);
        graph.invalidate(); // refresh
    }
}
