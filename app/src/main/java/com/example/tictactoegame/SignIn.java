package com.example.tictactoegame;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SignIn extends AppCompatActivity {

    private EditText editTextUsername;
    private EditText editTextPassword;
    private Button buttonSignIn;
    private Databasehelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signin);

        // Initialize views
        editTextUsername = findViewById(R.id.username);
        editTextPassword = findViewById(R.id.password);
        buttonSignIn = findViewById(R.id.sign_in_button);
        findViewById(R.id.switch1).setOnClickListener(v -> signupeu());

        // Initialize DatabaseHelper
        databaseHelper = new Databasehelper(this);

        // Set OnClickListener for sign-in button
        buttonSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Retrieve username and password from EditText fields
                String username = editTextUsername.getText().toString().trim();
                String password = editTextPassword.getText().toString().trim();

                // Validate username and password
                if (isValidCredentials(username, password)) {
                    // Get the role of the user
                    String role = databaseHelper.getUserRole(username, password);

                    // Save username to SharedPreferences for future use
                    saveUsernameToPreferences(username);

                    // Proceed to the appropriate activity based on role
                    if ("Admin".equalsIgnoreCase(role)) {
                        // Go to Admin Dashboard
                        Intent intent = new Intent(SignIn.this, AdminDashboard.class);
                        startActivity(intent);
                    } else {
                        // Go to Profile activity
                        Intent intent = new Intent(SignIn.this, Profile.class);
                        intent.putExtra("username", username);
                        startActivity(intent);
                    }
                    finish(); // Finish the SignIn activity
                } else {
                    // Display error message if credentials are invalid
                    Toast.makeText(SignIn.this, "Invalid username or password", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // Method to validate username and password against database
    private boolean isValidCredentials(String username, String password) {
        // Check if username and password match records in the database
        return databaseHelper.checkUserExists(username, password);
    }

    // Method to save username to SharedPreferences
    private void saveUsernameToPreferences(String username) {
        SharedPreferences preferences = getSharedPreferences("user_credentials", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("username", username);
        editor.apply();
    }

    private void signupeu() {
        Intent intent = new Intent(SignIn.this, SignUp.class);
        startActivity(intent);
        finish();
    }
}
