package com.example.neurocare;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;

public class ReactionActivity extends AppCompatActivity {

    private TextView txtReactionResult;
    private TextView txtReactionMessage;
    private TextView txtReactionArea;
    private TextView txtReactionDifficulty;
    private TextView txtReactionRound;

    private Button btnStartReaction;
    private Button btnChangeReactionDifficulty;

    private Handler handler = new Handler(Looper.getMainLooper());
    private Random random = new Random();

    private long startTime;

    private boolean isWaiting = false;
    private boolean isReady = false;
    private boolean resultSaved = false;
    private boolean gameStarted = false;

    private int attempts = 0;
    private int totalTime = 0;

    private int maxAttempts = 5;
    private int minDelay = 2000;
    private int maxDelay = 6000;

    private String selectedDifficulty = "Medium";

    private Runnable reactionRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_reaction);

        ThemeManager.applyTheme(this, R.id.reactionMain);

        txtReactionResult = findViewById(R.id.txtReactionResult);
        txtReactionMessage = findViewById(R.id.txtReactionMessage);
        txtReactionArea = findViewById(R.id.txtReactionArea);

        btnStartReaction = findViewById(R.id.btnStartReaction);

        txtReactionDifficulty = findViewById(R.id.txtReactionDifficulty);
        txtReactionRound = findViewById(R.id.txtReactionRound);
        btnChangeReactionDifficulty =
                findViewById(R.id.btnChangeReactionDifficulty);

        btnStartReaction.setOnClickListener(v -> startTest());

        txtReactionArea.setOnClickListener(v -> checkReaction());

        btnChangeReactionDifficulty.setOnClickListener(
                v -> confirmChangeDifficulty()
        );

        showDifficultyDialog();
    }

    // Difficulty Selection

    private void showDifficultyDialog() {

        View dialogView = getLayoutInflater().inflate(
                R.layout.dialog_reaction_difficulty,
                null
        );

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .setCancelable(false)
                .create();

        LinearLayout easy = dialogView.findViewById(
                R.id.reactionLevelEasy
        );

        LinearLayout medium = dialogView.findViewById(
                R.id.reactionLevelMedium
        );

        LinearLayout hard = dialogView.findViewById(
                R.id.reactionLevelHard
        );

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

        dialog.show();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(
                    android.R.color.transparent
            );
        }
    }

    private void setDifficulty(String difficulty) {

        selectedDifficulty = difficulty;

        switch (difficulty) {

            case "Easy":
                maxAttempts = 3;
                minDelay = 2000;
                maxDelay = 4000;
                break;

            case "Medium":
                maxAttempts = 5;
                minDelay = 2000;
                maxDelay = 6000;
                break;

            case "Hard":
                maxAttempts = 10;
                minDelay = 1000;
                maxDelay = 5000;
                break;
        }

        txtReactionDifficulty.setText(
                selectedDifficulty + " Level"
        );

        txtReactionRound.setText(
                "0 / " + maxAttempts + " Attempts"
        );

        resetScreen();
    }

    private void confirmChangeDifficulty() {

        if (gameStarted) {

            new AlertDialog.Builder(this)
                    .setTitle("Change Difficulty?")
                    .setMessage(
                            "Your current game will be restarted. Continue?"
                    )
                    .setNegativeButton("Cancel", null)
                    .setPositiveButton("Continue", (d, w) -> {
                        handler.removeCallbacksAndMessages(null);

                        gameStarted = false;
                        showDifficultyDialog();
                    })
                    .show();

        } else {

            showDifficultyDialog();
        }
    }

    // Reset Screen

    private void resetScreen() {

        attempts = 0;
        totalTime = 0;

        isWaiting = false;
        isReady = false;
        gameStarted = false;
        resultSaved = false;

        txtReactionResult.setText("--- ms");

        txtReactionMessage.setText(
                "Press Start to begin your reaction test!"
        );

        txtReactionRound.setText(
                "0 / " + maxAttempts + " Attempts"
        );

        txtReactionArea.setBackground(
                createBackground("#FF795B")
        );

        txtReactionArea.setText("READY? 🟠");

        btnStartReaction.setEnabled(true);
        btnStartReaction.setText("START TEST");
    }

    // Start Test

    private void startTest() {

        handler.removeCallbacksAndMessages(null);

        attempts = 0;
        totalTime = 0;

        isWaiting = false;
        isReady = false;

        resultSaved = false;
        gameStarted = true;

        txtReactionResult.setText("--- ms");

        txtReactionMessage.setText(
                "Wait for the green signal..."
        );

        txtReactionRound.setText(
                "0 / " + maxAttempts + " Attempts"
        );

        btnStartReaction.setEnabled(false);
        btnStartReaction.setText("TEST RUNNING...");

        nextRound();
    }

    // Next Round

    private void nextRound() {

        isWaiting = true;
        isReady = false;

        txtReactionArea.setBackground(
                createBackground("#E74C3C")
        );

        txtReactionArea.setText("WAIT FOR GREEN 🔴");

        int delay = minDelay +
                random.nextInt(maxDelay - minDelay + 1);

        reactionRunnable = () -> {

            isWaiting = false;
            isReady = true;

            txtReactionArea.setBackground(
                    createBackground("#27AE60")
            );

            txtReactionArea.setText("TAP NOW! 🟢");

            startTime = System.currentTimeMillis();
        };

        handler.postDelayed(reactionRunnable, delay);
    }

    // Check Reaction

    private void checkReaction() {

        if (isWaiting) {

            handler.removeCallbacks(reactionRunnable);

            isWaiting = false;
            isReady = false;
            gameStarted = false;

            txtReactionArea.setBackground(
                    createBackground("#E74C3C")
            );

            txtReactionArea.setText("TOO EARLY!");

            txtReactionMessage.setText(
                    "You tapped too early. Try again!"
            );

            btnStartReaction.setEnabled(true);
            btnStartReaction.setText("TRY AGAIN");

            Toast.makeText(
                    this,
                    "False Start! Press Start to retry.",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        if (isReady) {

            long reactionTime =
                    System.currentTimeMillis() - startTime;

            isReady = false;

            attempts++;

            totalTime += (int) reactionTime;

            txtReactionResult.setText(
                    reactionTime + " ms"
            );

            txtReactionRound.setText(
                    attempts + " / " + maxAttempts + " Attempts"
            );

            txtReactionArea.setBackground(
                    createBackground("#FF795B")
            );

            txtReactionArea.setText("GOOD JOB! 🎉");

            if (attempts < maxAttempts) {

                txtReactionMessage.setText(
                        "Round " + attempts + " completed. Get ready!"
                );

                handler.postDelayed(
                        this::nextRound,
                        1200
                );

            } else {

                gameCompleted();
            }
        }
    }

    // Game Completed

    private void gameCompleted() {

        int average = totalTime / maxAttempts;

        gameStarted = false;

        txtReactionResult.setText(
                average + " ms"
        );

        txtReactionMessage.setText(
                "Average reaction time after " +
                        maxAttempts + " attempts"
        );

        txtReactionArea.setBackground(
                createBackground("#FF795B")
        );

        txtReactionArea.setText("TEST COMPLETED 🏆");

        saveProgress(average);

        btnStartReaction.setEnabled(true);
        btnStartReaction.setText("PLAY AGAIN");

        new AlertDialog.Builder(this)
                .setTitle("Challenge Completed! 🏆")
                .setMessage(
                        "Difficulty: " + selectedDifficulty +
                                "\nAttempts: " + maxAttempts +
                                "\nAverage Reaction Time: " +
                                average + " ms"
                )
                .setPositiveButton("Play Again", (d, w) -> {
                    resetScreen();
                })
                .setNegativeButton("Close", null)
                .show();
    }

    // Save Progress and History

    private void saveProgress(int average) {

        if (resultSaved) {
            return;
        }

        SharedPreferences preferences =
                getSharedPreferences(
                        "NeuroCareStats",
                        MODE_PRIVATE
                );

        int totalSessions = preferences.getInt(
                "totalSessions", 0
        );

        int reactionSessions = preferences.getInt(
                "reactionSessions", 0
        );

        preferences.edit()
                .putInt("totalSessions", totalSessions + 1)
                .putInt("reactionSessions", reactionSessions + 1)
                .putInt("averageReaction", average)
                .apply();

        String gameResult =
                "Difficulty: " + selectedDifficulty +
                        " | Average reaction time: " + average +
                        " ms (" + maxAttempts + " attempts)";

        SessionHistoryManager.saveSession(
                this,
                "Reaction Time",
                average,
                gameResult
        );

        resultSaved = true;

        Toast.makeText(
                this,
                "Your progress has been saved!",
                Toast.LENGTH_SHORT
        ).show();
    }

    // Rounded Background

    private GradientDrawable createBackground(String color) {

        GradientDrawable drawable = new GradientDrawable();

        drawable.setColor(Color.parseColor(color));

        drawable.setCornerRadius(28f);

        return drawable;
    }

    @Override
    protected void onResume() {
        super.onResume();

        ThemeManager.applyTheme(this, R.id.reactionMain);
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();

        handler.removeCallbacksAndMessages(null);
    }
}