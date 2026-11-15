package com.example.neurocare;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.View;
import android.view.Window;

public class ThemeManager {

    public static final String LIGHT = "light";
    public static final String DARK = "dark";
    public static final String BLUE = "blue";
    public static final String PURPLE = "purple";

    public static String getSelectedTheme(Context context) {

        SharedPreferences preferences =
                context.getSharedPreferences(
                        "NeuroCareSettings",
                        Context.MODE_PRIVATE
                );

        return preferences.getString("appTheme", LIGHT);
    }

    public static int getBackgroundColor(Context context) {

        String theme = getSelectedTheme(context);

        switch (theme) {

            case DARK:
                return Color.parseColor("#171717");

            case BLUE:
                return Color.parseColor("#F0F5FF");

            case PURPLE:
                return Color.parseColor("#F7F1FF");

            default:
                return Color.parseColor("#FFF9F6");
        }
    }

    public static int getCardColor(Context context) {

        String theme = getSelectedTheme(context);

        switch (theme) {

            case DARK:
                return Color.parseColor("#292929");

            case BLUE:
                return Color.parseColor("#FFFFFF");

            case PURPLE:
                return Color.parseColor("#FFFFFF");

            default:
                return Color.parseColor("#FFFFFF");
        }
    }

    public static int getPrimaryColor(Context context) {

        String theme = getSelectedTheme(context);

        switch (theme) {

            case DARK:
                return Color.parseColor("#FF906F");

            case BLUE:
                return Color.parseColor("#3478F6");

            case PURPLE:
                return Color.parseColor("#8957D5");

            default:
                return Color.parseColor("#FF795B");
        }
    }

    public static int getTextColor(Context context) {

        String theme = getSelectedTheme(context);

        if (theme.equals(DARK)) {
            return Color.parseColor("#FFFFFF");
        }

        return Color.parseColor("#302723");
    }

    public static void applyTheme(Activity activity, int rootId) {

        View root = activity.findViewById(rootId);

        if (root != null) {
            root.setBackgroundColor(
                    getBackgroundColor(activity)
            );
        }

        Window window = activity.getWindow();

        window.setStatusBarColor(
                getBackgroundColor(activity)
        );

        window.setNavigationBarColor(
                getBackgroundColor(activity)
        );

        window.getDecorView().setSystemUiVisibility(
                getSelectedTheme(activity).equals(DARK)
                        ? 0
                        : View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                        | View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
        );
    }
}