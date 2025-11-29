package com.pixelsmith.pixelview;

import android.content.Context;
import android.content.SharedPreferences;

public class ThemeHelper {
    public static final String PREFS_NAME = "theme_prefs";
    public static final String KEY_THEME = "theme_mode";

    public static final int THEME_SYSTEM = 0;
    public static final int THEME_LIGHT = 1;
    public static final int THEME_DARK = 2;

    public static void applyTheme(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        int theme = prefs.getInt(KEY_THEME, THEME_SYSTEM);

        switch (theme) {
            case THEME_LIGHT:
                context.setTheme(R.style.Theme_PixelView_Light);
                break;
            case THEME_DARK:
                context.setTheme(R.style.Theme_PixelView_Dark);
                break;
            case THEME_SYSTEM:
            default:
                // применяем системную тему: можно light по умолчанию
                context.setTheme(R.style.Theme_PixelView_Light); // или темную, если хочешь
                break;
        }
    }

    public static void saveTheme(Context context, int theme) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().putInt(KEY_THEME, theme).apply();
    }

    public static int getSavedTheme(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getInt(KEY_THEME, THEME_SYSTEM);
    }
}
