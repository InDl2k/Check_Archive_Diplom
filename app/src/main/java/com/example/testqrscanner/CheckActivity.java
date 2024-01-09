package com.example.testqrscanner;

import android.os.Bundle;
import android.webkit.WebView;

import androidx.appcompat.app.AppCompatActivity;


public class CheckActivity extends AppCompatActivity {

    private WebView webView;
    private String html;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check);
        webView = (WebView) findViewById(R.id.web_view);
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDisplayZoomControls(false);
        int id = getIntent().getIntExtra("id", 0);
        html = DataBaseController.getCheckHTML(this, id);
        webView.loadDataWithBaseURL("file:///android_asset/css/", html, "text/html", "UTF-8", null);
    }

}
