package com.example.tictactoegame;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class Profile extends AppCompatActivity {

    private TextView welcomeTextView;
    private Button startGameButton, deleteAccountButton;

    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);

        // Initialize UI elements
        welcomeTextView = findViewById(R.id.textView5);
        startGameButton = findViewById(R.id.startgame);
        findViewById(R.id.deleteAccountButton).setOnClickListener(v -> confirmDeleteAccount());

        // Retrieve username from Intent extras
        username = getIntent().getStringExtra("username");
        if (username == null || username.isEmpty()) {
            // If username is not passed via Intent, check SharedPreferences
            username = retrieveUsernameFromPreferences();
        }

        // Construct the welcome message
        updateWelcomeMessage();

        // Set OnClickListener for Start Game button
        startGameButton.setOnClickListener(v -> {
            Intent intent = new Intent(Profile.this, Nickname.class);
            intent.putExtra("username", username); // Pass username to MainActivity if needed
            startActivity(intent);
        });
        // Set OnClickListener for Logout button
        findViewById(R.id.logout).setOnClickListener(v -> signOut());
    }

    private void signOut() {
        Intent signOutIntent = new Intent(Profile.this, SignIn.class);
        startActivity(signOutIntent);
        finish();
    }

    private String retrieveUsernameFromPreferences() {
        SharedPreferences preferences = getSharedPreferences("user_credentials", MODE_PRIVATE);
        return preferences.getString("username", "Unknown User");
    }

    private void updateWelcomeMessage() {
        String welcomeMessage = "Welcome back, " + username.toUpperCase() + "!";
        welcomeTextView.setText(welcomeMessage);
    }

    private void confirmDeleteAccount() {
        new AlertDialog.Builder(Profile.this)
                .setTitle("Delete Account")
                .setMessage("Are you sure you want to delete your account? This action cannot be undone.")
                .setPositiveButton(android.R.string.yes, (dialog, which) -> deleteAccount())
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void deleteAccount() {
        // Delete user credentials from SharedPreferences
        deleteCredentials();

        // Delete user from SQLite database
        Databasehelper dbHelper = new Databasehelper(Profile.this);
        dbHelper.deleteUser(username);

        // Redirect to Sign In activity
        Intent intent = new Intent(Profile.this, SignIn.class);
        startActivity(intent);
        finish(); // Finish the Profile activity after deletion
    }

    private void deleteCredentials() {
        // Delete user credentials from SharedPreferences
        SharedPreferences preferences = getSharedPreferences("user_credentials", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear(); // Clear all data
        editor.apply();
    }
}
