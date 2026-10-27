package com.awol.etechpro.util;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.unity3d.mediation.LevelPlay;
import com.unity3d.mediation.LevelPlayAdError;
import com.unity3d.mediation.LevelPlayAdInfo;
import com.unity3d.mediation.LevelPlayAdSize;
import com.unity3d.mediation.LevelPlayConfiguration;
import com.unity3d.mediation.LevelPlayInitError;
import com.unity3d.mediation.LevelPlayInitListener;
import com.unity3d.mediation.LevelPlayInitRequest;
import com.unity3d.mediation.banner.LevelPlayBannerAdView;
import com.unity3d.mediation.banner.LevelPlayBannerAdViewListener;
import com.unity3d.mediation.interstitial.LevelPlayInterstitialAd;
import com.unity3d.mediation.interstitial.LevelPlayInterstitialAdListener;

/**
 * LevelPlay (ironSource) SDK wrapper — used ONLY for Meta Audience Network mediation.
 * Unity Ads, AdMob, InMobi, Start.io continue to use their own direct managers.
 *
 * App Key:       2908fcb55
 * Banner:        2d6b8u0ergr39j02
 * Interstitial:  hn3cmowitwovm x7h
 */
public class LevelPlayAdsManager {

    private static final String TAG = "LevelPlayAdsManager";

    // ── IDs ───────────────────────────────────────────────────────
    private static final String APP_KEY              = "2908fcb55";
    private static final String AD_UNIT_BANNER       = "2d6b8u0ergr39j02";
    private static final String AD_UNIT_INTERSTITIAL = "hn3cmowitwovmx7h";

    private static boolean initialized = false;
    private static LevelPlayInterstitialAd interstitialAd;
    private static LevelPlayBannerAdView bannerAdView;
    private static Activity pendingBannerActivity = null;
    private static ViewGroup pendingBannerContainer = null;

    // ── Initialize — call on main thread ─────────────────────────

    public static void initialize(Context context) {
        if (initialized) return;

        LevelPlayInitRequest initRequest = new LevelPlayInitRequest.Builder(APP_KEY)
                .build();

        LevelPlay.init(context, initRequest, new LevelPlayInitListener() {
            @Override
            public void onInitFailed(@NonNull LevelPlayInitError error) {
                Log.e(TAG, "❌ LevelPlay init failed: " + error.getErrorMessage());
            }

            @Override
            public void onInitSuccess(@NonNull LevelPlayConfiguration configuration) {
                initialized = true;
                Log.d(TAG, "✅ LevelPlay init successful — app key: " + APP_KEY);
                createInterstitialAd(context);
                // Load queued banner if any
                if (pendingBannerActivity != null && pendingBannerContainer != null) {
                    Activity act = pendingBannerActivity;
                    ViewGroup cont = pendingBannerContainer;
                    pendingBannerActivity = null;
                    pendingBannerContainer = null;
                    act.runOnUiThread(() -> loadBanner(act, cont));
                }
            }
        });
    }

    public static boolean isInitialized() {
        return initialized;
    }

    // ── Interstitial ──────────────────────────────────────────────

    private static void createInterstitialAd(Context context) {
        interstitialAd = new LevelPlayInterstitialAd(AD_UNIT_INTERSTITIAL);
        interstitialAd.setListener(new LevelPlayInterstitialAdListener() {
            @Override
            public void onAdLoaded(@NonNull LevelPlayAdInfo adInfo) {
                Log.d(TAG, "✅ Interstitial loaded — network: " + adInfo.getAdNetwork());
            }

            @Override
            public void onAdLoadFailed(@NonNull LevelPlayAdError error) {
                Log.e(TAG, "❌ Interstitial load failed: " + error.getErrorMessage());
            }

            @Override
            public void onAdDisplayed(@NonNull LevelPlayAdInfo adInfo) {
                Log.d(TAG, "Interstitial displayed");
            }

            @Override
            public void onAdDisplayFailed(@NonNull LevelPlayAdError error,
                    @NonNull LevelPlayAdInfo adInfo) {
                Log.e(TAG, "❌ Interstitial display failed: " + error.getErrorMessage());
            }

            @Override
            public void onAdClicked(@NonNull LevelPlayAdInfo adInfo) {
                Log.d(TAG, "Interstitial clicked");
            }

            @Override
            public void onAdClosed(@NonNull LevelPlayAdInfo adInfo) {
                Log.d(TAG, "Interstitial closed — reloading");
                interstitialAd.loadAd();
            }

            @Override
            public void onAdInfoChanged(@NonNull LevelPlayAdInfo adInfo) {}
        });
        interstitialAd.loadAd();
        Log.d(TAG, "Interstitial load requested");
    }

    public static void showInterstitial(Activity activity, Runnable onComplete) {
        if (interstitialAd != null && interstitialAd.isAdReady()) {
            Log.d(TAG, "Showing LevelPlay interstitial");
            interstitialAd.showAd(activity);
            if (onComplete != null) onComplete.run();
        } else {
            Log.e(TAG, "LevelPlay interstitial not ready");
            if (onComplete != null) onComplete.run();
        }
    }

    public static boolean isInterstitialReady() {
        return interstitialAd != null && interstitialAd.isAdReady();
    }

    // ── Banner ────────────────────────────────────────────────────

    public static void loadBanner(Activity activity, ViewGroup container) {
        if (!initialized) {
            Log.d(TAG, "loadBanner queued — waiting for init");
            pendingBannerActivity = activity;
            pendingBannerContainer = container;
            return;
        }
        Log.d(TAG, "Banner load requested — ad unit: " + AD_UNIT_BANNER);

        // LevelPlayBannerAdView is itself a View (extends FrameLayout)
        bannerAdView = new LevelPlayBannerAdView(activity, AD_UNIT_BANNER);

        bannerAdView.setBannerListener(new LevelPlayBannerAdViewListener() {
            @Override
            public void onAdLoaded(@NonNull LevelPlayAdInfo adInfo) {
                Log.d(TAG, "✅ Banner loaded — network: " + adInfo.getAdNetwork());
                activity.runOnUiThread(() -> {
                    container.removeAllViews();
                    container.addView(bannerAdView);
                    container.setVisibility(View.VISIBLE);
                });
            }

            @Override
            public void onAdLoadFailed(@NonNull LevelPlayAdError error) {
                Log.e(TAG, "❌ Banner load failed: " + error.getErrorMessage());
                activity.runOnUiThread(() ->
                        container.setVisibility(View.GONE));
            }

            @Override
            public void onAdDisplayed(@NonNull LevelPlayAdInfo adInfo) {
                Log.d(TAG, "Banner displayed");
            }

            @Override
            public void onAdClicked(@NonNull LevelPlayAdInfo adInfo) {
                Log.d(TAG, "Banner clicked");
            }

            @Override
            public void onAdLeftApplication(@NonNull LevelPlayAdInfo adInfo) {}

            @Override
            public void onAdExpanded(@NonNull LevelPlayAdInfo adInfo) {}

            @Override
            public void onAdCollapsed(@NonNull LevelPlayAdInfo adInfo) {}
        });

        bannerAdView.loadAd();
    }

    public static void destroyBanner() {
        if (bannerAdView != null) {
            bannerAdView.destroy();
            bannerAdView = null;
        }
    }

    // ── Lifecycle — no-op in SDK 9.5.0 (lifecycle managed internally) ──────────

    public static void onResume(Activity activity) {
        // LevelPlay SDK 9.5.0 manages lifecycle internally — no call needed
    }

    public static void onPause(Activity activity) {
        // LevelPlay SDK 9.5.0 manages lifecycle internally — no call needed
    }
}
