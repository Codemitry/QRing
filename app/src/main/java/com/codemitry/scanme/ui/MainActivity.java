package com.codemitry.scanme.ui;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.codemitry.scanme.OnHistoryClickListener;
import com.codemitry.scanme.R;
import com.codemitry.scanme.history.HistoryActionsManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.mlkit.vision.barcode.Barcode;

//import com.google.android.gms.vision.barcode.Barcode;


public class MainActivity extends AppCompatActivity implements OnHistoryClickListener {

    private BottomNavigationView bottomNavigationView;

    // Fragments
    private CreateQRFragment createQRFragment;
    private ScanQRFragment scanQRFragment;
    private HistoryFragment historyFragment;
    private BarcodeResultFragment barcodeResultFragment;

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

        HistoryActionsManager historyActionsManager = new ViewModelProvider(this).get(HistoryActionsManager.class);
        historyActionsManager.setPath(getFilesDir());
//        historyActionsManager.getHistoryActions();
//        historyActionsManager.addHistoryAction(new HistoryAction(HistoryAction.Actions.SCAN, new com.google.android.gms.vision.barcode.Barcode(0, "Hello world", null, 0, null, null, null, null, null, null, null, null, null, null, null, false)));
    }


    private void startScanQRFragment() {
        scanQRFragment = new ScanQRFragment();
        scanQRFragment.setOnHistoryClickListener(this);
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
        scanQRFragment.setOnHistoryClickListener(this);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.container, scanQRFragment)
                .disallowAddToBackStack()
                .commit();
    }

    @Override
    public void onHistoryClick() {
        startHistoryFragment();
    }

    private void startHistoryFragment() {
        hideBottomNavigation();

        historyFragment = new HistoryFragment();
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.animator.slide_down_in, R.animator.slide_up_out, R.animator.slide_down_in, R.animator.slide_up_out)
                .replace(R.id.container, historyFragment, historyFragment.getClass().getSimpleName())
                .addToBackStack(historyFragment.getClass().getSimpleName())
                .commit();
    }

    private void startBarcodeResultFragment(Barcode barcode) {
        barcodeResultFragment = new BarcodeResultFragment(com.codemitry.scanme.barcode.Barcode.getBarcode(barcode));
        getSupportFragmentManager().beginTransaction()
                .add(R.id.container, barcodeResultFragment)
                .addToBackStack(barcodeResultFragment.getClass().getSimpleName())
                .commit();
    }

    @Override
    public void onBackPressed() {
        // Если отображается фрагмент с историей, то нужно вернуть bottom navigation
        if (getSupportFragmentManager().findFragmentByTag(HistoryFragment.class.getSimpleName()) != null) {
            showBottomNavigation();
        } else if (getSupportFragmentManager().findFragmentByTag(BarcodeResultFragment.class.getSimpleName()) != null) {
            System.out.println("Yeah!!! Visible!!");
        }
        super.onBackPressed();
    }

    private void hideBottomNavigation() {
        bottomNavigationView.animate().translationYBy(500).setInterpolator(new AccelerateDecelerateInterpolator()).withEndAction(() -> {
            bottomNavigationView.setVisibility(View.GONE);
        }).start();
    }

    private void showBottomNavigation() {
        bottomNavigationView.setVisibility(View.VISIBLE);
        bottomNavigationView.animate().translationYBy(-500).setInterpolator(new AccelerateDecelerateInterpolator()).start();
    }
}