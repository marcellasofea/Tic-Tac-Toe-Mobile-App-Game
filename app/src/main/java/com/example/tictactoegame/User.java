package com.example.tictactoegame;

public class User {
    private int id;
    private String nickname;
    private String username;
    private String password;
    private String role;

    public User(int id, String nickname, String username, String password, String role) {
        this.id = id;
        this.nickname = nickname;
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public int getId() {
        return id;
    }

    public String getNickname() {
        return nickname;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getRole() {
        return role;
    }
}
