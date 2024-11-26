package com.example.tictactoegame;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class DataTables extends AppCompatActivity {

    private LinearLayout userContainer;
    private LinearLayout resultContainer;
    private EditText searchBar;
    private Databasehelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_tables);

        // Initialize views
        userContainer = findViewById(R.id.user_container);
        resultContainer = findViewById(R.id.result_container);
        searchBar = findViewById(R.id.search_bar);
        Button backButton = findViewById(R.id.button_back);  // Back button initialization

        // Set OnClickListener for Back to Dashboard button
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DataTables.this, AdminDashboard.class);
                startActivity(intent);
            }
        });

        // Initialize DatabaseHelper
        databaseHelper = new Databasehelper(this);

        // Load data
        loadData();

        // Implement search functionality
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                loadData(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void loadData() {
        loadData("");
    }

    private void loadData(String query) {
        // Clear current views
        userContainer.removeAllViews();
        resultContainer.removeAllViews();

        // Load users and results from the database
        List<User> users = databaseHelper.getUsers(query);
        List<Result> results = databaseHelper.getResults(query);

        // Add user views
        for (User user : users) {
            View userView = getLayoutInflater().inflate(R.layout.item_user, userContainer, false);
            TextView username = userView.findViewById(R.id.text_view_username);
            Button deleteButton = userView.findViewById(R.id.button_delete_user);

            username.setText(user.getUsername());

            deleteButton.setOnClickListener(v -> deleteUser(user));

            userContainer.addView(userView);
        }

        // Add result views
        for (Result result : results) {
            View resultView = getLayoutInflater().inflate(R.layout.item_result, resultContainer, false);
            TextView resultText = resultView.findViewById(R.id.text_view_result);
            Button deleteButton = resultView.findViewById(R.id.button_delete_result);

            // Calculate result dynamically
            String resultString = determineResult(result.getWins(), result.getLosses());
            String displayText = "User ID: " + result.getUserId() + ", Result: " + resultString;
            resultText.setText(displayText);

            deleteButton.setOnClickListener(v -> deleteResult(result));

            resultContainer.addView(resultView);
        }
    }

    private String determineResult(int wins, int losses) {
        if (wins > losses) {
            return "Win";
        } else if (losses > wins) {
            return "Loss";
        } else {
            return "Draw";
        }
    }

    private void deleteUser(User user) {
        int userId = user.getId();
        boolean success = databaseHelper.deleteUser(String.valueOf(userId));

        if (success) {
            // Remove user view from UI
            for (int i = 0; i < userContainer.getChildCount(); i++) {
                View childView = userContainer.getChildAt(i);
                if (childView.getTag() instanceof User) {
                    User userTag = (User) childView.getTag();
                    if (userTag.getId() == userId) {
                        userContainer.removeViewAt(i);
                        break;
                    }
                }
            }
        } else {
            // Handle deletion failure
            Toast.makeText(this, "Failed to delete user", Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteResult(Result result) {
        // Delete result based on result ID
        databaseHelper.deleteResult(result.getResultId());
        // Reload data after deletion
        loadData();
    }
}