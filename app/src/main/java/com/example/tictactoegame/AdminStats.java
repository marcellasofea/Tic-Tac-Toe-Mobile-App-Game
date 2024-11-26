package com.example.tictactoegame;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;

public class AdminStats extends AppCompatActivity {

    private PieChart pieChart;
    private BarChart barChart;
    private EditText searchBar;
    private Button searchButton;
    private Button backToDashboardButton;
    private Databasehelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_stats);

        pieChart = findViewById(R.id.pie_chart);
        barChart = findViewById(R.id.bar_chart);
        searchBar = findViewById(R.id.search_bar);
        searchButton = findViewById(R.id.search_button);
        backToDashboardButton = findViewById(R.id.button_back);
        dbHelper = new Databasehelper(this);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userIdStr = searchBar.getText().toString().trim();
                if (!userIdStr.isEmpty()) {
                    int userId = Integer.parseInt(userIdStr);
                    List<Result> results = dbHelper.getResultsByUserId(userId);
                    if (!results.isEmpty()) {
                        setupPieChart(results);
                        setupBarChart(results);
                    } else {
                        Toast.makeText(AdminStats.this, "No results found for user ID: " + userId, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(AdminStats.this, "Please enter a user ID", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Set OnClickListener for Back to Dashboard button
        backToDashboardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminStats.this, AdminDashboard.class);
                startActivity(intent);
            }
        });
    }

    private void setupPieChart(List<Result> results) {
        int totalWins = 0;
        int totalLosses = 0;

        for (Result result : results) {
            totalWins += result.getWins();
            totalLosses += result.getLosses();
        }

        float winRate = calculateWinRate(totalWins, totalLosses);

        ArrayList<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(totalWins, "Wins"));
        entries.add(new PieEntry(totalLosses, "Losses"));

        PieDataSet dataSet = new PieDataSet(entries, "Win/Loss Ratio");
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);

        PieData data = new PieData(dataSet);
        pieChart.setData(data);
        pieChart.invalidate(); // refresh
    }

    private void setupBarChart(List<Result> results) {
        ArrayList<BarEntry> entries = new ArrayList<>();

        for (Result result : results) {
            entries.add(new BarEntry(result.getTotalPlay(), new float[]{result.getWins(), result.getLosses()}));
        }

        BarDataSet dataSet = new BarDataSet(entries, "Wins vs Losses");
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);

        BarData data = new BarData(dataSet);
        barChart.setData(data);
        barChart.invalidate(); // refresh
    }

    private float calculateWinRate(int totalWins, int totalLosses) {
        int totalGames = totalWins + totalLosses;
        if (totalGames > 0) {
            return ((float) totalWins / totalGames) * 100;
        } else {
            return 0;
        }
    }
}
