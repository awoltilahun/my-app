package com.awol.etechpro.util;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.startapp.sdk.ads.banner.BannerCreator;
import com.startapp.sdk.ads.banner.BannerFormat;
import com.startapp.sdk.ads.banner.BannerRequest;
import com.startapp.sdk.adsbase.Ad;
import com.startapp.sdk.adsbase.StartAppAd;
import com.startapp.sdk.adsbase.StartAppSDK;
import com.startapp.sdk.adsbase.VideoListener;
import com.startapp.sdk.adsbase.adListeners.AdEventListener;

/**
 * Start.io (formerly StartApp) SDK wrapper.
 *
 * SDK version: 5.3.1 (pinned in build.gradle)
 * App ID:      209129161
 *
 * Initialization strategy:
 *  - StartAppInitProvider ContentProvider is removed via tools:node="remove"
 *    in AndroidManifest.xml — SDK CANNOT auto-initialize before
 *    Application.onCreate() runs.
 *  - StartAppSDK.init() is called explicitly in EtechProApp.onCreate().
 *  - The `initialized` flag prevents any ad call before init completes.
 *
 * Banner strategy:
 *  - Uses BannerRequest API (recommended by Start.io SDK 5.x docs)
 *    so we receive explicit load-success / load-failure callbacks.
 *  - Container visibility is set to VISIBLE only after a successful load.
 *  - Test ads are enabled only when BuildConfig.DEBUG is true.
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
            StartAppSDK.init(context.getApplicationContext(), APP_ID, false);

            // Enable test ads in debug builds only
            if (com.awol.etechpro.BuildConfig.DEBUG) {
                StartAppSDK.setTestAdsEnabled(true);
                Log.d(TAG, "⚠️  Test ads ENABLED (debug build)");
            } else {
                StartAppSDK.setTestAdsEnabled(false);
            }

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

    /**
     * Loads a banner using BannerRequest (SDK 5.x recommended API).
     * Container is shown only after a successful load.
     * Failures are logged with the exact error string from the SDK.
     */
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
                        // Must add and show on the main thread
                        if (container instanceof android.view.ViewGroup) {
                            ((android.app.Activity) context).runOnUiThread(() -> {
                                container.removeAllViews();
                                container.addView(adView);
                                container.setVisibility(View.VISIBLE);
                                Log.d(TAG, "✅ Banner loaded and visible");
                            });
                        }
                    } else {
                        Log.e(TAG, "❌ Banner failed to load — error: "
                                + (error != null ? error : "unknown"));
                        ((android.app.Activity) context).runOnUiThread(() ->
                                container.setVisibility(View.GONE));
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
        StartAppAd interstitialAd = new StartAppAd(activity);
        interstitialAd.loadAd(new AdEventListener() {
            @Override
            public void onReceiveAd(@NonNull Ad ad) {
                Log.d(TAG, "Interstitial loaded — showing");
                interstitialAd.showAd();
                if (onComplete != null) onComplete.run();
            }
            @Override
            public void onFailedToReceiveAd(@Nullable Ad ad) {
                Log.e(TAG, "Interstitial failed to load");
                if (onComplete != null) onComplete.run();
            }
        });
    }

    // ── Rewarded ──────────────────────────────────────────────────

    public static void showRewarded(Activity activity,
            Runnable onRewarded, Runnable onComplete) {
        if (!initialized) {
            Log.e(TAG, "showRewarded called before init — skipping");
            if (onComplete != null) onComplete.run();
            return;
        }
        StartAppAd rewardedAd = new StartAppAd(activity);
        rewardedAd.setVideoListener(new VideoListener() {
            @Override
            public void onVideoCompleted() {
                Log.d(TAG, "Rewarded video completed — granting reward");
                if (onRewarded != null) onRewarded.run();
            }
        });
        rewardedAd.loadAd(StartAppAd.AdMode.REWARDED_VIDEO, new AdEventListener() {
            @Override
            public void onReceiveAd(@NonNull Ad ad) {
                Log.d(TAG, "Rewarded loaded — showing");
                rewardedAd.showAd();
                if (onComplete != null) onComplete.run();
            }
            @Override
            public void onFailedToReceiveAd(@Nullable Ad ad) {
                Log.e(TAG, "Rewarded failed to load");
                if (onComplete != null) onComplete.run();
            }
        });
    }
}
