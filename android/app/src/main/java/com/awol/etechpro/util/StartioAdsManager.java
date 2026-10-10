package com.awol.etechpro.util;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.ViewGroup;

import com.startapp.sdk.ads.banner.Banner;
import com.startapp.sdk.adsbase.StartAppAd;
import com.startapp.sdk.adsbase.StartAppSDK;
import com.startapp.sdk.adsbase.VideoListener;

public class StartioAdsManager {

    private static final String TAG = "StartioAdsManager";
    private static final String APP_ID = "209129161";

    // ── Initialize ────────────────────────────────────────────────

    public static void initialize(Context context) {
        // Explicitly initialize with App ID to avoid "undefined" issue
        StartAppSDK.init(context, APP_ID, false);
        StartAppSDK.setTestAdsEnabled(false);
        Log.d(TAG, "Start.io SDK initialized with App ID: " + APP_ID);
    }

    // ── Interstitial ──────────────────────────────────────────────

    public static void showInterstitial(Activity activity, Runnable onComplete) {
        try {
            StartAppAd.showAd(activity);
            Log.d(TAG, "Interstitial shown");
        } catch (Exception e) {
            Log.e(TAG, "Interstitial error: " + e.getMessage());
        }
        if (onComplete != null) onComplete.run();
    }

    // ── Rewarded ──────────────────────────────────────────────────

    public static void showRewarded(Activity activity,
            Runnable onRewarded, Runnable onComplete) {
        try {
            StartAppAd startAppAd = new StartAppAd(activity);
            startAppAd.setVideoListener(new VideoListener() {
                @Override
                public void onVideoCompleted() {
                    Log.d(TAG, "Rewarded video completed");
                    if (onRewarded != null) onRewarded.run();
                }
            });
            startAppAd.loadAd(StartAppAd.AdMode.REWARDED_VIDEO);
            startAppAd.showAd();
        } catch (Exception e) {
            Log.e(TAG, "Rewarded error: " + e.getMessage());
        }
        if (onComplete != null) onComplete.run();
    }

    // ── Banner ────────────────────────────────────────────────────

    public static Banner createBanner(Activity activity, ViewGroup container) {
        try {
            Banner banner = new Banner(activity);
            container.addView(banner);
            return banner;
        } catch (Exception e) {
            Log.e(TAG, "Banner error: " + e.getMessage());
            return null;
        }
    }
}
