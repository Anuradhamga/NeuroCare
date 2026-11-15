package com.example.neurocare;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private TextView navGames;
    private TextView navProgress;
    private TextView navHistory;
    private TextView navProfile;

    private TextView txtGreeting;
    private TextView btnSettings;

    private Handler greetingHandler =
            new Handler(Looper.getMainLooper());

    private Runnable greetingRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(
                findViewById(R.id.main), (v, insets) -> {

                    Insets systemBars = insets.getInsets(
                            WindowInsetsCompat.Type.systemBars()
                    );

                    v.setPadding(
                            systemBars.left,
                            systemBars.top,
                            systemBars.right,
                            systemBars.bottom
                    );

                    return insets;
                }
        );

        // Apply Theme
        applyAppTheme();

        // Greeting
        txtGreeting = findViewById(R.id.txtGreeting);

        // Settings
        btnSettings = findViewById(R.id.btnSettings);

        btnSettings.setOnClickListener(v -> {
            startActivity(new Intent(
                    MainActivity.this,
                    SettingsActivity.class
            ));
        });

        // Navigation Views
        navGames = findViewById(R.id.navGames);
        navProgress = findViewById(R.id.navProgress);
        navHistory = findViewById(R.id.navHistory);
        navProfile = findViewById(R.id.navProfile);

        TextView btnSeeAll = findViewById(R.id.btnSeeAll);

        // Games Navigation
        navGames.setOnClickListener(v -> {
            startActivity(new Intent(
                    MainActivity.this,
                    GamesActivity.class
            ));
        });

        // Progress Navigation
        navProgress.setOnClickListener(v -> {
            startActivity(new Intent(
                    MainActivity.this,
                    ProgressActivity.class
            ));
        });

        // History Navigation
        navHistory.setOnClickListener(v -> {
            startActivity(new Intent(
                    MainActivity.this,
                    HistoryActivity.class
            ));
        });

        // See All Navigation
        btnSeeAll.setOnClickListener(v -> {
            startActivity(new Intent(
                    MainActivity.this,
                    HistoryActivity.class
            ));
        });

        // Profile Navigation
        navProfile.setOnClickListener(v -> {
            startActivity(new Intent(
                    MainActivity.this,
                    SettingsActivity.class
            ));
        });

        // Automatic Greeting Update
        greetingRunnable = new Runnable() {
            @Override
            public void run() {

                updateGreeting();

                greetingHandler.postDelayed(
                        this,
                        30000
                );
            }
        };
    }

    private void applyAppTheme() {

        ThemeManager.applyTheme(
                this,
                R.id.main
        );
    }

    private void updateGreeting() {

        Calendar calendar = Calendar.getInstance();

        int hour = calendar.get(Calendar.HOUR_OF_DAY);

        String greeting;

        if (hour >= 5 && hour < 12) {

            greeting = "GOOD MORNING ☀️";

        } else if (hour >= 12 && hour < 17) {

            greeting = "GOOD AFTERNOON 🌤️";

        } else if (hour >= 17 && hour < 19) {

            greeting = "GOOD EVENING 🌅";

        } else {

            greeting = "GOOD NIGHT 🌙";
        }

        if (txtGreeting != null) {
            txtGreeting.setText(greeting);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        applyAppTheme();

        if (txtGreeting != null) {
            updateGreeting();
        }

        if (greetingRunnable != null) {
            greetingHandler.removeCallbacks(greetingRunnable);
            greetingHandler.post(greetingRunnable);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (greetingRunnable != null) {
            greetingHandler.removeCallbacks(greetingRunnable);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        greetingHandler.removeCallbacksAndMessages(null);
    }
}