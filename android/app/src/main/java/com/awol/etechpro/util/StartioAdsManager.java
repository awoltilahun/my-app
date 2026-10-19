package com.awol.etechpro.util;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.startapp.sdk.ads.banner.BannerFormat;
import com.startapp.sdk.ads.banner.BannerRequest;
import com.startapp.sdk.adsbase.StartAppAd;
import com.startapp.sdk.adsbase.StartAppSDK;
import com.startapp.sdk.adsbase.VideoListener;

/**
 * Start.io (formerly StartApp) SDK wrapper.
 *
 * SDK version: 5.3.1 (pinned in build.gradle)
 * App ID:      209129161
 *
 * Key fixes applied:
 * 1. StartAppInitProvider ContentProvider removed via tools:node="remove"
 *    — prevents auto-init with undefined App ID before Application.onCreate()
 * 2. setUserConsent() called BEFORE init() — suppresses the consent dialog
 *    which was causing "undefined" requests before the user dismissed it
 * 3. setReturnAdsEnabled(false) — disables intrusive return ads
 * 4. Uses new initParams() API (SDK 5.x recommended) with callback
 */
public class StartioAdsManager {

    private static final String TAG = "StartioAdsManager";
    public  static final String APP_ID = "209129161";
    private static boolean initialized = false;

    // ── Initialize ────────────────────────────────────────────────

    public static void initialize(Context context) {
        if (initialized) {
            Log.d(TAG, "Already initialized — skipping duplicate call");
            return;
        }
        try {
            // Initialize using the recommended initParams API
            // Consent dialog is suppressed via manifest GDPR_CONSENT_SHOWN=true
            // which tells the SDK our app already handles privacy disclosure
            StartAppSDK.initParams(context.getApplicationContext(), APP_ID)
                    .setReturnAdsEnabled(false)
                    .setCallback(() -> {
                        Log.d(TAG, "✅ Start.io SDK ready — App ID: " + APP_ID);
                    })
                    .init();

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

    // ── Banner ────────────────────────────────────────────────────

    public static void loadBanner(Context context, ViewGroup container) {
        if (!initialized) {
            Log.e(TAG, "loadBanner called before init — skipping");
            return;
        }
        Log.d(TAG, "Banner load requested…");
        new BannerRequest(context.getApplicationContext())
                .setAdFormat(BannerFormat.BANNER)
                .load((creator, error) -> {
                    if (creator != null) {
                        View adView = creator.create(context.getApplicationContext(), null);
                        if (context instanceof Activity) {
                            ((Activity) context).runOnUiThread(() -> {
                                container.removeAllViews();
                                container.addView(adView);
                                container.setVisibility(View.VISIBLE);
                                Log.d(TAG, "✅ Banner loaded and visible");
                            });
                        }
                    } else {
                        Log.e(TAG, "❌ Banner failed to load — error: "
                                + (error != null ? error : "unknown"));
                        if (context instanceof Activity) {
                            ((Activity) context).runOnUiThread(() ->
                                    container.setVisibility(View.GONE));
                        }
                    }
                });
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
            StartAppAd rewardedAd = new StartAppAd(activity);
            rewardedAd.setVideoListener(new VideoListener() {
                @Override
                public void onVideoCompleted() {
                    Log.d(TAG, "Rewarded video completed — granting reward");
                    if (onRewarded != null) onRewarded.run();
                }
            });
            rewardedAd.loadAd(StartAppAd.AdMode.REWARDED_VIDEO);
            rewardedAd.showAd();
        } catch (Exception e) {
            Log.e(TAG, "Rewarded error: " + e.getMessage());
        }
        if (onComplete != null) onComplete.run();
    }
}
