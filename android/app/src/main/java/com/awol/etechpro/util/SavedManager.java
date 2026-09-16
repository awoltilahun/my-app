package com.awol.etechpro.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.awol.etechpro.model.TechTip;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class SavedManager {

    private static final String PREFS_NAME = "saved_tips";
    private static final String KEY_TIPS   = "tips";

    // Save a tip
    public static void saveTip(Context context, TechTip tip) {
        List<TechTip> saved = getSavedTips(context);
        // Avoid duplicates
        for (TechTip t : saved) {
            if (t.getId() != null && t.getId().equals(tip.getId())) return;
        }
        saved.add(tip);
        persist(context, saved);
    }

    // Remove a tip
    public static void removeTip(Context context, TechTip tip) {
        List<TechTip> saved = getSavedTips(context);
        saved.removeIf(t -> t.getId() != null && t.getId().equals(tip.getId()));
        persist(context, saved);
    }

    // Check if a tip is saved
    public static boolean isSaved(Context context, TechTip tip) {
        List<TechTip> saved = getSavedTips(context);
        for (TechTip t : saved) {
            if (t.getId() != null && t.getId().equals(tip.getId())) return true;
        }
        return false;
    }

    // Get all saved tips
    public static List<TechTip> getSavedTips(Context context) {
        try {
            SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            String json = prefs.getString(KEY_TIPS, null);
            if (json == null) return new ArrayList<>();
            Type type = new TypeToken<List<TechTip>>(){}.getType();
            return new Gson().fromJson(json, type);
        } catch (Exception e) {
            // Clear corrupted data
            context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                   .edit().clear().apply();
            return new ArrayList<>();
        }
    }

    // Persist to SharedPreferences
    private static void persist(Context context, List<TechTip> tips) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().putString(KEY_TIPS, new Gson().toJson(tips)).apply();
    }
}
