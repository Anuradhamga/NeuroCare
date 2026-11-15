package com.example.neurocare;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ProgressActivity extends AppCompatActivity {

    private TextView txtTotalSessions;
    private TextView txtTotalScore;
    private TextView txtAverageReaction;

    private TextView txtMemoryProgress;
    private TextView txtAttentionProgress;
    private TextView txtReactionProgress;
    private TextView txtOddOneOutProgress;

    private ProgressBar progressMemoryBar;
    private ProgressBar progressAttentionBar;
    private ProgressBar progressReactionBar;
    private ProgressBar progressOddOneOutBar;

    private LinearLayout overallProgressCard;
    private LinearLayout scoreCard;
    private LinearLayout reactionCard;
    private LinearLayout performanceCard;

    private Button btnExportPDF;

    private String currentTheme;

    private static final int CREATE_PDF_REQUEST = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_progress);

        // TextViews
        txtTotalSessions = findViewById(R.id.txtTotalSessions);
        txtTotalScore = findViewById(R.id.txtTotalScore);
        txtAverageReaction = findViewById(R.id.txtAverageReaction);

        txtMemoryProgress = findViewById(R.id.txtMemoryProgress);
        txtAttentionProgress = findViewById(R.id.txtAttentionProgress);
        txtReactionProgress = findViewById(R.id.txtReactionProgress);
        txtOddOneOutProgress = findViewById(R.id.txtOddOneOutProgress);

        // Progress Bars
        progressMemoryBar = findViewById(R.id.progressMemoryBar);
        progressAttentionBar = findViewById(R.id.progressAttentionBar);
        progressReactionBar = findViewById(R.id.progressReactionBar);
        progressOddOneOutBar = findViewById(R.id.progressOddOneOutBar);

        // Cards
        overallProgressCard = findViewById(R.id.overallProgressCard);
        scoreCard = findViewById(R.id.scoreCard);
        reactionCard = findViewById(R.id.reactionCard);
        performanceCard = findViewById(R.id.performanceCard);

        // PDF Button
        btnExportPDF = findViewById(R.id.btnExportPDF);

        btnExportPDF.setOnClickListener(v -> exportProgressPDF());

        // Bottom Navigation
        setupBottomNavigation();

        loadProgress();
        applyProgressTheme();
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
            // Already in Progress screen
        });

        findViewById(R.id.navHistory).setOnClickListener(v -> {
            startActivity(new Intent(this, HistoryActivity.class));
            finish();
        });

        findViewById(R.id.navProfile).setOnClickListener(v -> {
            startActivity(new Intent(this, SettingsActivity.class));
            finish();
        });
    }

    // ==========================================
    // ODD ONE OUT SESSION COUNT
    // ==========================================

    private int getOddOneOutSessions(
            android.content.SharedPreferences preferences) {

        int count = 0;

        try {

            JSONArray historyArray = new JSONArray(
                    preferences.getString("sessionHistory", "[]")
            );

            for (int i = 0; i < historyArray.length(); i++) {

                JSONObject session = historyArray.getJSONObject(i);

                if ("Odd One Out".equals(
                        session.optString("gameName"))) {

                    count++;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return count;
    }

    private int getOddOneOutTotalScore(
            android.content.SharedPreferences preferences) {

        int score = 0;

        try {

            JSONArray historyArray = new JSONArray(
                    preferences.getString("sessionHistory", "[]")
            );

            for (int i = 0; i < historyArray.length(); i++) {

                JSONObject session = historyArray.getJSONObject(i);

                if ("Odd One Out".equals(
                        session.optString("gameName"))) {

                    score += session.optInt("score", 0);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return score;
    }

    // ==========================================
    // LOAD PROGRESS DATA
    // ==========================================

    private void loadProgress() {

        android.content.SharedPreferences preferences =
                getSharedPreferences("NeuroCareStats", MODE_PRIVATE);

        int totalSessions = preferences.getInt("totalSessions", 0);
        int totalScore = preferences.getInt("totalScore", 0);
        int averageReaction = preferences.getInt("averageReaction", 0);

        int memorySessions = preferences.getInt("memorySessions", 0);
        int attentionSessions = preferences.getInt("attentionSessions", 0);
        int reactionSessions = preferences.getInt("reactionSessions", 0);

        // Odd One Out
        int oddOneOutSessions = getOddOneOutSessions(preferences);
        int oddOneOutScore = getOddOneOutTotalScore(preferences);

        // Include Odd One Out in overall statistics
        totalSessions += oddOneOutSessions;
        totalScore += oddOneOutScore;

        txtTotalSessions.setText(String.valueOf(totalSessions));
        txtTotalScore.setText(String.valueOf(totalScore));

        if (averageReaction > 0) {
            txtAverageReaction.setText(averageReaction + " ms");
        } else {
            txtAverageReaction.setText("---");
        }

        // Game session counts
        txtMemoryProgress.setText(
                memorySessions + " sessions completed"
        );

        txtAttentionProgress.setText(
                attentionSessions + " sessions completed"
        );

        txtReactionProgress.setText(
                reactionSessions + " sessions completed"
        );

        txtOddOneOutProgress.setText(
                oddOneOutSessions + " sessions completed"
        );

        // Maximum sessions
        int maxSessions = Math.max(
                Math.max(memorySessions, attentionSessions),
                Math.max(reactionSessions, oddOneOutSessions)
        );

        // Progress Bars
        if (maxSessions > 0) {

            progressMemoryBar.setProgress(
                    memorySessions * 100 / maxSessions
            );

            progressAttentionBar.setProgress(
                    attentionSessions * 100 / maxSessions
            );

            progressReactionBar.setProgress(
                    reactionSessions * 100 / maxSessions
            );

            progressOddOneOutBar.setProgress(
                    oddOneOutSessions * 100 / maxSessions
            );

        } else {

            progressMemoryBar.setProgress(0);
            progressAttentionBar.setProgress(0);
            progressReactionBar.setProgress(0);
            progressOddOneOutBar.setProgress(0);
        }
    }

    // ==========================================
    // APPLY THEME
    // ==========================================

    private void applyProgressTheme() {

        currentTheme = ThemeManager.getSelectedTheme(this);

        ThemeManager.applyTheme(this, R.id.progressMain);

        int primaryColor = ThemeManager.getPrimaryColor(this);
        int cardColor = ThemeManager.getCardColor(this);
        int textColor = ThemeManager.getTextColor(this);

        int secondaryColor;

        if (currentTheme.equals("dark")) {
            secondaryColor = Color.parseColor("#BDBDBD");
        } else {
            secondaryColor = Color.parseColor("#6B5B52");
        }

        // Header and Titles

        setTextColor(R.id.progressHeader, primaryColor);

        setTextColor(R.id.progressTitle, textColor);
        setTextColor(R.id.statisticsTitle, textColor);
        setTextColor(R.id.performanceTitle, textColor);

        setTextColor(R.id.progressSubtitle, secondaryColor);
        setTextColor(R.id.performanceSubtitle, secondaryColor);

        setTextColor(R.id.progressFooter, primaryColor);

        // Statistics

        txtTotalScore.setTextColor(primaryColor);
        txtAverageReaction.setTextColor(primaryColor);

        // Game Titles

        setTextColor(R.id.memoryProgressTitle, textColor);
        setTextColor(R.id.attentionProgressTitle, textColor);
        setTextColor(R.id.reactionProgressTitle, textColor);
        setTextColor(R.id.oddOneOutProgressTitle, textColor);

        // Descriptions

        txtMemoryProgress.setTextColor(secondaryColor);
        txtAttentionProgress.setTextColor(secondaryColor);
        txtReactionProgress.setTextColor(secondaryColor);
        txtOddOneOutProgress.setTextColor(secondaryColor);

        // Cards

        setCardBackground(scoreCard, cardColor);
        setCardBackground(reactionCard, cardColor);
        setCardBackground(performanceCard, cardColor);

        // Export Button

        GradientDrawable buttonBackground = new GradientDrawable();

        buttonBackground.setColor(primaryColor);
        buttonBackground.setCornerRadius(dp(18));

        btnExportPDF.setBackground(buttonBackground);
        btnExportPDF.setTextColor(Color.WHITE);

        // Progress Bars

        ColorStateList progressTint =
                ColorStateList.valueOf(primaryColor);

        ColorStateList backgroundTint =
                ColorStateList.valueOf(
                        currentTheme.equals("dark")
                                ? Color.parseColor("#555555")
                                : Color.parseColor("#F2E6E1")
                );

        progressMemoryBar.setProgressTintList(progressTint);
        progressAttentionBar.setProgressTintList(progressTint);
        progressReactionBar.setProgressTintList(progressTint);
        progressOddOneOutBar.setProgressTintList(progressTint);

        progressMemoryBar.setProgressBackgroundTintList(backgroundTint);
        progressAttentionBar.setProgressBackgroundTintList(backgroundTint);
        progressReactionBar.setProgressBackgroundTintList(backgroundTint);
        progressOddOneOutBar.setProgressBackgroundTintList(backgroundTint);

        // Overall Card Gradient

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

        gradient.setCornerRadius(dp(28));

        overallProgressCard.setBackground(gradient);

        setOverallCardTextWhite(overallProgressCard);
    }

    private void setTextColor(int viewId, int color) {

        TextView textView = findViewById(viewId);

        if (textView != null) {
            textView.setTextColor(color);
        }
    }

    private void setCardBackground(LinearLayout card, int color) {

        GradientDrawable background = new GradientDrawable();

        background.setColor(color);
        background.setCornerRadius(dp(20));

        int strokeColor = currentTheme.equals("dark")
                ? Color.parseColor("#444444")
                : Color.parseColor("#FBD9BF");

        background.setStroke(dp(1), strokeColor);

        card.setBackground(background);
    }

    private void setOverallCardTextWhite(LinearLayout card) {

        for (int i = 0; i < card.getChildCount(); i++) {

            android.view.View child = card.getChildAt(i);

            if (child instanceof TextView) {
                ((TextView) child).setTextColor(Color.WHITE);
            }
        }
    }

    private int dp(int value) {

        return (int) (value * getResources()
                .getDisplayMetrics().density);
    }

    // ==========================================
    // EXPORT PROGRESS AS PDF
    // ==========================================

    private void exportProgressPDF() {

        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);

        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/pdf");

        intent.putExtra(
                Intent.EXTRA_TITLE,
                "NeuroCare_Progress_Report.pdf"
        );

        startActivityForResult(intent, CREATE_PDF_REQUEST);
    }

    @Override
    protected void onActivityResult(
            int requestCode,
            int resultCode,
            Intent data
    ) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CREATE_PDF_REQUEST
                && resultCode == RESULT_OK
                && data != null) {

            Uri pdfUri = data.getData();

            if (pdfUri != null) {
                createProgressPDF(pdfUri);
            }
        }
    }

    private void createProgressPDF(Uri uri) {

        PdfDocument document = new PdfDocument();

        try {

            android.content.SharedPreferences preferences =
                    getSharedPreferences("NeuroCareStats", MODE_PRIVATE);

            int totalSessions = preferences.getInt("totalSessions", 0);
            int totalScore = preferences.getInt("totalScore", 0);
            int averageReaction = preferences.getInt("averageReaction", 0);

            int memorySessions = preferences.getInt("memorySessions", 0);
            int attentionSessions = preferences.getInt("attentionSessions", 0);
            int reactionSessions = preferences.getInt("reactionSessions", 0);

            // Odd One Out
            int oddOneOutSessions = getOddOneOutSessions(preferences);
            int oddOneOutScore = getOddOneOutTotalScore(preferences);

            totalSessions += oddOneOutSessions;
            totalScore += oddOneOutScore;

            int maxSessions = Math.max(
                    Math.max(memorySessions, attentionSessions),
                    Math.max(reactionSessions, oddOneOutSessions)
            );

            int memoryPercent = maxSessions > 0
                    ? memorySessions * 100 / maxSessions : 0;

            int attentionPercent = maxSessions > 0
                    ? attentionSessions * 100 / maxSessions : 0;

            int reactionPercent = maxSessions > 0
                    ? reactionSessions * 100 / maxSessions : 0;

            int oddOneOutPercent = maxSessions > 0
                    ? oddOneOutSessions * 100 / maxSessions : 0;

            // Create PDF Page

            PdfDocument.PageInfo pageInfo =
                    new PdfDocument.PageInfo.Builder(595, 842, 1).create();

            PdfDocument.Page page = document.startPage(pageInfo);

            Canvas canvas = page.getCanvas();

            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

            canvas.drawColor(Color.WHITE);

            // Header

            paint.setColor(Color.rgb(201, 74, 12));

            canvas.drawRoundRect(
                    new RectF(30, 25, 565, 155),
                    20, 20, paint
            );

            paint.setColor(Color.WHITE);
            paint.setTextSize(27);
            paint.setTypeface(Typeface.create("sans-serif", Typeface.BOLD));

            canvas.drawText("NEUROCARE", 55, 75, paint);

            paint.setTextSize(19);

            canvas.drawText(
                    "Cognitive Training Progress Report",
                    55, 112, paint
            );

            paint.setTextSize(11);
            paint.setTypeface(Typeface.DEFAULT);

            String date = new SimpleDateFormat(
                    "dd MMMM yyyy, hh:mm a",
                    Locale.getDefault()
            ).format(new Date());

            canvas.drawText("Generated: " + date, 55, 137, paint);

            // Overall Statistics

            drawSectionTitle(canvas, paint, "OVERALL STATISTICS", 195);

            drawReportCard(canvas, paint, 30, 215, 565, 370);

            drawReportText(
                    canvas, paint,
                    "Total Training Sessions", 55, 255, true
            );

            drawReportValue(
                    canvas, paint,
                    String.valueOf(totalSessions), 525, 255
            );

            drawReportText(
                    canvas, paint,
                    "Total Score", 55, 300, true
            );

            drawReportValue(
                    canvas, paint,
                    String.valueOf(totalScore), 525, 300
            );

            drawReportText(
                    canvas, paint,
                    "Average Reaction Time", 55, 345, true
            );

            drawReportValue(
                    canvas, paint,
                    averageReaction > 0 ? averageReaction + " ms" : "No data",
                    525, 345
            );

            // Game Performance

            drawSectionTitle(canvas, paint, "GAME PERFORMANCE", 425);

            drawGameReport(
                    canvas, paint,
                    "Memory Match",
                    memorySessions,
                    memoryPercent,
                    465
            );

            drawGameReport(
                    canvas, paint,
                    "Attention Training",
                    attentionSessions,
                    attentionPercent,
                    525
            );

            drawGameReport(
                    canvas, paint,
                    "Reaction Time",
                    reactionSessions,
                    reactionPercent,
                    585
            );

            drawGameReport(
                    canvas, paint,
                    "Odd One Out",
                    oddOneOutSessions,
                    oddOneOutPercent,
                    645
            );

            // Footer

            paint.setColor(Color.rgb(201, 74, 12));
            paint.setTextSize(15);
            paint.setTypeface(Typeface.create("sans-serif", Typeface.BOLD));

            canvas.drawText(
                    "Keep practicing every day!",
                    175, 755, paint
            );

            paint.setColor(Color.GRAY);
            paint.setTextSize(10);
            paint.setTypeface(Typeface.DEFAULT);

            canvas.drawText(
                    "NeuroCare - Cognitive Training Application",
                    155, 800, paint
            );

            document.finishPage(page);

            // Save PDF

            OutputStream outputStream =
                    getContentResolver().openOutputStream(uri);

            if (outputStream == null) {
                throw new Exception("Unable to open file location.");
            }

            try {
                document.writeTo(outputStream);
            } finally {
                outputStream.close();
            }

            Toast.makeText(
                    this,
                    "PDF Report Saved Successfully!",
                    Toast.LENGTH_LONG
            ).show();

            // Share PDF

            new AlertDialog.Builder(this)
                    .setTitle("PDF Report Saved")
                    .setMessage("Would you like to share your NeuroCare Progress Report?")
                    .setPositiveButton("Share", (dialog, which) -> {

                        Intent shareIntent =
                                new Intent(Intent.ACTION_SEND);

                        shareIntent.setType("application/pdf");

                        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);

                        shareIntent.addFlags(
                                Intent.FLAG_GRANT_READ_URI_PERMISSION
                        );

                        startActivity(
                                Intent.createChooser(
                                        shareIntent,
                                        "Share NeuroCare Report"
                                )
                        );
                    })
                    .setNegativeButton("Later", null)
                    .show();

        } catch (Exception e) {

            Toast.makeText(
                    this,
                    "PDF Error: " + e.getMessage(),
                    Toast.LENGTH_LONG
            ).show();

            e.printStackTrace();

        } finally {

            document.close();
        }
    }

    // ==========================================
    // PDF DESIGN HELPERS
    // ==========================================

    private void drawSectionTitle(
            Canvas canvas,
            Paint paint,
            String title,
            float y
    ) {

        paint.setColor(Color.rgb(201, 74, 12));
        paint.setTextSize(17);
        paint.setTypeface(Typeface.create("sans-serif", Typeface.BOLD));

        canvas.drawText(title, 45, y, paint);
    }

    private void drawReportCard(
            Canvas canvas,
            Paint paint,
            float left,
            float top,
            float right,
            float bottom
    ) {

        paint.setColor(Color.rgb(255, 247, 243));

        canvas.drawRoundRect(
                new RectF(left, top, right, bottom),
                18, 18, paint
        );
    }

    private void drawReportText(
            Canvas canvas,
            Paint paint,
            String text,
            float x,
            float y,
            boolean bold
    ) {

        paint.setColor(Color.DKGRAY);
        paint.setTextSize(14);

        paint.setTypeface(
                bold
                        ? Typeface.create("sans-serif", Typeface.BOLD)
                        : Typeface.DEFAULT
        );

        canvas.drawText(text, x, y, paint);
    }

    private void drawReportValue(
            Canvas canvas,
            Paint paint,
            String value,
            float x,
            float y
    ) {

        paint.setColor(Color.rgb(201, 74, 12));
        paint.setTextSize(15);
        paint.setTypeface(Typeface.create("sans-serif", Typeface.BOLD));

        float width = paint.measureText(value);

        canvas.drawText(value, x - width, y, paint);
    }

    private void drawGameReport(
            Canvas canvas,
            Paint paint,
            String gameName,
            int sessions,
            int percentage,
            float y
    ) {

        paint.setColor(Color.DKGRAY);
        paint.setTextSize(14);
        paint.setTypeface(Typeface.create("sans-serif", Typeface.BOLD));

        canvas.drawText(gameName, 45, y, paint);

        paint.setColor(Color.GRAY);
        paint.setTextSize(11);
        paint.setTypeface(Typeface.DEFAULT);

        canvas.drawText(
                sessions + " sessions completed",
                45, y + 20, paint
        );

        canvas.drawText(
                percentage + "%",
                520, y + 10, paint
        );

        // Progress Background

        paint.setColor(Color.rgb(240, 230, 225));

        canvas.drawRoundRect(
                new RectF(45, y + 30, 550, y + 38),
                5, 5, paint
        );

        // Progress Value

        if (percentage > 0) {

            paint.setColor(Color.rgb(201, 74, 12));

            float barWidth = 505 * percentage / 100f;

            canvas.drawRoundRect(
                    new RectF(45, y + 30, 45 + barWidth, y + 38),
                    5, 5, paint
            );
        }
    }

    // ==========================================
    // REFRESH
    // ==========================================

    @Override
    protected void onResume() {
        super.onResume();

        if (txtTotalSessions != null) {
            loadProgress();
            applyProgressTheme();
        }
    }
}