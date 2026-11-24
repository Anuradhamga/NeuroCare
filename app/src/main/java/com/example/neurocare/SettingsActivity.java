package com.example.neurocare;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    // Profile
    private TextView txtProfileName, txtProfileEmail, txtProfileInitials;
    private ImageView imgProfile;

    private EditText edtProfileName, edtProfileEmail, edtProfileAge;
    private Spinner spinnerGender;

    private Button btnChoosePhoto, btnSaveProfile;

    // Theme
    private LinearLayout themeLight, themeDark, themeBlue, themePurple;
    private TextView checkLight, checkDark, checkBlue, checkPurple;

    // Accordion
    private LinearLayout headerPersonal, headerAppearance, headerAbout;
    private LinearLayout contentPersonal, contentAppearance, contentAbout;

    private TextView arrowPersonal, arrowAppearance, arrowAbout;

    // Navigation
    private TextView navHome, navGames, navProgress, navHistory, navProfile;

    // Preferences
    private SharedPreferences preferences;
    private SharedPreferences userPreferences;

    private String selectedTheme = "light";
    private String selectedImageUri = "";

    private ActivityResultLauncher<Intent> imagePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_settings);

        preferences = getSharedPreferences(
                "NeuroCareSettings",
                MODE_PRIVATE
        );

        userPreferences = getSharedPreferences(
                "NeuroCareUser",
                MODE_PRIVATE
        );

        initializeViews();

        setupImagePicker();

        loadUserProfile();

        setupProfileListeners();

        setupAccordion();

        selectedTheme = ThemeManager.getSelectedTheme(this);

        applyCurrentTheme();

        updateSelectedTheme();

        setupThemeListeners();

        setupBottomNavigation();
    }

    // =====================================
    // INITIALIZE VIEWS
    // =====================================

    private void initializeViews() {

        txtProfileName = findViewById(R.id.txtProfileName);
        txtProfileEmail = findViewById(R.id.txtProfileEmail);
        txtProfileInitials = findViewById(R.id.txtProfileInitials);

        imgProfile = findViewById(R.id.imgProfile);

        edtProfileName = findViewById(R.id.edtProfileName);
        edtProfileEmail = findViewById(R.id.edtProfileEmail);
        edtProfileAge = findViewById(R.id.edtProfileAge);

        spinnerGender = findViewById(R.id.spinnerGender);

        btnChoosePhoto = findViewById(R.id.btnChoosePhoto);
        btnSaveProfile = findViewById(R.id.btnSaveProfile);

        themeLight = findViewById(R.id.themeLight);
        themeDark = findViewById(R.id.themeDark);
        themeBlue = findViewById(R.id.themeBlue);
        themePurple = findViewById(R.id.themePurple);

        checkLight = findViewById(R.id.checkLight);
        checkDark = findViewById(R.id.checkDark);
        checkBlue = findViewById(R.id.checkBlue);
        checkPurple = findViewById(R.id.checkPurple);

        headerPersonal = findViewById(R.id.headerPersonal);
        headerAppearance = findViewById(R.id.headerAppearance);
        headerAbout = findViewById(R.id.headerAbout);

        contentPersonal = findViewById(R.id.contentPersonal);
        contentAppearance = findViewById(R.id.contentAppearance);
        contentAbout = findViewById(R.id.contentAbout);

        arrowPersonal = findViewById(R.id.arrowPersonal);
        arrowAppearance = findViewById(R.id.arrowAppearance);
        arrowAbout = findViewById(R.id.arrowAbout);

        navHome = findViewById(R.id.navHome);
        navGames = findViewById(R.id.navGames);
        navProgress = findViewById(R.id.navProgress);
        navHistory = findViewById(R.id.navHistory);
        navProfile = findViewById(R.id.navProfile);

        String[] genders = {
                "Select Gender",
                "Male",
                "Female",
                "Other",
                "Prefer not to say"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                genders
        );

        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spinnerGender.setAdapter(adapter);
    }

    // =====================================
    // IMAGE PICKER
    // =====================================

    private void setupImagePicker() {

        imagePicker = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {

                    if (result.getResultCode() == RESULT_OK
                            && result.getData() != null) {

                        Intent data = result.getData();

                        Uri imageUri = data.getData();

                        if (imageUri != null) {

                            try {

                                int flags = data.getFlags()
                                        & Intent.FLAG_GRANT_READ_URI_PERMISSION;

                                getContentResolver().takePersistableUriPermission(
                                        imageUri,
                                        flags
                                );

                                selectedImageUri = imageUri.toString();

                                imgProfile.setImageURI(imageUri);

                                imgProfile.setVisibility(View.VISIBLE);

                                txtProfileInitials.setVisibility(View.GONE);

                            } catch (Exception e) {

                                try {

                                    selectedImageUri = imageUri.toString();

                                    imgProfile.setImageURI(imageUri);

                                    imgProfile.setVisibility(View.VISIBLE);

                                    txtProfileInitials.setVisibility(View.GONE);

                                } catch (Exception ex) {

                                    Toast.makeText(
                                            this,
                                            "Unable to load image.",
                                            Toast.LENGTH_SHORT
                                    ).show();
                                }
                            }
                        }
                    }
                }
        );
    }

    // =====================================
    // PROFILE
    // =====================================

    private void loadUserProfile() {

        String name = userPreferences.getString(
                "name",
                "NeuroCare User"
        );

        String email = userPreferences.getString(
                "email",
                ""
        );

        String age = userPreferences.getString(
                "age",
                ""
        );

        String gender = userPreferences.getString(
                "gender",
                "Select Gender"
        );

        selectedImageUri = userPreferences.getString(
                "profileImage",
                ""
        );

        txtProfileName.setText(name);

        txtProfileEmail.setText(
                email.isEmpty() ? "Welcome to NeuroCare" : email
        );

        txtProfileInitials.setText(getInitials(name));

        edtProfileName.setText(
                name.equals("NeuroCare User") ? "" : name
        );

        edtProfileEmail.setText(email);

        edtProfileAge.setText(age);

        int genderPosition = 0;

        for (int i = 0; i < spinnerGender.getCount(); i++) {

            if (spinnerGender.getItemAtPosition(i)
                    .toString().equals(gender)) {

                genderPosition = i;
                break;
            }
        }

        spinnerGender.setSelection(genderPosition);

        if (!selectedImageUri.isEmpty()) {

            try {

                Uri uri = Uri.parse(selectedImageUri);

                imgProfile.setImageURI(uri);

                imgProfile.setVisibility(View.VISIBLE);

                txtProfileInitials.setVisibility(View.GONE);

            } catch (Exception e) {

                imgProfile.setVisibility(View.GONE);

                txtProfileInitials.setVisibility(View.VISIBLE);
            }

        } else {

            imgProfile.setVisibility(View.GONE);

            txtProfileInitials.setVisibility(View.VISIBLE);
        }
    }

    private void setupProfileListeners() {

        btnChoosePhoto.setOnClickListener(v -> {

            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);

            intent.setType("image/*");

            intent.addCategory(Intent.CATEGORY_OPENABLE);

            intent.addFlags(
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                            | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION
            );

            imagePicker.launch(intent);
        });

        btnSaveProfile.setOnClickListener(v -> saveProfile());
    }

    private void saveProfile() {

        String name = edtProfileName.getText().toString().trim();

        String email = edtProfileEmail.getText().toString().trim();

        String age = edtProfileAge.getText().toString().trim();

        String gender = spinnerGender.getSelectedItem().toString();

        if (name.isEmpty()) {

            edtProfileName.setError("Enter your name");
            return;
        }

        if (email.isEmpty()) {

            edtProfileEmail.setError("Enter your email");
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS
                .matcher(email).matches()) {

            edtProfileEmail.setError("Enter a valid email");
            return;
        }

        if (!age.isEmpty()) {

            try {

                int ageValue = Integer.parseInt(age);

                if (ageValue < 1 || ageValue > 120) {

                    edtProfileAge.setError("Enter a valid age");
                    return;
                }

            } catch (NumberFormatException e) {

                edtProfileAge.setError("Enter a valid age");
                return;
            }
        }

        userPreferences.edit()
                .putString("name", name)
                .putString("email", email)
                .putString("age", age)
                .putString("gender", gender)
                .putString("profileImage", selectedImageUri)
                .apply();

        txtProfileName.setText(name);

        txtProfileEmail.setText(email);

        txtProfileInitials.setText(getInitials(name));

        Toast.makeText(
                this,
                "Profile saved successfully!",
                Toast.LENGTH_SHORT
        ).show();
    }

    private String getInitials(String name) {

        if (name == null || name.trim().isEmpty()) {
            return "NC";
        }

        String[] words = name.trim().split("\\s+");

        if (words.length == 1) {

            return words[0]
                    .substring(0, 1)
                    .toUpperCase();
        }

        return (
                words[0].substring(0, 1)
                        + words[words.length - 1].substring(0, 1)
        ).toUpperCase();
    }

    // =====================================
    // ACCORDION
    // =====================================

    private void setupAccordion() {

        headerPersonal.setOnClickListener(v ->
                toggleSection(contentPersonal, arrowPersonal));

        headerAppearance.setOnClickListener(v ->
                toggleSection(contentAppearance, arrowAppearance));

        headerAbout.setOnClickListener(v ->
                toggleSection(contentAbout, arrowAbout));
    }

    private void toggleSection(
            LinearLayout selectedContent,
            TextView selectedArrow) {

        boolean isOpening =
                selectedContent.getVisibility() != View.VISIBLE;

        contentPersonal.setVisibility(View.GONE);
        contentAppearance.setVisibility(View.GONE);
        contentAbout.setVisibility(View.GONE);

        arrowPersonal.setText("⌄");
        arrowAppearance.setText("⌄");
        arrowAbout.setText("⌄");

        if (isOpening) {

            selectedContent.setVisibility(View.VISIBLE);

            selectedArrow.setText("⌃");
        }
    }

    // =====================================
    // THEME
    // =====================================

    private void setupThemeListeners() {

        themeLight.setOnClickListener(v -> selectTheme("light"));

        themeDark.setOnClickListener(v -> selectTheme("dark"));

        themeBlue.setOnClickListener(v -> selectTheme("blue"));

        themePurple.setOnClickListener(v -> selectTheme("purple"));
    }

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

    private void applyCurrentTheme() {

        ThemeManager.applyTheme(
                this,
                R.id.settingsMain
        );
    }

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

    // =====================================
    // BOTTOM NAVIGATION
    // =====================================

    private void setupBottomNavigation() {

        navHome.setOnClickListener(v -> {

            Intent intent = new Intent(
                    SettingsActivity.this,
                    MainActivity.class
            );

            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            startActivity(intent);

            finish();
        });

        navGames.setOnClickListener(v -> {

            startActivity(new Intent(
                    SettingsActivity.this,
                    GamesActivity.class
            ));

            finish();
        });

        navProgress.setOnClickListener(v -> {

            startActivity(new Intent(
                    SettingsActivity.this,
                    ProgressActivity.class
            ));

            finish();
        });

        navHistory.setOnClickListener(v -> {

            startActivity(new Intent(
                    SettingsActivity.this,
                    HistoryActivity.class
            ));

            finish();
        });

        navProfile.setOnClickListener(v -> {
            // Already in Profile page
        });
    }
}