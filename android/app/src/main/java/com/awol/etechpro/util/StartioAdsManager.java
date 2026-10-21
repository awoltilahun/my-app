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
    private static ViewGroup pendingBannerContainer = null;
    private static Context pendingBannerContext = null;

    // ── Initialize ────────────────────────────────────────────────

    public static void initialize(Context context) {
        if (initialized) {
            Log.d(TAG, "Already initialized — skipping duplicate call");
            return;
        }
        try {
            // Set user consent BEFORE init — prevents consent dialog from appearing
            // and stops undefined Product ID requests during consent-pending window.
            // Your users are primarily in Ethiopia (non-GDPR territory).
            // GDPR_CONSENT_SHOWN manifest key alone is unreliable in SDK 5.3.1.
            StartAppSDK.setUserConsent(
                    context.getApplicationContext(),
                    "pas",
                    System.currentTimeMillis(),
                    true);

            StartAppSDK.initParams(context.getApplicationContext(), APP_ID)
                    .setReturnAdsEnabled(false)
                    .setCallback(() -> {
                        StartAppSDK.setTestAdsEnabled(false);
                        initialized = true;
                        Log.d(TAG, "✅ Start.io SDK ready — App ID: " + APP_ID
                                + " — ads enabled");
                        // Load queued banner now that init is complete
                        if (pendingBannerContainer != null && pendingBannerContext != null) {
                            Context ctx = pendingBannerContext;
                            ViewGroup container = pendingBannerContainer;
                            pendingBannerContainer = null;
                            pendingBannerContext = null;
                            if (ctx instanceof Activity) {
                                ((Activity) ctx).runOnUiThread(() ->
                                        loadBanner(ctx, container));
                            }
                        }
                    })
                    .init();

            Log.d(TAG, "Start.io initParams called — waiting for callback…");

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
            // Queue banner — will be loaded when init callback fires
            Log.d(TAG, "loadBanner queued — waiting for init to complete");
            pendingBannerContainer = container;
            pendingBannerContext = context;
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
