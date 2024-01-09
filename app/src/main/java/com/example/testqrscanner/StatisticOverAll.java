package com.example.testqrscanner;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;

import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class StatisticOverAll extends Fragment {

    private PieChart pieChart;
    private PieChart pieChartClassification;
    private BarChart barChart;
    private BarChart barChartClassification;
    private DatePickerDialog datePickerDialog;
    private String yyear;
    private String mmonth;
    private String dday;
    private EditText txt_dateStart;
    private EditText txt_dateEnd;
    private LocalDate dateStart;
    private LocalDate dateEnd;


    StatisticOverAll(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_statistic_all, container, false);
        pieChart = (PieChart) view.findViewById(R.id.pieChartCategories);
        pieChartClassification = (PieChart) view.findViewById(R.id.pieChartClassification);
        barChart = (BarChart) view.findViewById(R.id.barChartCategories);
        barChartClassification = (BarChart) view.findViewById(R.id.barChartClassification);
        txt_dateStart = (EditText) view.findViewById(R.id.txt_dateStart);
        txt_dateEnd = (EditText) view.findViewById(R.id.txt_dateEnd);
        dateStart = LocalDate.MIN;
        dateEnd = LocalDate.MAX;
        txt_dateStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popDatePicker(txt_dateStart);
            }
        });
        txt_dateEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popDatePicker(txt_dateEnd);
            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        buildCharts();
    }

    private void buildCharts(){
        ArrayList<Check> checksDB = DataBaseController.getChecks(this.getActivity());
        ArrayList<Check> checks = new ArrayList<>();
        for(Check check : checksDB){
            LocalDate cur = check.getDate().toLocalDate();
            if(cur.isAfter(dateStart) && cur.isBefore(dateEnd)) checks.add(check);
        }
        invalidateCharts();
        if(checks.isEmpty()) return;
        buildPieCategories(checks);
        buildPieClassification(checks);
        buildChart(checks);
        buildChartClassification(checks);
    }

    private void invalidateCharts(){
        pieChart.setData(null);
        pieChartClassification.setData(null);
        barChart.setData(null);
        barChartClassification.setData(null);
        pieChart.invalidate();
        pieChartClassification.invalidate();
        barChart.invalidate();
        barChartClassification.invalidate();
    }

    private void buildPieCategories(ArrayList<Check> checks){
        HashMap<Integer, Float> categoriesTotalSpend = new HashMap<>();
        for(Check check : checks){
            for(Product product : check.getProducts()){
                categoriesTotalSpend.merge(product.getCategoryID(), product.getPrice() * product.getCount(), Float::sum);
            }
        }

        ArrayList<PieEntry> dataValues = new ArrayList<>();
        ArrayList<Integer> colors = new ArrayList<Integer>();
        for(Category category : DataBaseController.getCategories(this.getActivity())){
            if(!categoriesTotalSpend.containsKey(category.getId())) continue;
            dataValues.add(new PieEntry(categoriesTotalSpend.get(category.getId()), category.getName()));
            colors.add(category.getColor());
        }

        PieDataSet pieDataSet = new PieDataSet(dataValues, "");
        pieDataSet.setColors(colors);
        PieData pieData = new PieData(pieDataSet);
        pieChart.getLegend().setWordWrapEnabled(true);
        pieChart.setDescription(null);
        pieChart.setCenterText("Расходы по категориям");
        pieChart.setCenterTextSize(10);
        pieChart.animateX(1500);
        pieChart.setData(pieData);
        pieChart.invalidate();
    }

    private void buildPieClassification(ArrayList<Check> checks){
        HashMap<Integer, Float> classificationTotalSpend = new HashMap<>();
        for(Check check : checks){
            for(Product product : check.getProducts()){
                classificationTotalSpend.merge(product.getLiked(), product.getPrice() * product.getCount(), Float::sum);
            }
        }

        HashMap<Integer, String> className = new HashMap<Integer, String>(){{
                put(-1, "Второй необходимости");
                put(0, "Не определено");
                put(1, "Первой необходимости");
        }};
        ArrayList<PieEntry> dataValues = new ArrayList<>();
        ArrayList<Integer> colors = new ArrayList<Integer>();
        for(Map.Entry entry : classificationTotalSpend.entrySet()){
            dataValues.add(new PieEntry((Float) entry.getValue(), className.get(entry.getKey())));
            if((Integer) entry.getKey() == 0) colors.add(Color.GRAY);
            else if((Integer) entry.getKey() == 1) colors.add(Color.BLUE);
            else colors.add(Color.RED);
        }

        PieDataSet pieDataSet = new PieDataSet(dataValues, "");
        pieDataSet.setColors(colors);
        PieData pieData = new PieData(pieDataSet);
        pieChartClassification.getLegend().setWordWrapEnabled(true);
        pieChartClassification.setDescription(null);
        pieChartClassification.setCenterText("Расходы по необходимости");
        pieChartClassification.setCenterTextSize(10);
        pieChartClassification.animateX(1500);
        pieChartClassification.setData(pieData);
        pieChartClassification.invalidate();
    }

    private void buildChart(ArrayList<Check> checks){
        TreeMap<String, HashMap<Integer, Float>> data = new TreeMap<>();
        HashMap<Integer, Integer> index = new HashMap<>();
        int id = 0;
        for(Check check : checks){
            for(Product product : check.getProducts()){
                if(!data.containsKey(check.getDate().format(DateTimeFormatter.ofPattern("yyyy-MM"))))
                    data.put(check.getDate().format(DateTimeFormatter.ofPattern("yyyy-MM")), new HashMap<>());
                data.get(check.getDate().format(DateTimeFormatter.ofPattern("yyyy-MM")))
                        .merge(product.getCategoryID(), product.getPrice() * product.getCount(), Float::sum);
                if(!index.containsKey(product.getCategoryID())) index.put(product.getCategoryID(), id++);
            }
        }

        ArrayList<ArrayList<BarEntry>> dataValues = new ArrayList<ArrayList<BarEntry>>(index.size());
        for(int i = 0; i < index.size(); ++i) dataValues.add(new ArrayList<BarEntry>());
        ArrayList<Category> categories = new ArrayList<>(index.size());
        for(int i = 0; i < index.size(); ++i) categories.add(new Category());
        ArrayList<String> axisNames = new ArrayList<>();
        int x = 0;
        for(Map.Entry entry : data.entrySet()){
            for(Map.Entry el : data.get((String)entry.getKey()).entrySet()){
                dataValues.get(index.get(el.getKey())).add(new BarEntry(x, (Float) el.getValue()));
                categories.set(index.get(el.getKey()), DataBaseController.getCategoryByID(this.getActivity(), (Integer) el.getKey()));
            }
            axisNames.add((String) entry.getKey());
            x++;
        }

        BarData barData = new BarData();
        for(int i = 0; i < index.size(); ++i){
            BarDataSet barDataSet = new BarDataSet(dataValues.get(i), categories.get(i).getName());
            barDataSet.setColor(categories.get(i).getColor());
            barData.addDataSet(barDataSet);
        }
        barData.setBarWidth(0.10f);
        barChart.setData(barData);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(axisNames));
        xAxis.setCenterAxisLabels(true);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1);
        xAxis.setGranularityEnabled(true);

        barChart.setDescription(null);
        barChart.setDragEnabled(true);
        barChart.setVisibleXRangeMaximum(3);
        barChart.getXAxis().setDrawGridLines(false);
        barChart.getAxisLeft().setDrawGridLines(false);
        barChart.getLegend().setWordWrapEnabled(true);

        float barSpace = 0.08f;
        float groupSpace = 0.44f;

        barChart.getXAxis().setAxisMinimum(0);
        barChart.getXAxis().setAxisMaximum(0 + barChart.getBarData().getGroupWidth(groupSpace, barSpace) * axisNames.size());
        barChart.getAxisLeft().setAxisMinimum(0);

        if(barData.getDataSetCount() > 1) barChart.groupBars(0, groupSpace, barSpace);
        barChart.animateY(1500);
        barChart.invalidate();
    }

    private void buildChartClassification(ArrayList<Check> checks){
        TreeMap<String, HashMap<Integer, Float>> data = new TreeMap<>();
        HashMap<Integer, String> className = new HashMap<Integer, String>(){{
            put(-1, "Второй необходимости");
            put(0, "Не определено");
            put(1, "Первой необходимости");
        }};
        for(Check check : checks){
            for(Product product : check.getProducts()){
                if(!data.containsKey(check.getDate().format(DateTimeFormatter.ofPattern("yyyy-MM")))) data.put(check.getDate().format(DateTimeFormatter.ofPattern("yyyy-MM")), new HashMap<>());
                data.get(check.getDate().format(DateTimeFormatter.ofPattern("yyyy-MM"))).merge(product.getLiked(), product.getPrice() * product.getCount(), Float::sum);
            }
        }

        ArrayList<ArrayList<BarEntry>> dataValues = new ArrayList<ArrayList<BarEntry>>(3); for(int i = 0; i < 3; ++i) dataValues.add(new ArrayList<BarEntry>());
        ArrayList<String> axisNames = new ArrayList<>();
        int x = 0;
        for(Map.Entry entry : data.entrySet()){
            for(Map.Entry el : data.get((String)entry.getKey()).entrySet()){
                dataValues.get((Integer) el.getKey() + 1).add(new BarEntry(x, (Float) el.getValue()));
            }
            axisNames.add((String) entry.getKey());
            x++;
        }

        BarData barData = new BarData();
        for(int i = 0; i < 3; ++i){
            BarDataSet barDataSet = new BarDataSet(dataValues.get(i), className.get(i - 1));
            if(i - 1 == -1) barDataSet.setColor(Color.RED);
            if(i - 1 == 0) barDataSet.setColor(Color.GRAY);
            if(i - 1 == 1) barDataSet.setColor(Color.BLUE);
            barData.addDataSet(barDataSet);
        }
        barData.setBarWidth(0.10f);
        barChartClassification.setData(barData);

        XAxis xAxis = barChartClassification.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(axisNames));
        xAxis.setCenterAxisLabels(true);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1);
        xAxis.setGranularityEnabled(true);

        barChartClassification.setDescription(null);
        barChartClassification.setDragEnabled(true);
        barChartClassification.setVisibleXRangeMaximum(3);
        barChartClassification.getXAxis().setDrawGridLines(false);
        barChartClassification.getAxisLeft().setDrawGridLines(false);
        barChartClassification.getLegend().setWordWrapEnabled(true);


        float barSpace = 0.08f;
        float groupSpace = 0.44f;

        barChartClassification.getXAxis().setAxisMinimum(0);
        barChartClassification.getXAxis().setAxisMaximum(0 + barChartClassification.getBarData().getGroupWidth(groupSpace, barSpace) * axisNames.size());
        barChartClassification.getAxisLeft().setAxisMinimum(0);


        if(barData.getDataSetCount() > 1) barChartClassification.groupBars(0, groupSpace, barSpace);
        barChartClassification.animateY(1500);
        barChartClassification.invalidate();
    }

    private void popDatePicker(EditText txt_date){
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month++;
                yyear = String.valueOf(year);
                if(month < 10) mmonth = "0" + String.valueOf(month);
                else mmonth = String.valueOf(month);
                if(day < 10) dday = "0" + String.valueOf(day);
                else dday = String.valueOf(day);
                txt_date.setText(String.format("%s-%s-%s", dday, mmonth, yyear));
                if(txt_date.getId() == R.id.txt_dateStart) dateStart = LocalDate.parse(String.format("%s-%s-%s", yyear, mmonth, dday));
                else dateEnd = LocalDate.parse(String.format("%s-%s-%s", yyear, mmonth, dday));
                onResume();
            }
        };
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        datePickerDialog = new DatePickerDialog(this.getContext(), AlertDialog.THEME_HOLO_LIGHT, dateSetListener, year, month, day);
        datePickerDialog.show();
    }



}
