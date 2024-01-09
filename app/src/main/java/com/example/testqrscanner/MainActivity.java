package com.example.testqrscanner;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity implements NavigationBarView.OnItemSelectedListener {

    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ToastMessage.setMain(this);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(R.id.stats);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.stats:
                getSupportFragmentManager().beginTransaction().replace(R.id.layout_scene, new StatisticMainActivity()).commit();
                return true;

            case R.id.scanner:
                getSupportFragmentManager().beginTransaction().replace(R.id.layout_scene, new ScannerActivity()).commit();
                return true;

            case R.id.archive:
                getSupportFragmentManager().beginTransaction().replace(R.id.layout_scene, new ArchiveActivity()).commit();
                return true;

            case R.id.settings:
                getSupportFragmentManager().beginTransaction().replace(R.id.layout_scene, new SettingsActivity()).commit();
                return true;
        }
        return false;
    }
}
