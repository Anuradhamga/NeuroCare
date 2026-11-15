package com.example.neurocare;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.content.SharedPreferences;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;

public class OddOneOutActivity extends AppCompatActivity {

    private TextView txtScore, txtTimer, txtRound;
    private TextView txtQuestion, txtFeedback;
    private GridLayout gridGame;
    private Button btnStart, btnBack;
    private RadioGroup radioDifficulty;

    private CountDownTimer countDownTimer;
    private final Handler handler = new Handler();
    private final Random random = new Random();

    private int score = 0;
    private int timeLeft = 60;
    private int currentRound = 1;
    private int totalRounds = 10;
    private int gridSize = 3;

    private int correctPosition;
    private int totalAttempts = 0;
    private int correctAnswers = 0;

    private boolean gameRunning = false;
    private boolean answerLocked = false;

    private String difficulty = "Easy";

    private final String[][] symbolGroups = {
            {"★", "☆"},
            {"●", "○"},
            {"■", "□"},
            {"▲", "△"},
            {"♥", "♡"},
            {"◆", "◇"},
            {"✚", "✖"},
            {"☀", "☼"}
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_odd_one_out);

        ThemeManager.applyTheme(this, android.R.id.content);

        initializeViews();
        setupListeners();
        updateStats();

        txtFeedback.setText("Choose a difficulty and press Start!");
    }

    private void initializeViews() {

        txtScore = findViewById(R.id.txtScore);
        txtTimer = findViewById(R.id.txtTimer);
        txtRound = findViewById(R.id.txtRound);

        txtQuestion = findViewById(R.id.txtQuestion);
        txtFeedback = findViewById(R.id.txtFeedback);

        gridGame = findViewById(R.id.gridGame);

        btnStart = findViewById(R.id.btnStart);
        btnBack = findViewById(R.id.btnBack);

        radioDifficulty = findViewById(R.id.radioDifficulty);
    }

    private void setupListeners() {

        btnStart.setOnClickListener(v -> startGame());

        btnBack.setOnClickListener(v -> finish());

        radioDifficulty.setOnCheckedChangeListener((group, checkedId) -> {

            if (checkedId == R.id.radioEasy) {
                difficulty = "Easy";
                gridSize = 3;

            } else if (checkedId == R.id.radioMedium) {
                difficulty = "Medium";
                gridSize = 4;

            } else if (checkedId == R.id.radioHard) {
                difficulty = "Hard";
                gridSize = 5;
            }

            if (gameRunning) {
                startGame();
            }
        });
    }

    private void startGame() {

        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        handler.removeCallbacksAndMessages(null);

        score = 0;
        timeLeft = 60;
        currentRound = 1;
        totalAttempts = 0;
        correctAnswers = 0;

        gameRunning = true;
        answerLocked = false;

        radioDifficulty.setEnabled(true);

        for (int i = 0; i < radioDifficulty.getChildCount(); i++) {
            radioDifficulty.getChildAt(i).setEnabled(true);
        }

        btnStart.setText("RESTART GAME");

        txtFeedback.setText("Find the different symbol!");

        updateStats();
        generateQuestion();
        startTimer();
    }

    private void startTimer() {

        countDownTimer = new CountDownTimer(60000, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {

                timeLeft = (int) (millisUntilFinished / 1000);
                txtTimer.setText("Time\n" + timeLeft);
            }

            @Override
            public void onFinish() {

                timeLeft = 0;
                txtTimer.setText("Time\n0");

                finishGame();
            }

        }.start();
    }

    private void generateQuestion() {

        if (!gameRunning) {
            return;
        }

        answerLocked = false;

        gridGame.removeAllViews();

        int groupIndex = random.nextInt(symbolGroups.length);

        String normalSymbol = symbolGroups[groupIndex][0];
        String oddSymbol = symbolGroups[groupIndex][1];

        int totalCells = gridSize * gridSize;

        correctPosition = random.nextInt(totalCells);

        gridGame.setColumnCount(gridSize);
        gridGame.setRowCount(gridSize);

        for (int i = 0; i < totalCells; i++) {

            Button symbolButton = new Button(this);

            if (i == correctPosition) {
                symbolButton.setText(oddSymbol);
            } else {
                symbolButton.setText(normalSymbol);
            }

            symbolButton.setTextSize(26);
            symbolButton.setGravity(Gravity.CENTER);
            symbolButton.setAllCaps(false);

            GridLayout.LayoutParams params =
                    new GridLayout.LayoutParams();

            params.width = 0;
            params.height = dpToPx(75);

            params.columnSpec =
                    GridLayout.spec(GridLayout.UNDEFINED, 1f);

            params.rowSpec =
                    GridLayout.spec(GridLayout.UNDEFINED, 1f);

            params.setMargins(dpToPx(3), dpToPx(3),
                    dpToPx(3), dpToPx(3));

            symbolButton.setLayoutParams(params);

            final int selectedPosition = i;

            symbolButton.setOnClickListener(v -> {

                if (!gameRunning || answerLocked) {
                    return;
                }

                checkAnswer(selectedPosition);
            });

            gridGame.addView(symbolButton);
        }

        txtQuestion.setText(
                "Find the different symbol!"
        );
    }

    private void checkAnswer(int selectedPosition) {

        if (!gameRunning || answerLocked) {
            return;
        }

        totalAttempts++;

        if (selectedPosition == correctPosition) {

            answerLocked = true;

            score += 10;
            correctAnswers++;

            txtFeedback.setText("Correct! +10 points");

            currentRound++;

            updateStats();

            if (currentRound > totalRounds) {

                finishGame();

            } else {

                handler.postDelayed(() -> {

                    if (gameRunning) {
                        generateQuestion();
                    }

                }, 500);
            }

        } else {

            score = Math.max(0, score - 2);

            txtFeedback.setText("Wrong! Try again. -2 points");

            updateStats();
        }
    }

    private void updateStats() {

        txtScore.setText("Score\n" + score);

        txtTimer.setText("Time\n" + timeLeft);

        int displayedRound = Math.min(currentRound, totalRounds);

        txtRound.setText(
                "Round\n" + displayedRound + "/" + totalRounds
        );
    }

    private void finishGame() {

        if (!gameRunning) {
            return;
        }

        gameRunning = false;
        answerLocked = true;

        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        handler.removeCallbacksAndMessages(null);

        gridGame.removeAllViews();

        double accuracy = 0;

        if (totalAttempts > 0) {
            accuracy = (correctAnswers * 100.0) / totalAttempts;
        }

        SharedPreferences preferences =
                getSharedPreferences("OddOneOutPrefs", MODE_PRIVATE);

        int bestScore = preferences.getInt("bestScore", 0);

        if (score > bestScore) {

            bestScore = score;

            preferences.edit()
                    .putInt("bestScore", bestScore)
                    .apply();
        }

        String result = String.format(
                java.util.Locale.US,
                "Difficulty: %s | Accuracy: %.1f%% | Best Score: %d",
                difficulty,
                accuracy,
                bestScore
        );

        SessionHistoryManager.saveSession(
                this,
                "Odd One Out",
                score,
                result
        );

        txtQuestion.setText("Game Completed!");

        txtFeedback.setText(
                "Your Score: " + score +
                        "\nAccuracy: " + String.format(
                        java.util.Locale.US, "%.1f%%", accuracy) +
                        "\nBest Score: " + bestScore
        );

        btnStart.setText("PLAY AGAIN");
    }

    private int dpToPx(int dp) {

        return (int) (
                dp * getResources().getDisplayMetrics().density
        );
    }

    @Override
    protected void onDestroy() {

        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        handler.removeCallbacksAndMessages(null);

        super.onDestroy();
    }
}