package com.team.testscanner

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate

class TestScannerApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    }
}
