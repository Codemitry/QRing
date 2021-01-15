package com.codemitry.scanme.ui;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.TooltipCompat;
import androidx.lifecycle.ViewModelProvider;

import com.codemitry.scanme.OnHistoryClickListener;
import com.codemitry.scanme.R;
import com.codemitry.scanme.history.HistoryActionsManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;


public class MainActivity extends AppCompatActivity implements OnHistoryClickListener {

    private BottomNavigationView bottomNavigationView;

    // Fragments
    private CreateQRFragment createQRFragment;
    private ScanQRFragment scanQRFragment;
    private HistoryFragment historyFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottomNavigation);

        // Убирает всплывающие подсказки при долгом удержании item
        int len = bottomNavigationView.getMenu().size();
        for (int i = 0; i < len; i++) {
            TooltipCompat.setTooltipText(findViewById(bottomNavigationView.getMenu().getItem(i).getItemId()), null);
        }

        bottomNavigationView.setOnNavigationItemSelectedListener((@NonNull MenuItem item) -> {
            int id = item.getItemId();

            if (id == R.id.create)
                startCreateQRFragment();
            else if (id == R.id.scan)
                startScanQRFragment();

            return true;
        });

        if (savedInstanceState == null) {
            showDefaultFragment();
        }

        HistoryActionsManager historyActionsManager = new ViewModelProvider(this).get(HistoryActionsManager.class);
        historyActionsManager.setPath(getFilesDir());
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

    @Override
    public void onBackPressed() {
        // Если отображается фрагмент с историей, то нужно вернуть bottom navigation
        if (getSupportFragmentManager().findFragmentByTag(HistoryFragment.class.getSimpleName()) != null) {
            showBottomNavigation();
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