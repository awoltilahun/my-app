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

    private static final String PREFS_NAME = "etech_prefs";
    private static final String KEY_DARK   = "dark_mode";

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
            // Save preference
            prefs.edit().putBoolean(KEY_DARK, isChecked).apply();
            // Apply theme
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
            // Restart app from MainActivity to fully apply theme
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });

        LinearLayout btnAbout = findViewById(R.id.btn_about);
        btnAbout.setOnClickListener(v ->
            startActivity(new Intent(this, AboutActivity.class)));

        LinearLayout btnPrivacy = findViewById(R.id.btn_privacy);
        btnPrivacy.setOnClickListener(v ->
            startActivity(new Intent(this, PrivacyPolicyActivity.class)));

        LinearLayout btnContact = findViewById(R.id.btn_contact);
        btnContact.setOnClickListener(v ->
            startActivity(new Intent(this, ContactActivity.class)));

        // Publisher Information expand/collapse
        LinearLayout btnPublisher = findViewById(R.id.btn_publisher);
        LinearLayout layoutPublisherInfo = findViewById(R.id.layout_publisher_info);
        android.widget.ImageView ivArrow = findViewById(R.id.iv_publisher_arrow);

        btnPublisher.setOnClickListener(v -> {
            if (layoutPublisherInfo.getVisibility() == android.view.View.GONE) {
                layoutPublisherInfo.setVisibility(android.view.View.VISIBLE);
                ivArrow.setImageResource(android.R.drawable.arrow_up_float);
            } else {
                layoutPublisherInfo.setVisibility(android.view.View.GONE);
                ivArrow.setImageResource(android.R.drawable.arrow_down_float);
            }
        });
    }

    public static void applyTheme(SharedPreferences prefs) {
        // Default is true (dark mode) — first launch will be dark
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
