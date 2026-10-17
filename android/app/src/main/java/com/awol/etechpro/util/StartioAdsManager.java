package com.awol.etechpro.util;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.ViewGroup;

import com.startapp.sdk.ads.banner.Banner;
import com.startapp.sdk.adsbase.StartAppAd;
import com.startapp.sdk.adsbase.StartAppSDK;
import com.startapp.sdk.adsbase.VideoListener;

/**
 * Start.io (formerly StartApp) SDK wrapper.
 *
 * SDK version: 5.3.1 (pinned in build.gradle)
 * App ID:      209129161
 *
 * Initialization strategy:
 *  - StartAppInitProvider ContentProvider is removed via tools:node="remove"
 *    in AndroidManifest.xml so the SDK CANNOT auto-initialize before
 *    Application.onCreate() runs.
 *  - StartAppSDK.init() is called explicitly in EtechProApp.onCreate(),
 *    which is the first code that runs after all ContentProviders complete.
 *  - The `initialized` flag prevents any ad call before init completes.
 */
public class StartioAdsManager {

    private static final String TAG = "StartioAdsManager";
    public  static final String APP_ID = "209129161";
    private static boolean initialized = false;

    // ── Initialize ────────────────────────────────────────────────

    /**
     * Must be called in Application.onCreate() before any Activity starts.
     * StartAppSDK.init() is synchronous — init is complete when this returns.
     */
    public static void initialize(Context context) {
        if (initialized) {
            Log.d(TAG, "Already initialized — skipping duplicate call");
            return;
        }

        try {
            // Pass APPLICATION context to avoid Activity leaks.
            // Third param = false → disable test ads in production.
            StartAppSDK.init(context.getApplicationContext(), APP_ID, false);
            StartAppSDK.setTestAdsEnabled(false);
            initialized = true;
            Log.d(TAG, "✅ Start.io initialized with App ID: " + APP_ID);

        } catch (Exception e) {
            Log.e(TAG, "❌ Start.io init failed: " + e.getMessage(), e);
        }
    }

    public static boolean isInitialized() {
        return initialized;
    }

    // ── Interstitial ──────────────────────────────────────────────

    public static void showInterstitial(Activity activity, Runnable onComplete) {
        if (!initialized) {
            Log.e(TAG, "showInterstitial called before init — skipping");
            if (onComplete != null) onComplete.run();
            return;
        }
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
        if (!initialized) {
            Log.e(TAG, "showRewarded called before init — skipping");
            if (onComplete != null) onComplete.run();
            return;
        }
        try {
            StartAppAd startAppAd = new StartAppAd(activity);
            startAppAd.setVideoListener(new VideoListener() {
                @Override
                public void onVideoCompleted() {
                    Log.d(TAG, "Rewarded video completed — granting reward");
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

    /**
     * Creates a banner programmatically AFTER SDK init.
     * Never call this before initialize() completes.
     */
    public static Banner createBanner(Activity activity, ViewGroup container) {
        if (!initialized) {
            Log.e(TAG, "createBanner called before init — skipping");
            return null;
        }
        try {
            Banner banner = new Banner(activity);
            container.addView(banner);
            container.setVisibility(android.view.View.VISIBLE);
            Log.d(TAG, "Banner created for App ID: " + APP_ID);
            return banner;
        } catch (Exception e) {
            Log.e(TAG, "Banner creation error: " + e.getMessage());
            return null;
        }
    }
}
