package com.awol.etechpro.util;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.ViewGroup;

import com.startapp.sdk.ads.banner.Banner;
import com.startapp.sdk.adsbase.StartAppAd;
import com.startapp.sdk.adsbase.StartAppSDK;
import com.startapp.sdk.adsbase.adListeners.AdEventListener;
import com.startapp.sdk.adsbase.model.AdPreferences;

public class StartioAdsManager {

    private static final String TAG = "StartioAdsManager";

    // ── Initialize ────────────────────────────────────────────────

    public static void initialize(Context context) {
        // SDK is auto-initialized via manifest meta-data (APPLICATION_ID)
        // Just log confirmation
        Log.d(TAG, "Start.io SDK ready (App ID: 209129161)");
    }

    // ── Interstitial ──────────────────────────────────────────────

    public static void showInterstitial(Activity activity, Runnable onComplete) {
        StartAppAd startAppAd = new StartAppAd(activity);
        startAppAd.loadAd(new AdEventListener() {
            @Override
            public void onReceiveAd(com.startapp.sdk.adsbase.Ad ad) {
                startAppAd.showAd();
                if (onComplete != null) onComplete.run();
            }
            @Override
            public void onFailedToReceiveAd(com.startapp.sdk.adsbase.Ad ad) {
                Log.e(TAG, "Interstitial failed to load");
                if (onComplete != null) onComplete.run();
            }
        });
    }

    // ── Rewarded ──────────────────────────────────────────────────

    public static void showRewarded(Activity activity,
            Runnable onRewarded, Runnable onComplete) {
        StartAppAd startAppAd = new StartAppAd(activity);
        startAppAd.setVideoListener(() -> {
            // User completed watching the video — grant reward
            Log.d(TAG, "Rewarded video completed");
            if (onRewarded != null) onRewarded.run();
        });
        startAppAd.loadAd(StartAppAd.AdMode.REWARDED_VIDEO,
                new AdEventListener() {
                    @Override
                    public void onReceiveAd(com.startapp.sdk.adsbase.Ad ad) {
                        startAppAd.showAd();
                        if (onComplete != null) onComplete.run();
                    }
                    @Override
                    public void onFailedToReceiveAd(com.startapp.sdk.adsbase.Ad ad) {
                        Log.e(TAG, "Rewarded failed to load");
                        if (onComplete != null) onComplete.run();
                    }
                });
    }

    // ── Banner ────────────────────────────────────────────────────

    public static Banner createBanner(Activity activity, ViewGroup container) {
        Banner banner = new Banner(activity);
        container.addView(banner);
        return banner;
    }
}
