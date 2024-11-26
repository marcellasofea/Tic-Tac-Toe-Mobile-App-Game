package com.example.tictactoegame;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class AdminDashboard extends AppCompatActivity {

    private TextView welcomeTextView;
    private Button startGameButton, deleteAccountButton;

    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        // Initialize UI elements
        welcomeTextView = findViewById(R.id.textView5);
        startGameButton = findViewById(R.id.startgame);

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
            Intent intent = new Intent(AdminDashboard.this, Nickname.class);
            intent.putExtra("username", username); // Pass username to MainActivity if needed
            startActivity(intent);
        });
        // Set OnClickListener for Logout button
        findViewById(R.id.logout).setOnClickListener(v -> signOut());
        findViewById(R.id.tables).setOnClickListener(v -> dataTables());
        findViewById(R.id.stats).setOnClickListener(v -> adminStats());
    }
    private void dataTables() {
        Intent dataTableIntent = new Intent(AdminDashboard.this, DataTables.class);
        startActivity(dataTableIntent);
        finish();
    }

    private void adminStats() {
        Intent adminStatsIntent = new Intent(AdminDashboard.this, AdminStats.class);
        startActivity(adminStatsIntent);
        finish();
    }

    private void signOut() {
        Intent signOutIntent = new Intent(AdminDashboard.this, SignIn.class);
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

}