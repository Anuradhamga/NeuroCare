package com.example.neurocare;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private static final String APP_PASSWORD = "1234";

    private EditText edtPassword;
    private TextView txtError;
    private Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        edtPassword = findViewById(R.id.edtPassword);
        txtError = findViewById(R.id.txtError);
        btnLogin = findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(v -> {

            String enteredPassword = edtPassword.getText().toString();

            if (enteredPassword.equals(APP_PASSWORD)) {

                Intent intent = new Intent(
                        LoginActivity.this,
                        MainActivity.class
                );

                startActivity(intent);
                finish();

            } else {

                txtError.setText("Incorrect password. Try again.");
                txtError.setVisibility(TextView.VISIBLE);
                edtPassword.setText("");
            }
        });
    }
}