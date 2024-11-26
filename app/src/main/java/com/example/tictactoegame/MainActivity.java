package com.example.tictactoegame;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.example.tictactoegame.Databasehelper;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView[][] buttons = new TextView[4][4];
    private boolean player1Turn = true;
    private int roundCount;
    private int player1Points;
    private int player2Points;
    private TextView textViewPlayer1;
    private TextView textViewPlayer2;
    private String player1Name = "Player 1";
    private String player2Name = "Player 2";
    private String username; // Add username field
    private Databasehelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new Databasehelper(this);

        textViewPlayer1 = findViewById(R.id.text_view_p1);
        textViewPlayer2 = findViewById(R.id.text_view_p2);

        Intent intent = getIntent();
        if (intent != null) {
            player1Name = intent.getStringExtra("PLAYER1_NICKNAME");
            player2Name = intent.getStringExtra("PLAYER2_NICKNAME");
            player1Points = intent.getIntExtra("player1Points", 0);
            player2Points = intent.getIntExtra("player2Points", 0);
            player1Turn = intent.getBooleanExtra("player1Turn", true);
            roundCount = intent.getIntExtra("roundCount", 0);
            String[][] boardState = (String[][]) intent.getSerializableExtra("boardState");
            username = intent.getStringExtra("username"); // Retrieve username here

            if (player1Name != null && !player1Name.isEmpty()) {
                textViewPlayer1.setText(player1Name);
            } else {
                player1Name = "Player 1";
            }

            if (player2Name != null && !player2Name.isEmpty()) {
                textViewPlayer2.setText(player2Name);
            } else {
                player2Name = "Player 2";
            }

            initializeButtons();

            if (boardState != null) {
                restoreBoardState(boardState);
            }
        }

        findViewById(R.id.button_reset).setOnClickListener(v -> resetGame());
        findViewById(R.id.button_profile).setOnClickListener(v -> returnToProfile());
        findViewById(R.id.button_edit).setOnClickListener(v -> editNickname());

    }

    private void initializeButtons() {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                String buttonID = "button_" + i + j;
                int resID = getResources().getIdentifier(buttonID, "id", getPackageName());
                buttons[i][j] = findViewById(resID);
                if (buttons[i][j] != null) {
                    buttons[i][j].setOnClickListener(this);
                } else {
                    Log.e("MainActivity", "Button " + buttonID + " is null");
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        TextView clickedButton = (TextView) v;
        if (!clickedButton.getText().toString().isEmpty()) {
            return;
        }

        if (player1Turn) {
            clickedButton.setText("X");
        } else {
            clickedButton.setText("O");
        }

        roundCount++;

        if (checkForWin()) {
            if (player1Turn) {
                player1Wins();
            } else {
                player2Wins();
            }
        } else if (roundCount == 16) {
            draw();
        } else {
            player1Turn = !player1Turn;
        }
    }

    private boolean checkForWin() {
        String[][] field = new String[4][4];

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                field[i][j] = buttons[i][j].getText().toString();
            }
        }

        for (int i = 0; i < 4; i++) {
            if (field[i][0].equals(field[i][1]) && field[i][0].equals(field[i][2]) && field[i][0].equals(field[i][3]) && !field[i][0].isEmpty()) {
                return true;
            }
        }

        for (int i = 0; i < 4; i++) {
            if (field[0][i].equals(field[1][i]) && field[0][i].equals(field[2][i]) && field[0][i].equals(field[3][i]) && !field[0][i].isEmpty()) {
                return true;
            }
        }

        if (field[0][0].equals(field[1][1]) && field[0][0].equals(field[2][2]) && field[0][0].equals(field[3][3]) && !field[0][0].isEmpty()) {
            return true;
        }

        if (field[0][3].equals(field[1][2]) && field[0][3].equals(field[2][1]) && field[0][3].equals(field[3][0]) && !field[0][3].isEmpty()) {
            return true;
        }

        return false;
    }

    private void player1Wins() {
        player1Points += 3; // Award 3 points for a win
        Toast.makeText(this, player1Name + " wins!", Toast.LENGTH_SHORT).show();
        updatePointsText();
        insertResultInDatabase(player1Name, true, false); // Insert win for player 1
        dbHelper.insertLoss(dbHelper.getUserIdByUsername(player2Name)); // Insert loss for player 2
        resetBoard();
    }

    private void player2Wins() {
        player2Points += 3; // Award 3 points for a win
        Toast.makeText(this, player2Name + " wins!", Toast.LENGTH_SHORT).show();
        updatePointsText();
        insertResultInDatabase(player2Name, true, false); // Insert win for player 2
        dbHelper.insertLoss(dbHelper.getUserIdByUsername(player1Name)); // Insert loss for player 1
        resetBoard();
    }

    private void draw() {
        player1Points += 1; // Award 1 point for a draw
        player2Points += 1; // Award 1 point for a draw
        Toast.makeText(this, "Draw!", Toast.LENGTH_SHORT).show();
        updatePointsText();
        insertResultInDatabase(player1Name, false, true); // Insert draw for player 1
        insertResultInDatabase(player2Name, false, true); // Insert draw for player 2
        resetBoard();
    }

    private void insertResultInDatabase(String playerName, boolean isWin, boolean isDraw) {
        Databasehelper dbHelper = new Databasehelper(this);
        int userId = dbHelper.getUserIdByUsername(playerName);

        if (isWin) {
            dbHelper.insertResult(userId, true, false);
        } else if (isDraw) {
            dbHelper.insertResult(userId, false, true);
        } else {
            dbHelper.insertResult(userId, false, false);
        }
    }

    private void updatePointsText() {
        textViewPlayer1.setText(player1Name + ": " + player1Points);
        textViewPlayer2.setText(player2Name + ": " + player2Points);
    }

    private void resetBoard() {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                buttons[i][j].setText("");
            }
        }

        roundCount = 0;
        player1Turn = true;
    }

    private void resetGame() {
        player1Points = 0;
        player2Points = 0;
        updatePointsText();
        resetBoard();
    }

    private void returnToProfile() {
        Intent returnIntent = new Intent(MainActivity.this, Profile.class);
        returnIntent.putExtra("username", username); // Pass username back to Profile activity
        startActivity(returnIntent);
        finish();
    }

    private void editNickname() {
        Intent editIntent = new Intent(MainActivity.this, EditNickname.class);
        editIntent.putExtra("PLAYER1_NICKNAME", player1Name);
        editIntent.putExtra("PLAYER2_NICKNAME", player2Name);
        editIntent.putExtra("player1Points", player1Points);
        editIntent.putExtra("player2Points", player2Points);
        editIntent.putExtra("player1Turn", player1Turn);
        editIntent.putExtra("roundCount", roundCount);

        String[][] boardState = getBoardState();
        editIntent.putExtra("boardState", boardState);
        editIntent.putExtra("username", username); // Pass username to EditNickname activity

        startActivity(editIntent);
        finish();
    }

    private String[][] getBoardState() {
        String[][] boardState = new String[4][4];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                boardState[i][j] = buttons[i][j].getText().toString();
            }
        }
        return boardState;
    }

    private void restoreBoardState(String[][] boardState) {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                buttons[i][j].setText(boardState[i][j]);
            }
        }
    }
}