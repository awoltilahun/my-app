package com.awol.etechpro;

import android.app.Application;
import android.content.SharedPreferences;
import android.util.Log;

import com.awol.etechpro.ui.SettingsActivity;
import com.google.firebase.messaging.FirebaseMessaging;

public class EtechProApp extends Application {

    private static final String TAG = "EtechProApp";

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

        // Subscribe to FCM topic to receive broadcast notifications
        FirebaseMessaging.getInstance().subscribeToTopic("all")
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "Subscribed to FCM topic: all");
                    } else {
                        Log.e(TAG, "Failed to subscribe to FCM topic: all");
                    }
                });
    }
}
