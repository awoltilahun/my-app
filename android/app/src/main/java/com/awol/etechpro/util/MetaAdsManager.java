package com.awol.etechpro.util;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.ViewGroup;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdListener;
import com.facebook.ads.AdSettings;
import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;
import com.facebook.ads.AudienceNetworkAds;
import com.facebook.ads.InterstitialAd;
import com.facebook.ads.InterstitialAdListener;

public class MetaAdsManager {

    private static final String TAG = "MetaAdsManager";

    // ── Placement IDs ─────────────────────────────────────────────
    public static final String PLACEMENT_BANNER       = "1780406383290238_1780412649956278";
    public static final String PLACEMENT_INTERSTITIAL = "1780406383290238_1780412639956279";
    public static final String PLACEMENT_MREC         = "1780406383290238_1780412636622946";
    public static final String PLACEMENT_REWARDED     = "1780406383290238_1780412633289613";

    private static InterstitialAd interstitialAd;

    // ── Initialize ────────────────────────────────────────────────

    public static void initialize(Context context) {
        // Test mode OFF — production build
        AudienceNetworkAds.initialize(context);
        Log.d(TAG, "Meta Audience Network initialized");
        // Preload interstitial and rewarded
        loadInterstitial(context);
        loadRewarded(context);
    }

    // ── Banner ────────────────────────────────────────────────────

    public static void loadBanner(Activity activity, ViewGroup container) {
        AdView adView = new AdView(activity, PLACEMENT_BANNER, AdSize.BANNER_HEIGHT_50);
        container.addView(adView);
        adView.loadAd(adView.buildLoadAdConfig()
                .withAdListener(new AdListener() {
                    @Override
                    public void onAdLoaded(Ad ad) {
                        Log.d(TAG, "Meta banner loaded");
                        container.setVisibility(android.view.View.VISIBLE);
                    }
                    @Override
                    public void onError(Ad ad, AdError error) {
                        Log.e(TAG, "Meta banner failed: " + error.getErrorMessage());
                        container.setVisibility(android.view.View.GONE);
                    }
                    @Override public void onAdClicked(Ad ad) {}
                    @Override public void onLoggingImpression(Ad ad) {}
                })
                .build());
    }

    // ── Interstitial ──────────────────────────────────────────────

    public static void loadInterstitial(Context context) {
        interstitialAd = new InterstitialAd(context, PLACEMENT_INTERSTITIAL);
        interstitialAd.loadAd(interstitialAd.buildLoadAdConfig()
                .withAdListener(new InterstitialAdListener() {
                    @Override
                    public void onInterstitialDisplayed(Ad ad) {}
                    @Override
                    public void onInterstitialDismissed(Ad ad) {
                        loadInterstitial(context); // preload next
                    }
                    @Override
                    public void onError(Ad ad, AdError error) {
                        Log.e(TAG, "Meta interstitial failed: " + error.getErrorMessage());
                    }
                    @Override
                    public void onAdLoaded(Ad ad) {
                        Log.d(TAG, "Meta interstitial loaded");
                    }
                    @Override public void onAdClicked(Ad ad) {}
                    @Override public void onLoggingImpression(Ad ad) {}
                })
                .build());
    }

    public static void showInterstitial(Activity activity, Runnable onComplete) {
        if (interstitialAd != null && interstitialAd.isAdLoaded()) {
            interstitialAd.show();
            if (onComplete != null) onComplete.run();
        } else {
            Log.e(TAG, "Meta interstitial not ready");
            if (onComplete != null) onComplete.run();
        }
    }

    // ── Rewarded ──────────────────────────────────────────────────

    public static void loadRewarded(Context context) {
        // Note: Rewarded Interstitial placement used here
        // Meta SDK RewardedVideoAd does not support Rewarded Interstitial format
        // Skipping Meta rewarded — use Unity/AdMob rewarded instead
        Log.d(TAG, "Meta rewarded skipped — placement format mismatch");
    }

    public static void showRewarded(Activity activity,
            Runnable onRewarded, Runnable onComplete) {
        // Meta rewarded not available — fall through to Unity/AdMob
        Log.d(TAG, "Meta rewarded not available — using fallback");
        if (onComplete != null) onComplete.run();
    }

    public static boolean isInterstitialReady() {
        return interstitialAd != null && interstitialAd.isAdLoaded();
    }

    public static boolean isRewardedReady() {
        return rewardedAd != null && rewardedAd.isAdLoaded();
    }
}
