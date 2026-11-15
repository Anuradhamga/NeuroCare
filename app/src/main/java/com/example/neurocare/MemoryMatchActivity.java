package com.example.neurocare;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.app.AlertDialog;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

public class MemoryMatchActivity extends AppCompatActivity {

    private GridLayout cardGrid;

    private TextView txtScore;
    private TextView txtTime;
    private TextView txtMoves;
    private TextView txtDifficulty;
    private TextView txtPairs;

    private Button btnRestart;
    private Button btnChangeDifficulty;

    private final String[] emojis = {
            "🧠", "❤️", "⭐", "🌸",
            "🚀", "🎵", "🍎", "🐼",
            "⚽", "🌈", "🐱", "🔥"
    };

    private ArrayList<String> cards;

    private Button firstCard = null;
    private Button secondCard = null;

    private String firstValue = "";

    private String selectedDifficulty = "Medium";

    private int pairsCount = 8;
    private int totalCards = 16;

    private int score = 0;
    private int moves = 0;
    private int matchedPairs = 0;
    private int seconds = 0;

    private boolean isBusy = false;
    private boolean gameStarted = false;
    private boolean resultSaved = false;

    private Handler handler =
            new Handler(Looper.getMainLooper());

    private final Runnable timerRunnable = new Runnable() {

        @Override
        public void run() {

            if (gameStarted) {

                seconds++;

                int minutes = seconds / 60;
                int remainingSeconds = seconds % 60;

                txtTime.setText(String.format(
                        Locale.getDefault(),
                        "%02d:%02d",
                        minutes,
                        remainingSeconds
                ));

                handler.postDelayed(this, 1000);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_memory_match);

        cardGrid = findViewById(R.id.cardGrid);

        txtScore = findViewById(R.id.txtScore);
        txtTime = findViewById(R.id.txtTime);
        txtMoves = findViewById(R.id.txtMoves);

        txtDifficulty = findViewById(R.id.txtDifficulty);
        txtPairs = findViewById(R.id.txtPairs);

        btnRestart = findViewById(R.id.btnRestart);

        btnChangeDifficulty =
                findViewById(R.id.btnChangeDifficulty);

        btnRestart.setOnClickListener(v -> restartGame());

        btnChangeDifficulty.setOnClickListener(v ->
                confirmChangeDifficulty()
        );

        applyMemoryTheme();

        showDifficultyDialog();
    }

    // Theme Support

    private void applyMemoryTheme() {

        ThemeManager.applyTheme(
                this,
                R.id.memoryMain
        );

        int primaryColor =
                ThemeManager.getPrimaryColor(this);

        int cardColor =
                ThemeManager.getCardColor(this);

        int textColor =
                ThemeManager.getTextColor(this);

        LinearLayout scoreCard =
                findViewById(R.id.scoreCard);

        LinearLayout timeCard =
                findViewById(R.id.timeCard);

        scoreCard.setBackground(
                createRoundedBackground(
                        cardColor,
                        "#EAEAEA"
                )
        );

        timeCard.setBackground(
                createRoundedBackground(
                        cardColor,
                        "#EAEAEA"
                )
        );

        txtScore.setTextColor(primaryColor);
        txtTime.setTextColor(primaryColor);
        txtDifficulty.setTextColor(primaryColor);

        txtMoves.setTextColor(
                currentSecondaryColor()
        );

        txtPairs.setTextColor(
                currentSecondaryColor()
        );

        btnChangeDifficulty.setBackgroundTintList(
                android.content.res.ColorStateList.valueOf(
                        primaryColor
                )
        );
    }

    private int currentSecondaryColor() {

        if (ThemeManager.getSelectedTheme(this).equals("dark")) {
            return Color.parseColor("#BDBDBD");
        }

        return Color.parseColor("#89817D");
    }

    private GradientDrawable createRoundedBackground(
            int color,
            String borderColor
    ) {

        GradientDrawable drawable =
                new GradientDrawable();

        drawable.setColor(color);

        drawable.setCornerRadius(dp(22));

        drawable.setStroke(
                dp(1),
                Color.parseColor(borderColor)
        );

        return drawable;
    }

    // Difficulty Selection

    private void showDifficultyDialog() {

        View dialogView =
                LayoutInflater.from(this).inflate(
                        R.layout.dialog_difficulty,
                        null
                );

        AlertDialog dialog =
                new AlertDialog.Builder(this)
                        .setView(dialogView)
                        .create();

        dialog.setCancelable(false);

        dialog.show();

        if (dialog.getWindow() != null) {

            dialog.getWindow().setBackgroundDrawableResource(
                    android.R.color.transparent
            );

            dialog.getWindow().setLayout(
                    (int) (getResources()
                            .getDisplayMetrics().widthPixels * 0.90),
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
        }

        dialogView.findViewById(R.id.levelEasy)
                .setOnClickListener(v -> {

                    selectedDifficulty = "Easy";
                    pairsCount = 4;
                    totalCards = 8;

                    dialog.dismiss();

                    startGame();
                });

        dialogView.findViewById(R.id.levelMedium)
                .setOnClickListener(v -> {

                    selectedDifficulty = "Medium";
                    pairsCount = 8;
                    totalCards = 16;

                    dialog.dismiss();

                    startGame();
                });

        dialogView.findViewById(R.id.levelHard)
                .setOnClickListener(v -> {

                    selectedDifficulty = "Hard";
                    pairsCount = 12;
                    totalCards = 24;

                    dialog.dismiss();

                    startGame();
                });
    }

    // Confirm Difficulty Change

    private void confirmChangeDifficulty() {

        if (!gameStarted && matchedPairs == 0) {

            showDifficultyDialog();

            return;
        }

        new AlertDialog.Builder(this)

                .setTitle("Change Difficulty?")

                .setMessage(
                        "Your current game progress will be lost. " +
                                "Do you want to continue?"
                )

                .setPositiveButton(
                        "Continue",
                        (dialog, which) -> {

                            handler.removeCallbacks(timerRunnable);

                            gameStarted = false;

                            showDifficultyDialog();
                        }
                )

                .setNegativeButton(
                        "Cancel",
                        null
                )

                .show();
    }

    // Start Game

    private void startGame() {

        handler.removeCallbacks(timerRunnable);

        score = 0;
        moves = 0;
        matchedPairs = 0;
        seconds = 0;

        gameStarted = false;
        isBusy = false;
        resultSaved = false;

        firstCard = null;
        secondCard = null;
        firstValue = "";

        txtScore.setText("0");
        txtTime.setText("00:00");

        txtMoves.setText("Moves: 0");

        txtDifficulty.setText(selectedDifficulty);

        txtPairs.setText(pairsCount + " Pairs");

        cardGrid.removeAllViews();

        cardGrid.setColumnCount(4);

        cardGrid.setRowCount(totalCards / 4);

        cards = new ArrayList<>();

        for (int i = 0; i < pairsCount; i++) {

            cards.add(emojis[i]);
            cards.add(emojis[i]);
        }

        Collections.shuffle(cards);

        for (int i = 0; i < cards.size(); i++) {

            Button card = new Button(this);

            GridLayout.LayoutParams params =
                    new GridLayout.LayoutParams();

            params.width = 0;
            params.height = 0;

            params.columnSpec =
                    GridLayout.spec(
                            GridLayout.UNDEFINED,
                            1f
                    );

            params.rowSpec =
                    GridLayout.spec(
                            GridLayout.UNDEFINED,
                            1f
                    );

            params.setMargins(
                    dp(4),
                    dp(4),
                    dp(4),
                    dp(4)
            );

            card.setLayoutParams(params);

            card.setText("?");

            card.setTextSize(
                    selectedDifficulty.equals("Hard")
                            ? 22
                            : 27
            );

            card.setTextColor(Color.WHITE);

            card.setTypeface(
                    null,
                    Typeface.BOLD
            );

            card.setGravity(Gravity.CENTER);

            card.setAllCaps(false);

            card.setPadding(0, 0, 0, 0);

            card.setBackground(
                    createCardBackground(
                            ThemeManager.getPrimaryColor(this)
                    )
            );

            final int position = i;

            card.setOnClickListener(v ->
                    flipCard(card, position)
            );

            cardGrid.addView(card);
        }
    }

    // Card Background

    private GradientDrawable createCardBackground(
            int color
    ) {

        GradientDrawable drawable =
                new GradientDrawable();

        drawable.setColor(color);

        drawable.setCornerRadius(dp(17));

        return drawable;
    }

    // Flip Card

    private void flipCard(
            Button card,
            int position
    ) {

        if (isBusy) {
            return;
        }

        if (card == firstCard ||
                card == secondCard) {

            return;
        }

        if (card.getText().toString()
                .equals(cards.get(position))) {

            return;
        }

        if (!gameStarted) {

            gameStarted = true;

            handler.post(timerRunnable);
        }

        String selectedEmoji = cards.get(position);

        card.setText(selectedEmoji);

        card.setTextColor(
                Color.parseColor("#292321")
        );

        card.setBackground(
                createCardBackground(Color.WHITE)
        );

        if (firstCard == null) {

            firstCard = card;

            firstValue = selectedEmoji;

        } else {

            secondCard = card;

            moves++;

            txtMoves.setText("Moves: " + moves);

            checkMatch();
        }
    }

    // Check Match

    private void checkMatch() {

        if (firstValue.equals(
                secondCard.getText().toString()
        )) {

            score += 10;

            matchedPairs++;

            txtScore.setText(
                    String.valueOf(score)
            );

            firstCard.setBackground(
                    createCardBackground(
                            Color.parseColor("#D9F5E5")
                    )
            );

            secondCard.setBackground(
                    createCardBackground(
                            Color.parseColor("#D9F5E5")
                    )
            );

            firstCard.setEnabled(false);
            secondCard.setEnabled(false);

            firstCard = null;
            secondCard = null;

            firstValue = "";

            if (matchedPairs == pairsCount) {

                gameCompleted();
            }

        } else {

            isBusy = true;

            handler.postDelayed(() -> {

                if (firstCard != null &&
                        secondCard != null) {

                    int primaryColor =
                            ThemeManager.getPrimaryColor(this);

                    firstCard.setText("?");

                    firstCard.setTextColor(Color.WHITE);

                    firstCard.setBackground(
                            createCardBackground(primaryColor)
                    );

                    secondCard.setText("?");

                    secondCard.setTextColor(Color.WHITE);

                    secondCard.setBackground(
                            createCardBackground(primaryColor)
                    );
                }

                firstCard = null;
                secondCard = null;

                firstValue = "";

                isBusy = false;

            }, 900);
        }
    }

    // Save Progress

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

        int memorySessions =
                preferences.getInt("memorySessions", 0);

        preferences.edit()

                .putInt(
                        "totalSessions",
                        totalSessions + 1
                )

                .putInt(
                        "totalScore",
                        totalScore + score
                )

                .putInt(
                        "memorySessions",
                        memorySessions + 1
                )

                .apply();

        String gameResult =

                "Difficulty: " + selectedDifficulty +

                        " | Score: " + score +

                        " | Moves: " + moves +

                        " | Time: " + txtTime.getText().toString();

        SessionHistoryManager.saveSession(
                this,
                "Memory Match",
                score,
                gameResult
        );
    }

    // Game Completed

    private void gameCompleted() {

        gameStarted = false;

        handler.removeCallbacks(timerRunnable);

        saveProgress();

        new AlertDialog.Builder(this)

                .setTitle("Congratulations! 🎉")

                .setMessage(

                        "You completed Memory Match!\n\n" +

                                "Difficulty: " + selectedDifficulty +

                                "\nScore: " + score +

                                "\nMoves: " + moves +

                                "\nTime: " + txtTime.getText()
                )

                .setCancelable(false)

                .setPositiveButton(
                        "Play Again",
                        (dialog, which) -> {

                            showDifficultyDialog();
                        }
                )

                .setNegativeButton(
                        "Finish",
                        (dialog, which) -> finish()
                )

                .show();
    }

    // Restart Game

    private void restartGame() {

        startGame();

        Toast.makeText(
                this,
                selectedDifficulty + " Level Restarted!",
                Toast.LENGTH_SHORT
        ).show();
    }

    private int dp(int value) {

        return (int) (
                value * getResources()
                        .getDisplayMetrics()
                        .density
        );
    }

    @Override
    protected void onResume() {

        super.onResume();

        applyMemoryTheme();
    }

    @Override
    protected void onDestroy() {

        handler.removeCallbacksAndMessages(null);

        super.onDestroy();
    }
}