package com.example.tictactoegame;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ForgotPass extends AppCompatActivity {

    private EditText editTextUsername;
    private EditText editTextNewPassword;
    private EditText editTextConfirmNewPassword;
    private Button buttonSave;
    private Button buttonBackToSignIn;

    private Databasehelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgotpass);

        editTextUsername = findViewById(R.id.uname);
        editTextNewPassword = findViewById(R.id.newpassword);
        editTextConfirmNewPassword = findViewById(R.id.confirmednewpassword);
        buttonSave = findViewById(R.id.save);
        buttonBackToSignIn = findViewById(R.id.backtosignin);

        dbHelper = new Databasehelper(this);

        buttonSave.setOnClickListener(v -> saveNewPassword());
        buttonBackToSignIn.setOnClickListener(v -> {
            Intent signInIntent = new Intent(ForgotPass.this, SignIn.class);
            startActivity(signInIntent);
            finish();
        });
    }

    private void saveNewPassword() {
        String username = editTextUsername.getText().toString().trim();
        String newPassword = editTextNewPassword.getText().toString().trim();
        String confirmNewPassword = editTextConfirmNewPassword.getText().toString().trim();

        if (username.isEmpty() || newPassword.isEmpty() || confirmNewPassword.isEmpty()) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!newPassword.equals(confirmNewPassword)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean updateSuccessful = dbHelper.updatePassword(username, newPassword);
        if (updateSuccessful) {
            Toast.makeText(this, "Password updated successfully", Toast.LENGTH_SHORT).show();
            Intent signInIntent = new Intent(ForgotPass.this, SignIn.class);
            startActivity(signInIntent);
            finish();
        } else {
            Toast.makeText(this, "Error updating password. User may not exist.", Toast.LENGTH_SHORT).show();
        }
    }
}
