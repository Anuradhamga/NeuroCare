package com.example.neurocare;

import android.os.Bundle;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.app.AlertDialog;
import android.widget.Toast;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class AttentionActivity extends AppCompatActivity {

    private TextView txtScore;
    private TextView txtTargetSymbol;
    private TextView txtAttentionRound;
    private TextView txtAttentionDifficulty;
    private TextView txtAttentionLevelInfo;

    private GridLayout attentionGrid;

    private Button btnStartAttention;
    private Button btnChangeAttentionDifficulty;

    private int score = 0;
    private int round = 1;

    private int totalRounds = 10;
    private int symbolCount = 6;

    private String difficulty = "Medium";
    private String targetSymbol;

    private boolean gameStarted = false;
    private boolean resultSaved = false;
    private boolean difficultySelected = false;

    private final String[] symbols = {
            "⭐", "❤️", "🧠", "🌸",
            "🚀", "🎵", "🍎", "🐼",
            "⚡", "🎯", "🌙", "🍀"
    };

    private final Random random = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_attention);

        ThemeManager.applyTheme(this, R.id.attentionMain);

        txtScore = findViewById(R.id.txtAttentionScore);
        txtTargetSymbol = findViewById(R.id.txtTargetSymbol);
        txtAttentionRound = findViewById(R.id.txtAttentionRound);

        txtAttentionDifficulty =
                findViewById(R.id.txtAttentionDifficulty);

        txtAttentionLevelInfo =
                findViewById(R.id.txtAttentionLevelInfo);

        attentionGrid = findViewById(R.id.attentionGrid);

        btnStartAttention =
                findViewById(R.id.btnStartAttention);

        btnChangeAttentionDifficulty =
                findViewById(R.id.btnChangeAttentionDifficulty);

        applyAttentionTheme();

        btnStartAttention.setOnClickListener(v -> {

            if (!difficultySelected) {
                showDifficultyDialog();
            } else if (!gameStarted) {
                startGame();
            } else {
                restartGame();
            }

        });

        btnChangeAttentionDifficulty.setOnClickListener(v -> {

            if (gameStarted) {
                confirmChangeDifficulty();
            } else {
                showDifficultyDialog();
            }

        });

        showReadyScreen();

        showDifficultyDialog();
    }

    // Apply Theme

    private void applyAttentionTheme() {

        int textColor = ThemeManager.getTextColor(this);

        int cardColor = ThemeManager.getCardColor(this);

        int primaryColor = ThemeManager.getPrimaryColor(this);

        View scoreCard = findViewById(R.id.attentionScoreCard);

        View difficultyCard =
                findViewById(R.id.attentionDifficultyCard);

        scoreCard.setBackground(
                createCardBackground(cardColor)
        );

        difficultyCard.setBackground(
                createCardBackground(cardColor)
        );

        TextView title = findViewById(R.id.attentionMain)
                .findViewWithTag("unused");

        txtScore.setTextColor(primaryColor);

        txtAttentionDifficulty.setTextColor(primaryColor);

        txtAttentionRound.setTextColor(
                textColor
        );

        btnChangeAttentionDifficulty.setTextColor(
                primaryColor
        );

    }

    private GradientDrawable createCardBackground(int color) {

        GradientDrawable drawable = new GradientDrawable();

        drawable.setColor(color);

        drawable.setCornerRadius(24f);

        drawable.setStroke(
                1,
                ThemeManager.getSelectedTheme(this).equals(ThemeManager.DARK)
                        ? Color.parseColor("#444444")
                        : Color.parseColor("#F5EAE5")
        );

        return drawable;
    }

    // Difficulty Selection Dialog

    private void showDifficultyDialog() {

        View dialogView = getLayoutInflater().inflate(
                R.layout.dialog_attention_difficulty,
                null
        );

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .setCancelable(false)
                .create();

        LinearLayout easy =
                dialogView.findViewById(R.id.attentionLevelEasy);

        LinearLayout medium =
                dialogView.findViewById(R.id.attentionLevelMedium);

        LinearLayout hard =
                dialogView.findViewById(R.id.attentionLevelHard);

        easy.setOnClickListener(v -> {

            setDifficulty("Easy");

            dialog.dismiss();

        });

        medium.setOnClickListener(v -> {

            setDifficulty("Medium");

            dialog.dismiss();

        });

        hard.setOnClickListener(v -> {

            setDifficulty("Hard");

            dialog.dismiss();

        });

        dialog.setOnShowListener(d -> {

            if (dialog.getWindow() != null) {

                dialog.getWindow().setBackgroundDrawableResource(
                        android.R.color.transparent
                );

                dialog.getWindow().setLayout(
                        (int) (getResources().getDisplayMetrics().widthPixels * 0.90),
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
            }

        });

        dialog.show();

    }

    // Set Difficulty

    private void setDifficulty(String selectedDifficulty) {

        difficulty = selectedDifficulty;

        difficultySelected = true;

        switch (difficulty) {

            case "Easy":

                totalRounds = 5;

                symbolCount = 4;

                break;

            case "Medium":

                totalRounds = 10;

                symbolCount = 6;

                break;

            case "Hard":

                totalRounds = 15;

                symbolCount = 9;

                break;

        }

        txtAttentionDifficulty.setText(difficulty);

        txtAttentionLevelInfo.setText(
                totalRounds + " Rounds • " +
                        symbolCount + " Symbols"
        );

        showReadyScreen();

        Toast.makeText(
                this,
                difficulty + " Level Selected!",
                Toast.LENGTH_SHORT
        ).show();

    }

    // Ready Screen

    private void showReadyScreen() {

        attentionGrid.removeAllViews();

        txtTargetSymbol.setText("⭐");

        txtAttentionRound.setText("Ready to begin?");

        txtScore.setText("0");

        btnStartAttention.setText("Start Training");

    }

    // Start Game

    private void startGame() {

        if (!difficultySelected) {

            showDifficultyDialog();

            return;
        }

        score = 0;

        round = 1;

        gameStarted = true;

        resultSaved = false;

        txtScore.setText("0");

        btnStartAttention.setText("Restart Game");

        loadRound();

    }

    // Load Round

    private void loadRound() {

        if (round > totalRounds) {

            gameCompleted();

            return;
        }

        attentionGrid.removeAllViews();

        txtAttentionRound.setText(
                "Round " + round + " / " + totalRounds
        );

        ArrayList<String> options = new ArrayList<>();

        while (options.size() < symbolCount) {

            String symbol = symbols[
                    random.nextInt(symbols.length)
                    ];

            if (!options.contains(symbol)) {

                options.add(symbol);

            }

        }

        targetSymbol = options.get(
                random.nextInt(options.size())
        );

        txtTargetSymbol.setText(targetSymbol);

        Collections.shuffle(options);

        int columns = 3;

        int rows = (int) Math.ceil(symbolCount / 3.0);

        attentionGrid.setColumnCount(columns);

        attentionGrid.setRowCount(rows);

        for (int i = 0; i < options.size(); i++) {

            String option = options.get(i);

            Button symbolButton = new Button(this);

            GridLayout.LayoutParams params =
                    new GridLayout.LayoutParams();

            params.width = 0;

            params.height = 0;

            params.columnSpec = GridLayout.spec(
                    i % columns,
                    1f
            );

            params.rowSpec = GridLayout.spec(
                    i / columns,
                    1f
            );

            params.setMargins(8, 8, 8, 8);

            symbolButton.setLayoutParams(params);

            symbolButton.setText(option);

            symbolButton.setTextSize(30);

            symbolButton.setGravity(Gravity.CENTER);

            symbolButton.setAllCaps(false);

            symbolButton.setPadding(0, 0, 0, 0);

            symbolButton.setBackground(
                    createBackground(
                            ThemeManager.getCardColor(this)
                    )
            );

            symbolButton.setTextColor(
                    Color.parseColor("#292321")
            );

            symbolButton.setTypeface(
                    null,
                    Typeface.BOLD
            );

            symbolButton.setOnClickListener(v -> {

                checkAnswer(option);

            });

            attentionGrid.addView(symbolButton);

        }

    }

    // Symbol Button Background

    private GradientDrawable createBackground(int color) {

        GradientDrawable drawable =
                new GradientDrawable();

        drawable.setColor(color);

        drawable.setCornerRadius(25f);

        drawable.setStroke(
                2,
                ThemeManager.getSelectedTheme(this).equals(ThemeManager.DARK)
                        ? Color.parseColor("#444444")
                        : Color.parseColor("#F5EAE5")
        );

        return drawable;

    }

    private GradientDrawable createBackground(String color) {

        return createBackground(Color.parseColor(color));

    }

    // Check Answer

    private void checkAnswer(String selectedSymbol) {

        if (!gameStarted) {

            return;
        }

        if (selectedSymbol.equals(targetSymbol)) {

            score += 10;

            Toast.makeText(
                    this,
                    "Correct! +10 Points 🎉",
                    Toast.LENGTH_SHORT
            ).show();

        } else {

            score -= 5;

            Toast.makeText(
                    this,
                    "Wrong Answer! -5 Points",
                    Toast.LENGTH_SHORT
            ).show();

        }

        txtScore.setText(String.valueOf(score));

        round++;

        loadRound();

    }

    // Confirm Change Difficulty

    private void confirmChangeDifficulty() {

        new AlertDialog.Builder(this)

                .setTitle("Change Difficulty?")

                .setMessage(
                        "Your current game progress will be lost. " +
                                "Do you want to change the difficulty?"
                )

                .setPositiveButton(
                        "Continue",
                        (dialog, which) -> {

                            gameStarted = false;

                            resultSaved = false;

                            showDifficultyDialog();

                        }
                )

                .setNegativeButton(
                        "Cancel",
                        null
                )

                .show();

    }

    // Save Progress and History

    private void saveProgress() {

        if (resultSaved) {

            return;
        }

        resultSaved = true;

        SharedPreferences preferences =
                getSharedPreferences(
                        "NeuroCareStats",
                        MODE_PRIVATE
                );

        int totalSessions =
                preferences.getInt("totalSessions", 0);

        int totalScore =
                preferences.getInt("totalScore", 0);

        int attentionSessions =
                preferences.getInt("attentionSessions", 0);

        preferences.edit()

                .putInt("totalSessions", totalSessions + 1)

                .putInt("totalScore", totalScore + score)

                .putInt("attentionSessions", attentionSessions + 1)

                .apply();

        String gameResult =
                "Difficulty: " + difficulty +
                        " | Score: " + score +
                        " | Rounds: " + totalRounds;

        SessionHistoryManager.saveSession(

                this,

                "Attention Training",

                score,

                gameResult

        );

    }

    // Game Completed

    private void gameCompleted() {

        gameStarted = false;

        saveProgress();

        attentionGrid.removeAllViews();

        txtTargetSymbol.setText("🏆");

        txtAttentionRound.setText("Training Completed!");

        btnStartAttention.setText("Play Again");

        new AlertDialog.Builder(this)

                .setTitle("Training Completed! 🎉")

                .setMessage(

                        "Great Job!\n\n" +

                                "Difficulty: " + difficulty +

                                "\nYour Final Score: " + score +

                                "\nRounds Completed: " + totalRounds

                )

                .setCancelable(false)

                .setPositiveButton(

                        "Play Again",

                        (dialog, which) -> restartGame()

                )

                .setNegativeButton(

                        "Finish",

                        (dialog, which) -> finish()

                )

                .show();

    }

    // Restart Game

    private void restartGame() {

        score = 0;

        round = 1;

        gameStarted = false;

        resultSaved = false;

        showReadyScreen();

        Toast.makeText(

                this,

                "Ready for a new challenge!",

                Toast.LENGTH_SHORT

        ).show();

    }

    @Override
    protected void onResume() {

        super.onResume();

        ThemeManager.applyTheme(
                this,
                R.id.attentionMain
        );

        applyAttentionTheme();

    }

}