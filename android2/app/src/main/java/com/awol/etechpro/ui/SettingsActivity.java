package com.awol.etechpro.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.LinearLayout;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;

import com.awol.etechpro.R;

public class SettingsActivity extends AppCompatActivity {

    private static final String PREFS_NAME  = "etech_prefs";
    private static final String KEY_DARK    = "dark_mode";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setTitle("Settings");
        }

        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean isDark = prefs.getBoolean(KEY_DARK, true);

        SwitchCompat switchDarkMode = findViewById(R.id.switch_dark_mode);
        switchDarkMode.setChecked(isDark);

        switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefs.edit().putBoolean(KEY_DARK, isChecked).apply();
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
        });

        // About
        LinearLayout btnAbout = findViewById(R.id.btn_about);
        btnAbout.setOnClickListener(v ->
            startActivity(new Intent(this, AboutActivity.class)));

        // Privacy Policy
        LinearLayout btnPrivacy = findViewById(R.id.btn_privacy);
        btnPrivacy.setOnClickListener(v ->
            startActivity(new Intent(this, PrivacyPolicyActivity.class)));

        // Contact
        LinearLayout btnContact = findViewById(R.id.btn_contact);
        btnContact.setOnClickListener(v ->
            startActivity(new Intent(this, ContactActivity.class)));
    }

    // Called from Application class on startup to restore dark mode preference
    public static void applyTheme(SharedPreferences prefs) {
        boolean isDark = prefs.getBoolean(KEY_DARK, true);
        if (isDark) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) { finish(); return true; }
        return super.onOptionsItemSelected(item);
    }
}
