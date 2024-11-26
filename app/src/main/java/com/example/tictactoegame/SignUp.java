package com.example.tictactoegame;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class SignUp extends AppCompatActivity {

    private EditText usernameEditText;
    private EditText passwordEditText;
    private Button signUpButton;
    private Switch toggleSwitch; // Switch for navigating to Sign In
    private Databasehelper databaseHelper;

    private static final String TAG = "SignUpActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);

        usernameEditText = findViewById(R.id.username);
        passwordEditText = findViewById(R.id.password);
        signUpButton = findViewById(R.id.signup_button);
        toggleSwitch = findViewById(R.id.switch1);
        databaseHelper = new Databasehelper(this);

        // Sign Up button click listener
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();
                String roles = "User";

                // Validate input fields
                if (username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(SignUp.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                } else {
                    // Check if username already exists
                    if (databaseHelper.checkUserExists(username, password)) {
                        Toast.makeText(SignUp.this, "Username already exists. Please choose another one.", Toast.LENGTH_SHORT).show();
                    } else {
                        // Add user to database
                        if (databaseHelper.addUser(username, password, roles)) {
                            Toast.makeText(SignUp.this, "Sign Up Successful!", Toast.LENGTH_SHORT).show();
                            // Navigate to SignIn activity
                            Intent intent = new Intent(SignUp.this, SignIn.class);
                            startActivity(intent);
                            finish(); // Close SignUp activity
                        } else {
                            Toast.makeText(SignUp.this, "Sign Up Failed. Please try again.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });

        // Handle toggle switch to navigate to Sign In activity
        toggleSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                Log.d(TAG, "Switch is checked, navigating to SignIn activity");
                Intent intent = new Intent(SignUp.this, SignIn.class);
                startActivity(intent);
                finish();
            } else {
                Log.d(TAG, "Switch is unchecked");
            }
        });
    }
}
