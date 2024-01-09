package com.example.testqrscanner;


import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;


public class LineCharXAxisValueFormatter extends IndexAxisValueFormatter {

    private ArrayList<LocalDateTime> data;

    LineCharXAxisValueFormatter(ArrayList<LocalDateTime> date){
        this.data = date;
    }


    @Override
    public String getFormattedValue(float value) {
        if(value != (int)value || (int)value >= data.size() || (int)value < 0) return "";
        return data.get((int)value).format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
    }
}
