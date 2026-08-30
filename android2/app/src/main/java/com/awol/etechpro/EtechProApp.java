package com.awol.etechpro;

import android.app.Application;
import android.content.SharedPreferences;

import com.awol.etechpro.ui.SettingsActivity;

public class EtechProApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        // Restore dark/light mode preference every time app starts
        SharedPreferences prefs = getSharedPreferences("etech_prefs", MODE_PRIVATE);
        SettingsActivity.applyTheme(prefs);
    }
}
