package com.example.neurocare;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class GamesActivity extends AppCompatActivity {

    private LinearLayout cardMemoryMatch;
    private LinearLayout cardAttention;
    private LinearLayout cardReaction;
    private LinearLayout cardOddOneOut;
    private LinearLayout dailyChallengeCard;

    private String currentTheme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_games);

        cardMemoryMatch = findViewById(R.id.cardMemoryMatch);
        cardAttention = findViewById(R.id.cardAttention);
        cardReaction = findViewById(R.id.cardReaction);
        cardOddOneOut = findViewById(R.id.cardOddOneOut);
        dailyChallengeCard = findViewById(R.id.dailyChallengeCard);

        // Memory Match
        cardMemoryMatch.setOnClickListener(v -> {
            Intent intent = new Intent(
                    GamesActivity.this,
                    MemoryMatchActivity.class
            );
            startActivity(intent);
        });

        // Attention Training
        cardAttention.setOnClickListener(v -> {
            Intent intent = new Intent(
                    GamesActivity.this,
                    AttentionActivity.class
            );
            startActivity(intent);
        });

        // Reaction Time
        cardReaction.setOnClickListener(v -> {
            Intent intent = new Intent(
                    GamesActivity.this,
                    ReactionActivity.class
            );
            startActivity(intent);
        });
        // Odd One Out
        cardOddOneOut.setOnClickListener(v -> {
            Intent intent = new Intent(
                    GamesActivity.this,
                    OddOneOutActivity.class
            );
            startActivity(intent);
        });

        // Bottom Navigation
        setupBottomNavigation();

        applyGamesTheme();
    }

    private void setupBottomNavigation() {

        LinearLayout navHome = findViewById(R.id.navHome);
        LinearLayout navGames = findViewById(R.id.navGames);
        LinearLayout navProgress = findViewById(R.id.navProgress);
        LinearLayout navHistory = findViewById(R.id.navHistory);
        LinearLayout navProfile = findViewById(R.id.navProfile);

        // Highlight current tab
        navGames.setBackgroundResource(R.drawable.bg_nav_selected);

        navHome.setOnClickListener(v -> {
            Intent intent = new Intent(GamesActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        });

        navGames.setOnClickListener(v -> {
            // Already in Games screen
        });

        navProgress.setOnClickListener(v -> {
            startActivity(new Intent(GamesActivity.this, ProgressActivity.class));
            finish();
        });

        navHistory.setOnClickListener(v -> {
            startActivity(new Intent(GamesActivity.this, HistoryActivity.class));
            finish();
        });

        navProfile.setOnClickListener(v -> {
            startActivity(new Intent(GamesActivity.this, SettingsActivity.class));
            finish();
        });
    }

    private void applyGamesTheme() {

        currentTheme = ThemeManager.getSelectedTheme(this);

        ThemeManager.applyTheme(
                this,
                R.id.gamesMain
        );

        int backgroundColor = ThemeManager.getBackgroundColor(this);
        int cardColor = ThemeManager.getCardColor(this);
        int primaryColor = ThemeManager.getPrimaryColor(this);
        int textColor = ThemeManager.getTextColor(this);

        int secondaryColor;

        if (currentTheme.equals("dark")) {
            secondaryColor = Color.parseColor("#C0C0C0");
        } else {
            secondaryColor = Color.parseColor("#89817D");
        }

        // Game Cards
        setCardBackground(cardMemoryMatch, cardColor, primaryColor);
        setCardBackground(cardAttention, cardColor, primaryColor);
        setCardBackground(cardReaction, cardColor, primaryColor);

        // Daily Challenge Gradient
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
                startColor = Color.parseColor("#FF795B");
                endColor = Color.parseColor("#FFB49A");
                break;
        }

        GradientDrawable challengeBackground =
                new GradientDrawable(
                        GradientDrawable.Orientation.LEFT_RIGHT,
                        new int[]{startColor, endColor}
                );

        challengeBackground.setCornerRadius(
                28 * getResources().getDisplayMetrics().density
        );

        dailyChallengeCard.setBackground(challengeBackground);

        // Text Colors
        TextView gamesTitle = findViewById(R.id.gamesTitle);
        TextView gamesSubtitle = findViewById(R.id.gamesSubtitle);

        TextView exploreTitle = findViewById(R.id.exploreTitle);
        TextView exploreSubtitle = findViewById(R.id.exploreSubtitle);

        TextView memoryTitle = findViewById(R.id.memoryTitle);
        TextView memoryDescription = findViewById(R.id.memoryDescription);

        TextView attentionTitle = findViewById(R.id.attentionTitle);
        TextView reactionTitle = findViewById(R.id.reactionTitle);

        gamesTitle.setTextColor(textColor);
        exploreTitle.setTextColor(textColor);

        gamesSubtitle.setTextColor(secondaryColor);
        exploreSubtitle.setTextColor(secondaryColor);

        memoryTitle.setTextColor(textColor);
        memoryDescription.setTextColor(secondaryColor);

        attentionTitle.setTextColor(textColor);
        reactionTitle.setTextColor(textColor);

        // Other text colors
        updateOtherTextColors(
                (ViewGroup) findViewById(R.id.gamesMain),
                primaryColor,
                textColor,
                secondaryColor
        );

        // Keep challenge text white
        updateChallengeText(dailyChallengeCard);
    }

    private void setCardBackground(
            LinearLayout card,
            int color,
            int primaryColor
    ) {

        GradientDrawable background = new GradientDrawable();

        background.setColor(color);

        background.setCornerRadius(
                24 * getResources().getDisplayMetrics().density
        );

        background.setStroke(
                (int) (1 * getResources().getDisplayMetrics().density),
                currentTheme.equals("dark")
                        ? Color.parseColor("#444444")
                        : Color.parseColor("#F0E5E0")
        );

        card.setBackground(background);
    }

    private void updateOtherTextColors(
            ViewGroup group,
            int primaryColor,
            int textColor,
            int secondaryColor
    ) {

        for (int i = 0; i < group.getChildCount(); i++) {

            View child = group.getChildAt(i);

            if (child == dailyChallengeCard) {
                continue;
            }

            if (child instanceof TextView) {

                TextView textView = (TextView) child;

                float size = textView.getTextSize()
                        / getResources().getDisplayMetrics().scaledDensity;

                String text = textView.getText().toString();

                if (text.equals("›")) {
                    textView.setTextColor(primaryColor);

                } else if (
                        text.equals("MEMORY") ||
                                text.equals("FOCUS") ||
                                text.equals("SPEED") ||
                                text.equals("NEUROCARE • TRAIN YOUR MIND")
                ) {
                    textView.setTextColor(primaryColor);

                } else if (size >= 16) {
                    textView.setTextColor(textColor);

                } else {
                    textView.setTextColor(secondaryColor);
                }

            } else if (child instanceof ViewGroup) {

                updateOtherTextColors(
                        (ViewGroup) child,
                        primaryColor,
                        textColor,
                        secondaryColor
                );
            }
        }
    }

    private void updateChallengeText(ViewGroup group) {

        for (int i = 0; i < group.getChildCount(); i++) {

            View child = group.getChildAt(i);

            if (child instanceof TextView) {

                ((TextView) child).setTextColor(Color.WHITE);

            } else if (child instanceof ViewGroup) {

                updateChallengeText((ViewGroup) child);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (dailyChallengeCard != null) {
            applyGamesTheme();
        }
    }
}