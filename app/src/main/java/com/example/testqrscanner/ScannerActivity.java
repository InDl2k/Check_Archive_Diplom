package com.example.testqrscanner;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.codescanner.GmsBarcodeScanner;
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions;
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

import yuku.ambilwarna.AmbilWarnaDialog;

public class ScannerActivity extends Fragment {

    private EditText txt_date;
    private EditText txt_time;
    private EditText txt_sum;
    private EditText txt_fn;
    private EditText txt_fd;
    private EditText txt_fp;
    private DatePickerDialog datePickerDialog;
    private int hhour;
    private int mminute;
    private String hh;
    private String mm;
    private String yyear;
    private String mmonth;
    private String dday;

    ScannerActivity(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_scanner, container, false);
        CardView cardScanner = view.findViewById(R.id.card_qrScanner);
        CardView cardSelfInput = view.findViewById(R.id.card_selfInput);
        cardScanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scanCode(view);
            }
        });
        cardSelfInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addCategory(view);
            }
        });
        return view;
    }

    private void addCategory(View view){
        Dialog dialog = new Dialog(this.getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.activity_dialog_check);
        Button btn_addCheckInput = (Button) dialog.findViewById(R.id.btn_addCheckInput);
        txt_date = (EditText) dialog.findViewById(R.id.txt_datePick);
        txt_time = (EditText) dialog.findViewById(R.id.txt_timePick);
        txt_sum = (EditText) dialog.findViewById(R.id.txt_sumPick);
        txt_fn = (EditText) dialog.findViewById(R.id.txt_fnPick);
        txt_fd = (EditText) dialog.findViewById(R.id.txt_fdPick);
        txt_fp = (EditText) dialog.findViewById(R.id.txt_fpPick);
        txt_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popTimePicker(view);
            }
        });
        txt_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { popDatePicker(view); }
        });
        btn_addCheckInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inputCode(view);
            }
        });
        dialog.show();
    }

    public void scanCode(View view){
        GmsBarcodeScannerOptions options = new GmsBarcodeScannerOptions.Builder()
                .setBarcodeFormats(
                        Barcode.FORMAT_QR_CODE,
                        Barcode.FORMAT_AZTEC)
                .build();
        GmsBarcodeScanner scanner = GmsBarcodeScanning.getClient(this.getContext(), options);
        Activity activity = this.getActivity();
        scanner.startScan().addOnSuccessListener(
                barcode -> {
                    asyncGetCheck(this.getActivity(), barcode.getRawValue());
                }
        );
    }

    private static void asyncGetCheck(Activity main,String qrraw){
        new Thread(new Runnable() {
            @Override
            public void run() {
                RequestController.getCheck(main, qrraw);
            }
        }).start();
    }

    public void inputCode(View view){
        if(validateInputFields()){
            String qrraw = String.format("t=%sT%s&s=%s&fn=%s&i=%s&fp=%s&n=1", yyear + mmonth + dday, hh + mm,
                    txt_sum.getText().toString(), txt_fn.getText().toString(),
                    txt_fd.getText().toString(), txt_fp.getText().toString());
            asyncGetCheck(this.getActivity(), qrraw);
        }
    }

    private boolean validateInputFields(){
        ArrayList<EditText> txts = new ArrayList<>(
                Arrays.asList(  txt_date,
                                txt_time,
                                txt_sum,
                                txt_fn,
                                txt_fd,
                                txt_fp));
        ArrayList<Integer> need = new ArrayList<>(
                Arrays.asList(
                                0,
                                0,
                                0,
                                16,
                                5,
                                10
                )
        );
        for(int i = 0; i < txts.size(); ++i){
            if(need.get(i) == 0){
                if(txts.get(i).getText().length() == 0){
                    txts.get(i).setError("Пустое поле!");
                    return false;
                }
                else txts.get(i).setError(null);
            }
            else{
                if(txts.get(i).getText().length() != need.get(i)){
                    txts.get(i).setError(String.format("Поле должно содержать: %d цифр!", need.get(i)));
                    return false;
                }
                else txts.get(i).setError(null);
            }
        }
        return true;
    }

    public void popTimePicker(View view) {
        TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                hhour = hour;
                mminute = minute;
                if(hour < 10) hh = "0" + String.valueOf(hour);
                else hh = String.valueOf(hour);
                if(minute < 10) mm = "0" + String.valueOf(minute);
                else mm = String.valueOf(minute);

                txt_time.setText(String.format("%s:%s", hh, mm));
            }
        };
        TimePickerDialog timePickerDialog = new TimePickerDialog(this.getContext(), onTimeSetListener, hhour, mminute, true);
        timePickerDialog.show();
    }

    public void popDatePicker(View view){
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month++;
                yyear = String.valueOf(year);
                if(month < 10) mmonth = "0" + String.valueOf(month);
                else mmonth = String.valueOf(month);
                if(day < 10) dday = "0" + String.valueOf(day);
                else dday = String.valueOf(day);
                txt_date.setText(String.format("%s/%s/%s", dday, mmonth, yyear));
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