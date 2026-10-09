package com.awol.etechpro;

import android.app.Activity;
import android.app.Application;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ProcessLifecycleOwner;

import com.awol.etechpro.ui.SettingsActivity;
import com.awol.etechpro.util.UnityAdsManager;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.appopen.AppOpenAd;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.Date;

public class EtechProApp extends Application
        implements Application.ActivityLifecycleCallbacks, DefaultLifecycleObserver {

    private static final String TAG = "EtechProApp";
    private static final String APP_OPEN_AD_UNIT_ID =
            "ca-app-pub-9678232109126473/9555935084";

    private AppOpenAd appOpenAd = null;
    private boolean isLoadingAd = false;
    private boolean isShowingAd = false;
    private long loadTime = 0;
    private Activity currentActivity;

    @Override
    public void onCreate() {
        super.onCreate();

        SharedPreferences prefs = getSharedPreferences("etech_prefs", MODE_PRIVATE);

        if (!prefs.contains("dark_mode")) {
            prefs.edit().putBoolean("dark_mode", false).apply();
        }

        SettingsActivity.applyTheme(prefs);

        FirebaseMessaging.getInstance().subscribeToTopic("all")
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "Subscribed to FCM topic: all");
                    } else {
                        Log.e(TAG, "Failed to subscribe to FCM topic: all");
                    }
                });

        // Initialize AdMob SDK here (Application level) so it's ready before any
        // Activity tries to load ads. The App Open Ad is loaded inside the callback
        // to guarantee MobileAds is fully initialized first.
        MobileAds.initialize(this, initializationStatus -> {
            Log.d(TAG, "AdMob initialized");
            loadAppOpenAd();
        });

        // Initialize Unity Ads
        UnityAdsManager.initialize(this);
        UnityAdsManager.loadInterstitial();
        UnityAdsManager.loadRewarded();

        // Register lifecycle callbacks
        registerActivityLifecycleCallbacks(this);
        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);
    }

    // ── App Open Ad ───────────────────────────────────────────────

    private void loadAppOpenAd() {
        if (isLoadingAd || isAdAvailable()) return;
        isLoadingAd = true;
        AdRequest request = new AdRequest.Builder().build();
        AppOpenAd.load(this, APP_OPEN_AD_UNIT_ID, request,
                new AppOpenAd.AppOpenAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull AppOpenAd ad) {
                        appOpenAd = ad;
                        isLoadingAd = false;
                        loadTime = new Date().getTime();
                        Log.d(TAG, "App open ad loaded");
                    }
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError error) {
                        isLoadingAd = false;
                        Log.e(TAG, "App open ad failed: " + error.getMessage());
                    }
                });
    }

    private boolean isAdAvailable() {
        // Ad expires after 4 hours
        return appOpenAd != null &&
                (new Date().getTime() - loadTime) < (4 * 60 * 60 * 1000);
    }

    public void showAdIfAvailable(@NonNull Activity activity) {
        if (isShowingAd) return;
        if (!isAdAvailable()) {
            loadAppOpenAd();
            return;
        }
        appOpenAd.setFullScreenContentCallback(
                new com.google.android.gms.ads.FullScreenContentCallback() {
                    @Override
                    public void onAdDismissedFullScreenContent() {
                        appOpenAd = null;
                        isShowingAd = false;
                        loadAppOpenAd();
                    }
                    @Override
                    public void onAdFailedToShowFullScreenContent(
                            @NonNull com.google.android.gms.ads.AdError adError) {
                        isShowingAd = false;
                    }
                    @Override
                    public void onAdShowedFullScreenContent() {
                        isShowingAd = true;
                    }
                });
        isShowingAd = true;
        appOpenAd.show(activity);
    }

    // ── Lifecycle Observer ────────────────────────────────────────

    @Override
    public void onStart(@NonNull LifecycleOwner owner) {
        // App comes to foreground — show app open ad
        if (currentActivity != null) {
            showAdIfAvailable(currentActivity);
        }
    }

    // ── Activity Lifecycle Callbacks ──────────────────────────────

    @Override
    public void onActivityCreated(@NonNull Activity activity,
                                  @Nullable Bundle savedInstanceState) {}

    @Override
    public void onActivityStarted(@NonNull Activity activity) {
        currentActivity = activity;
    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {
        currentActivity = activity;
    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {}

    @Override
    public void onActivityStopped(@NonNull Activity activity) {}

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity,
                                            @NonNull Bundle outState) {}

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {
        if (currentActivity == activity) currentActivity = null;
    }
}
