package com.example.neurocare;

import android.os.Bundle;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

public class HistoryActivity extends AppCompatActivity {

    private LinearLayout historyContainer;
    private LinearLayout emptyHistory;
    private LinearLayout historySummaryCard;

    private TextView txtHistoryCount;

    private String currentTheme;

    private int primaryColor;
    private int cardColor;
    private int textColor;
    private int secondaryColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_history);

        historyContainer = findViewById(R.id.historyContainer);
        emptyHistory = findViewById(R.id.emptyHistory);
        txtHistoryCount = findViewById(R.id.txtHistoryCount);

        historySummaryCard = findViewById(R.id.historySummaryCard);

        // Bottom Navigation
        setupBottomNavigation();

        applyHistoryTheme();
        loadHistory();
    }

    // ==========================================
    // BOTTOM NAVIGATION
    // ==========================================

    private void setupBottomNavigation() {

        findViewById(R.id.navHome).setOnClickListener(v -> {

            Intent intent = new Intent(this, MainActivity.class);

            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            startActivity(intent);

            finish();
        });

        findViewById(R.id.navGames).setOnClickListener(v -> {

            startActivity(new Intent(this, GamesActivity.class));

            finish();
        });

        findViewById(R.id.navProgress).setOnClickListener(v -> {

            startActivity(new Intent(this, ProgressActivity.class));

            finish();
        });

        findViewById(R.id.navHistory).setOnClickListener(v -> {

            // Already in History screen
        });

        findViewById(R.id.navProfile).setOnClickListener(v -> {

            startActivity(new Intent(this, SettingsActivity.class));

            finish();
        });
    }

    // ==========================================
    // REFRESH
    // ==========================================

    @Override
    protected void onResume() {
        super.onResume();

        if (historyContainer != null) {

            applyHistoryTheme();

            loadHistory();
        }
    }

    // ==========================================
    // APPLY THEME
    // ==========================================

    private void applyHistoryTheme() {

        currentTheme = ThemeManager.getSelectedTheme(this);

        ThemeManager.applyTheme(this, R.id.historyMain);

        primaryColor = ThemeManager.getPrimaryColor(this);

        cardColor = ThemeManager.getCardColor(this);

        textColor = ThemeManager.getTextColor(this);

        if (currentTheme.equals("dark")) {

            secondaryColor = Color.parseColor("#BDBDBD");

        } else {

            secondaryColor = Color.parseColor("#6B5B52");
        }

        // Header and Titles

        setTextColor(R.id.historyHeader, primaryColor);

        setTextColor(R.id.historyTitle, textColor);

        setTextColor(R.id.historySubtitle, secondaryColor);

        setTextColor(R.id.recentSessionsTitle, textColor);

        setTextColor(R.id.historyLatestLabel, primaryColor);

        setTextColor(R.id.emptyHistoryTitle, textColor);

        setTextColor(R.id.emptyHistoryDescription, secondaryColor);

        // Summary Card Gradient

        int startColor;
        int endColor;

        switch (currentTheme) {

            case "dark":

                startColor = Color.parseColor("#9C4430");

                endColor = Color.parseColor("#D66D50");

                break;

            case "blue":

                startColor = Color.parseColor("#2861D8");

                endColor = Color.parseColor("#69A4FF");

                break;

            case "purple":

                startColor = Color.parseColor("#6940B8");

                endColor = Color.parseColor("#A77AE8");

                break;

            default:

                startColor = Color.parseColor("#C94A0C");

                endColor = Color.parseColor("#F59A62");

                break;
        }

        GradientDrawable gradient = new GradientDrawable(
                GradientDrawable.Orientation.LEFT_RIGHT,
                new int[]{startColor, endColor}
        );

        gradient.setCornerRadius(dp(24));

        historySummaryCard.setBackground(gradient);

        // Summary text

        txtHistoryCount.setTextColor(Color.WHITE);

        // Empty History

        emptyHistory.setBackgroundColor(Color.TRANSPARENT);
    }

    private void setTextColor(int viewId, int color) {

        TextView textView = findViewById(viewId);

        if (textView != null) {

            textView.setTextColor(color);
        }
    }

    // ==========================================
    // LOAD SESSION HISTORY
    // ==========================================

    private void loadHistory() {

        historyContainer.removeAllViews();

        SharedPreferences preferences =
                getSharedPreferences("NeuroCareStats", MODE_PRIVATE);

        String historyData = preferences.getString(
                "sessionHistory",
                "[]"
        );

        try {

            JSONArray historyArray = new JSONArray(historyData);

            int totalSessions = historyArray.length();

            txtHistoryCount.setText(
                    totalSessions + (
                            totalSessions == 1
                                    ? " Session Completed"
                                    : " Sessions Completed"
                    )
            );

            if (totalSessions == 0) {

                emptyHistory.setVisibility(View.VISIBLE);

                return;
            }

            emptyHistory.setVisibility(View.GONE);

            for (int i = 0; i < totalSessions; i++) {

                JSONObject session = historyArray.getJSONObject(i);

                String gameName = session.optString(
                        "gameName",
                        "Brain Training"
                );

                int score = session.optInt("score", 0);

                String result = session.optString(
                        "result",
                        ""
                );

                String date = session.optString(
                        "date",
                        ""
                );

                addHistoryCard(
                        gameName,
                        score,
                        result,
                        date
                );
            }

        } catch (Exception e) {

            e.printStackTrace();

            txtHistoryCount.setText("0 Sessions Completed");

            emptyHistory.setVisibility(View.VISIBLE);
        }
    }

    // ==========================================
    // CREATE HISTORY CARD
    // ==========================================

    private void addHistoryCard(
            String gameName,
            int score,
            String result,
            String date
    ) {

        // Main Card

        LinearLayout card = new LinearLayout(this);

        card.setOrientation(LinearLayout.VERTICAL);

        card.setPadding(
                dp(18),
                dp(17),
                dp(18),
                dp(17)
        );

        GradientDrawable background = new GradientDrawable();

        background.setColor(cardColor);

        background.setCornerRadius(dp(20));

        background.setStroke(
                dp(1),
                currentTheme.equals("dark")
                        ? Color.parseColor("#444444")
                        : Color.parseColor("#FBD9BF")
        );

        card.setBackground(background);

        LinearLayout.LayoutParams cardParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        cardParams.bottomMargin = dp(14);

        card.setLayoutParams(cardParams);

        // Heading Row

        LinearLayout heading = new LinearLayout(this);

        heading.setOrientation(LinearLayout.HORIZONTAL);

        heading.setGravity(Gravity.CENTER_VERTICAL);

        // Game Icon

        TextView icon = new TextView(this);

        icon.setText(getGameIcon(gameName));

        icon.setTextSize(25);

        LinearLayout.LayoutParams iconParams =
                new LinearLayout.LayoutParams(
                        dp(48),
                        dp(48)
                );

        iconParams.rightMargin = dp(12);

        icon.setGravity(Gravity.CENTER);

        icon.setBackground(createIconBackground());

        heading.addView(icon, iconParams);

        // Title Area

        LinearLayout titleArea = new LinearLayout(this);

        titleArea.setOrientation(LinearLayout.VERTICAL);

        TextView title = new TextView(this);

        title.setText(gameName);

        title.setTextSize(16);

        title.setTextColor(textColor);

        title.setTypeface(null, Typeface.BOLD);

        TextView dateText = new TextView(this);

        dateText.setText(date);

        dateText.setTextSize(11);

        dateText.setTextColor(secondaryColor);

        LinearLayout.LayoutParams dateParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        dateParams.topMargin = dp(5);

        titleArea.addView(title);

        titleArea.addView(dateText, dateParams);

        heading.addView(titleArea);

        card.addView(heading);

        // Divider

        View divider = new View(this);

        divider.setBackgroundColor(
                currentTheme.equals("dark")
                        ? Color.parseColor("#444444")
                        : Color.parseColor("#FBD9BF")
        );

        LinearLayout.LayoutParams dividerParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        dp(1)
                );

        dividerParams.topMargin = dp(15);

        dividerParams.bottomMargin = dp(13);

        card.addView(divider, dividerParams);

        // Result

        TextView resultText = new TextView(this);

        resultText.setText(result);

        resultText.setTextSize(13);

        resultText.setTextColor(secondaryColor);

        card.addView(resultText);

        // Score

        TextView scoreText = new TextView(this);

        if (gameName.equals("Reaction Time")) {

            scoreText.setText("Average: " + score + " ms");

        } else {

            scoreText.setText("Score: " + score + " points");
        }

        scoreText.setTextSize(14);

        scoreText.setTextColor(primaryColor);

        scoreText.setTypeface(null, Typeface.BOLD);

        LinearLayout.LayoutParams scoreParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        scoreParams.topMargin = dp(12);

        card.addView(scoreText, scoreParams);

        // Add Card

        historyContainer.addView(card);
    }

    // ==========================================
    // GAME ICONS
    // ==========================================

    private String getGameIcon(String gameName) {

        if (gameName.equals("Memory Match")) {

            return "🧠";
        }

        if (gameName.equals("Attention Training")) {

            return "🎯";
        }

        if (gameName.equals("Reaction Time")) {

            return "⚡";
        }

        return "🎮";
    }

    // ==========================================
    // ICON BACKGROUND
    // ==========================================

    private GradientDrawable createIconBackground() {

        GradientDrawable drawable = new GradientDrawable();

        int iconColor;

        switch (currentTheme) {

            case "dark":

                iconColor = Color.parseColor("#49312B");

                break;

            case "blue":

                iconColor = Color.parseColor("#E5EEFF");

                break;

            case "purple":

                iconColor = Color.parseColor("#EFE5FF");

                break;

            default:

                iconColor = Color.parseColor("#FFF4EB");

                break;
        }

        drawable.setColor(iconColor);

        drawable.setCornerRadius(dp(15));

        return drawable;
    }

    // ==========================================
    // DP CONVERSION
    // ==========================================

    private int dp(int value) {

        return (int) (
                value * getResources()
                        .getDisplayMetrics()
                        .density
        );
    }
}