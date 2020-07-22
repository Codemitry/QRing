package com.codemitry.scanme;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;


public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;

    // Fragments
    private Fragment createQRFragment;
    private Fragment scanQRFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottomNavigation);

        bottomNavigationView.setOnNavigationItemSelectedListener((@NonNull MenuItem item) -> {
            switch (item.getItemId()) {
                case R.id.create:
                    startCreateQRFragment();
                    break;
                case R.id.scan:
                    startScanQRFragment();
                    break;
            }
            return true;
        });

        if (savedInstanceState == null) {
            showDefaultFragment();
        }


    }


    private void startScanQRFragment() {
        scanQRFragment = new ScanQRFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, scanQRFragment)
                .disallowAddToBackStack()
                .commit();
    }

    private void startCreateQRFragment() {
        createQRFragment = new CreateQRFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, createQRFragment)
                .disallowAddToBackStack()
                .commit();
    }

    private void showDefaultFragment() {
        scanQRFragment = new ScanQRFragment();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.container, scanQRFragment)
                .disallowAddToBackStack()
                .commit();
    }
}