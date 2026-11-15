package com.example.neurocare;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    // Theme Layouts
    private LinearLayout themeLight;
    private LinearLayout themeDark;
    private LinearLayout themeBlue;
    private LinearLayout themePurple;

    // Theme Check Marks
    private TextView checkLight;
    private TextView checkDark;
    private TextView checkBlue;
    private TextView checkPurple;

    // Profile Views
    private TextView txtProfileName;
    private TextView txtProfileEmail;
    private TextView txtProfileInitials;

    // Accordion Headers
    private LinearLayout headerPersonal;
    private LinearLayout headerAppearance;
    private LinearLayout headerAbout;

    // Accordion Contents
    private LinearLayout contentPersonal;
    private LinearLayout contentAppearance;
    private LinearLayout contentAbout;

    // Accordion Arrows
    private TextView arrowPersonal;
    private TextView arrowAppearance;
    private TextView arrowAbout;

    // Navigation
    private TextView navHome;
    private TextView navGames;
    private TextView navProgress;
    private TextView navHistory;
    private TextView navProfile;

    // Preferences
    private SharedPreferences preferences;
    private SharedPreferences userPreferences;

    private String selectedTheme;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_settings);

        // Preferences
        preferences = getSharedPreferences(
                "NeuroCareSettings",
                MODE_PRIVATE
        );

        userPreferences = getSharedPreferences(
                "NeuroCareUser",
                MODE_PRIVATE
        );

        initializeViews();

        loadUserProfile();

        setupAccordion();

        selectedTheme = ThemeManager.getSelectedTheme(this);

        applyCurrentTheme();

        updateSelectedTheme();

        setupThemeListeners();

        setupBottomNavigation();
    }


    // ==========================================
    // INITIALIZE VIEWS
    // ==========================================

    private void initializeViews() {

        // Profile
        txtProfileName = findViewById(R.id.txtProfileName);
        txtProfileEmail = findViewById(R.id.txtProfileEmail);
        txtProfileInitials = findViewById(R.id.txtProfileInitials);

        // Themes
        themeLight = findViewById(R.id.themeLight);
        themeDark = findViewById(R.id.themeDark);
        themeBlue = findViewById(R.id.themeBlue);
        themePurple = findViewById(R.id.themePurple);

        checkLight = findViewById(R.id.checkLight);
        checkDark = findViewById(R.id.checkDark);
        checkBlue = findViewById(R.id.checkBlue);
        checkPurple = findViewById(R.id.checkPurple);

        // Accordion Headers
        headerPersonal = findViewById(R.id.headerPersonal);
        headerAppearance = findViewById(R.id.headerAppearance);
        headerAbout = findViewById(R.id.headerAbout);

        // Accordion Contents
        contentPersonal = findViewById(R.id.contentPersonal);
        contentAppearance = findViewById(R.id.contentAppearance);
        contentAbout = findViewById(R.id.contentAbout);

        // Arrows
        arrowPersonal = findViewById(R.id.arrowPersonal);
        arrowAppearance = findViewById(R.id.arrowAppearance);
        arrowAbout = findViewById(R.id.arrowAbout);

        // Bottom Navigation
        navHome = findViewById(R.id.navHome);
        navGames = findViewById(R.id.navGames);
        navProgress = findViewById(R.id.navProgress);
        navHistory = findViewById(R.id.navHistory);
        navProfile = findViewById(R.id.navProfile);
    }


    // ==========================================
    // ACCORDION
    // ==========================================

    private void setupAccordion() {

        headerPersonal.setOnClickListener(v -> {

            toggleSection(contentPersonal, arrowPersonal);

        });

        headerAppearance.setOnClickListener(v -> {

            toggleSection(contentAppearance, arrowAppearance);

        });

        headerAbout.setOnClickListener(v -> {

            toggleSection(contentAbout, arrowAbout);

        });

    }


    private void toggleSection(
            LinearLayout selectedContent,
            TextView selectedArrow) {

        boolean isOpening =
                selectedContent.getVisibility() != View.VISIBLE;

        // Close all sections
        contentPersonal.setVisibility(View.GONE);
        contentAppearance.setVisibility(View.GONE);
        contentAbout.setVisibility(View.GONE);

        // Reset arrows
        arrowPersonal.setText("⌄");
        arrowAppearance.setText("⌄");
        arrowAbout.setText("⌄");

        // Open selected section
        if (isOpening) {

            selectedContent.setVisibility(View.VISIBLE);

            selectedArrow.setText("⌃");

        }

    }


    // ==========================================
    // USER PROFILE
    // ==========================================

    private void loadUserProfile() {

        String userName = userPreferences.getString(
                "name",
                "NeuroCare User"
        );

        String userEmail = userPreferences.getString(
                "email",
                "Welcome to NeuroCare"
        );

        txtProfileName.setText(userName);

        txtProfileEmail.setText(userEmail);

        txtProfileInitials.setText(getInitials(userName));

    }


    private String getInitials(String name) {

        if (name == null || name.trim().isEmpty()) {

            return "NC";

        }

        String[] words = name.trim().split("\\s+");

        if (words.length == 1) {

            return words[0].substring(0, 1).toUpperCase();

        }

        return (
                words[0].substring(0, 1) +
                        words[words.length - 1].substring(0, 1)
        ).toUpperCase();

    }


    // ==========================================
    // THEME LISTENERS
    // ==========================================

    private void setupThemeListeners() {

        themeLight.setOnClickListener(v -> selectTheme("light"));

        themeDark.setOnClickListener(v -> selectTheme("dark"));

        themeBlue.setOnClickListener(v -> selectTheme("blue"));

        themePurple.setOnClickListener(v -> selectTheme("purple"));

    }


    // ==========================================
    // SELECT THEME
    // ==========================================

    private void selectTheme(String theme) {

        selectedTheme = theme;

        preferences.edit()
                .putString("appTheme", theme)
                .apply();

        applyCurrentTheme();

        updateSelectedTheme();

        Toast.makeText(
                this,
                "Theme selected successfully!",
                Toast.LENGTH_SHORT
        ).show();

    }


    // ==========================================
    // APPLY THEME
    // ==========================================

    private void applyCurrentTheme() {

        ThemeManager.applyTheme(
                this,
                R.id.settingsMain
        );

    }


    // ==========================================
    // UPDATE THEME CHECK MARKS
    // ==========================================

    private void updateSelectedTheme() {

        checkLight.setVisibility(View.GONE);
        checkDark.setVisibility(View.GONE);
        checkBlue.setVisibility(View.GONE);
        checkPurple.setVisibility(View.GONE);

        switch (selectedTheme) {

            case "dark":

                checkDark.setVisibility(View.VISIBLE);

                break;

            case "blue":

                checkBlue.setVisibility(View.VISIBLE);

                break;

            case "purple":

                checkPurple.setVisibility(View.VISIBLE);

                break;

            default:

                checkLight.setVisibility(View.VISIBLE);

                break;

        }

    }


    // ==========================================
    // BOTTOM NAVIGATION
    // ==========================================

    private void setupBottomNavigation() {

        // Home
        navHome.setOnClickListener(v -> {

            Intent intent = new Intent(
                    SettingsActivity.this,
                    MainActivity.class
            );

            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            startActivity(intent);

            finish();

        });


        // Games
        navGames.setOnClickListener(v -> {

            startActivity(new Intent(
                    SettingsActivity.this,
                    GamesActivity.class
            ));

            finish();

        });


        // Progress
        navProgress.setOnClickListener(v -> {

            startActivity(new Intent(
                    SettingsActivity.this,
                    ProgressActivity.class
            ));

            finish();

        });


        // History
        navHistory.setOnClickListener(v -> {

            startActivity(new Intent(
                    SettingsActivity.this,
                    HistoryActivity.class
            ));

            finish();

        });


        // Profile
        navProfile.setOnClickListener(v -> {

            // Already in Profile page

        });

    }


    // ==========================================
    // REFRESH PROFILE
    // ==========================================

    @Override
    protected void onResume() {

        super.onResume();

        if (txtProfileName != null) {

            loadUserProfile();

        }

    }

}