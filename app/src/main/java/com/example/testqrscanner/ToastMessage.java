package com.example.testqrscanner;

import android.app.Activity;
import android.widget.Toast;

public final class ToastMessage {
    private static Activity main = null;

    public static void show(String msg){
        main.runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(main, msg, Toast.LENGTH_LONG).show();
            }
        });
    }

    public static void setMain(Activity main) {
        ToastMessage.main = main;
    }
}
