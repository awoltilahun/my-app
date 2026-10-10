package com.awol.etechpro.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;

import com.awol.etechpro.R;
import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.firebase.messaging.FirebaseMessaging;
import com.awol.etechpro.util.UnityAdsManager;

import java.io.File;

public class SettingsActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "etech_prefs";
    private static final String KEY_DARK   = "dark_mode";
    private static final String KEY_NOTIF  = "notifications_enabled";
    private static final String KEY_AD_FREE_UNTIL = "ad_free_until";
    private static final int CURRENT_VERSION = 14;

    private RewardedAd rewardedAd;
    private TextView tvAdStatus;

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
        LinearLayout btnClearCacheNav = findViewById(R.id.btn_clear_cache_nav);
        android.widget.TextView tvCacheSizeNav = findViewById(R.id.tv_cache_size_nav);
        if (tvCacheSizeNav != null) tvCacheSizeNav.setText(formatSize(getCacheSize()));
        if (btnClearCacheNav != null) {
            btnClearCacheNav.setOnClickListener(v -> {
                // Open dedicated ClearCacheActivity so user sees size and confirms
                startActivity(new Intent(this, ClearCacheActivity.class));
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

        // ── Rewarded Ad — Remove Banner ───────────────────────────
        tvAdStatus = findViewById(R.id.tv_ad_status);
        LinearLayout btnWatchAd = findViewById(R.id.btn_watch_ad);
        updateAdStatus(prefs);
        loadRewardedAd();
        if (btnWatchAd != null) {
            btnWatchAd.setOnClickListener(v -> showRewardedAd(prefs));
        }
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

    // ── Rewarded Ad Methods ───────────────────────────────────────

    private void loadRewardedAd() {
        AdRequest adRequest = new AdRequest.Builder().build();
        RewardedAd.load(this,
                "ca-app-pub-9678232109126473/4544290713",
                adRequest,
                new RewardedAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull RewardedAd ad) {
                        rewardedAd = ad;
                    }
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError error) {
                        rewardedAd = null;
                    }
                });
    }

    private void showRewardedAd(SharedPreferences prefs) {
        // Check if already ad-free
        long adFreeUntil = prefs.getLong(KEY_AD_FREE_UNTIL, 0);
        if (System.currentTimeMillis() < adFreeUntil) {
            long remaining = (adFreeUntil - System.currentTimeMillis()) / 60000;
            Toast.makeText(this,
                "Banner already removed! " + remaining + " minutes remaining.",
                Toast.LENGTH_SHORT).show();
            return;
        }

        if (rewardedAd == null) {
            Toast.makeText(this, "Ad not ready yet. Please try again.", Toast.LENGTH_SHORT).show();
            loadRewardedAd();
            // Try Unity Ads rewarded as fallback
            UnityAdsManager.showRewarded(this, () -> {
                long oneHourFromNow = System.currentTimeMillis() + (60 * 60 * 1000);
                prefs.edit().putLong(KEY_AD_FREE_UNTIL, oneHourFromNow).apply();
                Toast.makeText(this,
                    "🎉 Banner removed for 1 hour! Thank you.",
                    Toast.LENGTH_LONG).show();
                updateAdStatus(prefs);
            }, null);
            return;
        }

        rewardedAd.show(this, rewardItem -> {
            // User watched the ad — remove banner for 1 hour
            long oneHourFromNow = System.currentTimeMillis() + (60 * 60 * 1000);
            prefs.edit().putLong(KEY_AD_FREE_UNTIL, oneHourFromNow).apply();
            Toast.makeText(this,
                "🎉 Banner removed for 1 hour! Thank you.",
                Toast.LENGTH_LONG).show();
            updateAdStatus(prefs);
            rewardedAd = null;
            loadRewardedAd();
        });
    }

    private void updateAdStatus(SharedPreferences prefs) {
        if (tvAdStatus == null) return;
        long adFreeUntil = prefs.getLong(KEY_AD_FREE_UNTIL, 0);
        if (System.currentTimeMillis() < adFreeUntil) {
            long remaining = (adFreeUntil - System.currentTimeMillis()) / 60000;
            tvAdStatus.setText("✅ Banner removed! " + remaining + " minutes remaining");
        } else {
            tvAdStatus.setText("Watch a short ad to remove the banner for 1 hour");
        }
    }

    // ── Update Checker ────────────────────────────────────────────

    private void checkForUpdate() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), 0);
            long installedVersion;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                installedVersion = info.getLongVersionCode();
            } else {
                installedVersion = info.versionCode;
            }
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
    protected void onResume() {
        super.onResume();
        // Refresh cache size display when returning from ClearCacheActivity
        android.widget.TextView tvCacheSizeNav = findViewById(R.id.tv_cache_size_nav);
        if (tvCacheSizeNav != null) tvCacheSizeNav.setText(formatSize(getCacheSize()));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) { finish(); return true; }
        return super.onOptionsItemSelected(item);
    }
}
