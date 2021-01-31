package com.codemitry.scanme.ui

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.TooltipCompat
import com.codemitry.scanme.OnHistoryClickListener
import com.codemitry.scanme.R
import com.codemitry.scanme.history.HistoryActionsManager
import com.codemitry.scanme.ui.create.CreateQRFragment
import com.codemitry.scanme.ui.scan.REQUEST_CODE_CAMERA
import com.codemitry.scanme.ui.scan.ScanFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity(), OnHistoryClickListener {

    private var bottomNavigationView: BottomNavigationView? = null

    // fragments
    private var createQRFragment: CreateQRFragment? = null
    private var scanQRFragment: ScanFragment? = null
    private var historyFragment: HistoryFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottomNavigationView = findViewById(R.id.bottomNavigation)

        // Убирает всплывающие подсказки при долгом удержании item
        bottomNavigationView?.menu?.size()?.let { len ->
            for (i in 0 until len) {
                if (bottomNavigationView != null)
                    TooltipCompat.setTooltipText(findViewById(bottomNavigationView!!.menu.getItem(i).itemId), null)
            }

            bottomNavigationView?.setOnNavigationItemSelectedListener { item ->
                when (item.itemId) {
                    R.id.create -> startCreateQRFragment()
                    R.id.scan -> startScanQRFragment()
                }

                true
            }

            if (savedInstanceState == null)
                showDefaultFragment()

            val historyActionsManager: HistoryActionsManager by viewModels()
            historyActionsManager.setPath(filesDir)
        }
    }

    private fun startScanQRFragment() {
        scanQRFragment = ScanFragment().apply {
            onHistoryClickListener = this@MainActivity
        }.also { scanQRFragment ->
            supportFragmentManager.beginTransaction()
                    .replace(R.id.container, scanQRFragment)
                    .disallowAddToBackStack()
                    .commit()
        }

    }

    private fun startCreateQRFragment() {
        createQRFragment = CreateQRFragment().apply {
            setOnHistoryClickListener(this@MainActivity)
        }.also { createQRFragment ->
            supportFragmentManager.beginTransaction()
                    .replace(R.id.container, createQRFragment)
                    .disallowAddToBackStack()
                    .commit()
        }
    }


    private fun showDefaultFragment() {
        scanQRFragment = ScanFragment().apply {
            onHistoryClickListener = this@MainActivity
        }.also { scanQRFragment ->
            supportFragmentManager.beginTransaction()
                    .add(R.id.container, scanQRFragment)
                    .disallowAddToBackStack()
                    .commit()
        }
    }

    override fun onHistoryClick() {
        startHistoryFragment()
    }


    private fun startHistoryFragment() {
        hideBottomNavigation()

        historyFragment = HistoryFragment().also { historyFragment ->
            supportFragmentManager.beginTransaction()
                    .setCustomAnimations(R.animator.slide_down_in, R.animator.slide_up_out, R.animator.slide_down_in, R.animator.slide_up_out)
                    .replace(R.id.container, historyFragment, HistoryFragment::class.simpleName)
                    .addToBackStack(HistoryFragment::class.simpleName)
                    .commit()
        }

    }

    override fun onBackPressed() {
        // Если отображается фрагмент с историей, то нужно вернуть bottom navigation
        if (supportFragmentManager.findFragmentByTag(HistoryFragment::class.simpleName) != null) {
            showBottomNavigation()
        }
        super.onBackPressed()
    }


    private fun hideBottomNavigation() {
        bottomNavigationView?.let {
            it.animate().translationYBy(500F).setInterpolator(AccelerateDecelerateInterpolator()).withEndAction {
                it.visibility = View.GONE
            }.start()
        }
    }

    private fun showBottomNavigation() {
        bottomNavigationView?.let {
            it.visibility = View.VISIBLE
            it.animate().translationYBy(-500F).setInterpolator(AccelerateDecelerateInterpolator()).start()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQUEST_CODE_CAMERA) {
            for (permission in grantResults)
                if (permission != PackageManager.PERMISSION_GRANTED)
                    return
            startScanQRFragment()
        }
    }
}