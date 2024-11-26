package com.example.tictactoegame;

public class Result {
    private int resultId;
    private int userId;
    private int wins;
    private int losses;
    private int totalPlay;
    private String latestDate;

    public Result(int resultId, int userId, int wins, int losses, int totalPlay, String latestDate) {
        this.resultId = resultId;
        this.userId = userId;
        this.wins = wins;
        this.losses = losses;
        this.totalPlay = totalPlay;
        this.latestDate = latestDate;
    }

    public int getResultId() {
        return resultId;
    }

    public int getUserId() { return userId; }

    public int getWins() {
        return wins;
    }

    public int getLosses() { return losses; }

    public int getTotalPlay() {
        return totalPlay;
    }

    public String getLatestDate() {
        return latestDate;
    }
}
