package com.awol.etechpro.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;

import com.awol.etechpro.R;
import com.bumptech.glide.Glide;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.File;

public class SettingsActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "etech_prefs";
    private static final String KEY_DARK   = "dark_mode";
    private static final String KEY_NOTIF  = "notifications_enabled";
    private static final int CURRENT_VERSION = 1;

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

        // ── Dark Mode ─────────────────────────────────────────────
        boolean isDark = prefs.getBoolean(KEY_DARK, false);
        SwitchCompat switchDarkMode = findViewById(R.id.switch_dark_mode);
        switchDarkMode.setChecked(isDark);
        switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefs.edit().putBoolean(KEY_DARK, isChecked).apply();
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });

        // ── Notifications ─────────────────────────────────────────
        boolean notifEnabled = prefs.getBoolean(KEY_NOTIF, true);
        SwitchCompat switchNotif = findViewById(R.id.switch_notifications);
        if (switchNotif != null) {
            switchNotif.setChecked(notifEnabled);
            switchNotif.setOnCheckedChangeListener((buttonView, isChecked) -> {
                prefs.edit().putBoolean(KEY_NOTIF, isChecked).apply();
                if (isChecked) {
                    FirebaseMessaging.getInstance().subscribeToTopic("all")
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful())
                                    Toast.makeText(this, "Notifications enabled", Toast.LENGTH_SHORT).show();
                            });
                } else {
                    FirebaseMessaging.getInstance().unsubscribeFromTopic("all")
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful())
                                    Toast.makeText(this, "Notifications disabled", Toast.LENGTH_SHORT).show();
                            });
                }
            });
        }

        // ── Clear Cache ───────────────────────────────────────────
        TextView tvCacheSize = findViewById(R.id.tv_cache_size);
        LinearLayout btnClearCache = findViewById(R.id.btn_clear_cache);

        if (tvCacheSize != null) {
            tvCacheSize.setText(formatSize(getCacheSize()));
        }

        if (btnClearCache != null) {
            btnClearCache.setOnClickListener(v -> {
                new AlertDialog.Builder(this)
                        .setTitle("Clear Cache")
                        .setMessage("This will clear all cached images and data. Continue?")
                        .setPositiveButton("Clear", (dialog, which) -> {
                            deleteCache();
                            new Thread(() -> Glide.get(this).clearDiskCache()).start();
                            Glide.get(this).clearMemory();
                            if (tvCacheSize != null) tvCacheSize.setText("0 KB");
                            Toast.makeText(this, "Cache cleared successfully!", Toast.LENGTH_SHORT).show();
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            });
        }

        // ── Navigation ────────────────────────────────────────────
        LinearLayout btnAbout = findViewById(R.id.btn_about);
        btnAbout.setOnClickListener(v ->
                startActivity(new Intent(this, AboutActivity.class)));

        LinearLayout btnPrivacy = findViewById(R.id.btn_privacy);
        btnPrivacy.setOnClickListener(v ->
                startActivity(new Intent(this, PrivacyPolicyActivity.class)));

        LinearLayout btnContact = findViewById(R.id.btn_contact);
        btnContact.setOnClickListener(v ->
                startActivity(new Intent(this, ContactActivity.class)));

        // ── Publisher Info expand/collapse ────────────────────────
        LinearLayout btnPublisher = findViewById(R.id.btn_publisher);
        LinearLayout layoutPublisherInfo = findViewById(R.id.layout_publisher_info);
        android.widget.ImageView ivArrow = findViewById(R.id.iv_publisher_arrow);

        if (btnPublisher != null && layoutPublisherInfo != null) {
            btnPublisher.setOnClickListener(v -> {
                if (layoutPublisherInfo.getVisibility() == android.view.View.GONE) {
                    layoutPublisherInfo.setVisibility(android.view.View.VISIBLE);
                    if (ivArrow != null) ivArrow.setImageResource(android.R.drawable.arrow_up_float);
                } else {
                    layoutPublisherInfo.setVisibility(android.view.View.GONE);
                    if (ivArrow != null) ivArrow.setImageResource(android.R.drawable.arrow_down_float);
                }
            });
        }

        // ── App Update Checker ────────────────────────────────────
        checkForUpdate();
    }

    // ── Cache Methods ─────────────────────────────────────────────

    private long getCacheSize() {
        long size = 0;
        try {
            size += getDirSize(getCacheDir());
            if (getExternalCacheDir() != null)
                size += getDirSize(getExternalCacheDir());
        } catch (Exception e) { /* ignore */ }
        return size;
    }

    private long getDirSize(File dir) {
        if (dir == null) return 0;
        long size = 0;
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                size += file.isDirectory() ? getDirSize(file) : file.length();
            }
        }
        return size;
    }

    private String formatSize(long bytes) {
        if (bytes < 1024) return bytes + " B";
        else if (bytes < 1024 * 1024) return (bytes / 1024) + " KB";
        else return String.format("%.1f MB", bytes / (1024.0 * 1024.0));
    }

    private void deleteCache() {
        try {
            deleteDir(getCacheDir());
            if (getExternalCacheDir() != null)
                deleteDir(getExternalCacheDir());
        } catch (Exception e) { /* ignore */ }
    }

    private boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            File[] children = dir.listFiles();
            if (children != null) {
                for (File child : children) deleteDir(child);
            }
        }
        return dir != null && dir.delete();
    }

    // ── Update Checker ────────────────────────────────────────────

    private void checkForUpdate() {
        try {
            int installedVersion = getPackageManager()
                    .getPackageInfo(getPackageName(), 0).versionCode;
            if (installedVersion < CURRENT_VERSION) {
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("market://details?id=" + getPackageName())));
                } catch (Exception e) {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName())));
                }
            }
        } catch (Exception e) { /* ignore */ }
    }

    // ── Theme ─────────────────────────────────────────────────────

    public static void applyTheme(SharedPreferences prefs) {
        boolean isDark = prefs.getBoolean(KEY_DARK, false);
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
