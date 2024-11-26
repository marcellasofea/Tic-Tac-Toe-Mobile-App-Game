package com.example.tictactoegame;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class Nickname extends AppCompatActivity {

    private EditText editTextPlayer1, editTextPlayer2;
    private Button buttonSubmit;
    private int player1Points;
    private int player2Points;
    private boolean player1Turn;
    private int roundCount;
    private String[][] boardState;
    private String player1Name;
    private String player2Name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nickname);

        editTextPlayer1 = findViewById(R.id.editText_player1);
        editTextPlayer2 = findViewById(R.id.editText_player2);
        buttonSubmit = findViewById(R.id.button_submit);
        findViewById(R.id.button_back).setOnClickListener(v -> back());

        Intent intent = getIntent();
        player1Points = intent.getIntExtra("player1Points", 0);
        player2Points = intent.getIntExtra("player2Points", 0);
        player1Turn = intent.getBooleanExtra("player1Turn", true);
        roundCount = intent.getIntExtra("roundCount", 0);
        boardState = (String[][]) intent.getSerializableExtra("boardState");
        player1Name = intent.getStringExtra("PLAYER1_NICKNAME");
        player2Name = intent.getStringExtra("PLAYER2_NICKNAME");

        // Set the latest nicknames as placeholders
        if (player1Name != null && !player1Name.isEmpty()) {
            editTextPlayer1.setText(player1Name);
        }
        if (player2Name != null && !player2Name.isEmpty()) {
            editTextPlayer2.setText(player2Name);
        }

        buttonSubmit.setOnClickListener(v -> {
            String player1Nickname = editTextPlayer1.getText().toString().trim();
            String player2Nickname = editTextPlayer2.getText().toString().trim();

            if (TextUtils.isEmpty(player1Nickname)) {
                editTextPlayer1.setError("Please enter Player 1's nickname");
                return;
            }
            if (TextUtils.isEmpty(player2Nickname)) {
                editTextPlayer2.setError("Please enter Player 2's nickname");
                return;
            }

            // Pass nicknames and game state back to MainActivity
            Intent mainIntent = new Intent(Nickname.this, MainActivity.class);
            mainIntent.putExtra("PLAYER1_NICKNAME", player1Nickname);
            mainIntent.putExtra("PLAYER2_NICKNAME", player2Nickname);
            mainIntent.putExtra("player1Points", player1Points);
            mainIntent.putExtra("player2Points", player2Points);
            mainIntent.putExtra("player1Turn", player1Turn);
            mainIntent.putExtra("roundCount", roundCount);
            mainIntent.putExtra("boardState", boardState);
            startActivity(mainIntent);
            finish(); // Finish Nickname activity after starting MainActivity
        });
    }

    private void back() {
        Intent kembali = new Intent(Nickname.this, Profile.class);
        startActivity(kembali);
        finish();
    }
}
