package com.awol.etechpro;

import android.app.Application;
import android.content.SharedPreferences;

import com.awol.etechpro.ui.SettingsActivity;

public class EtechProApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        SharedPreferences prefs = getSharedPreferences("etech_prefs", MODE_PRIVATE);

        // Set dark mode as default on first launch
        if (!prefs.contains("dark_mode")) {
            prefs.edit().putBoolean("dark_mode", true).apply();
        }

        // Apply saved theme
        SettingsActivity.applyTheme(prefs);
    }
}
