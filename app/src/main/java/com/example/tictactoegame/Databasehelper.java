package com.example.tictactoegame;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import java.util.ArrayList;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Databasehelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "users.db";
    private static final int DATABASE_VERSION = 2;
    private static final String TABLE_USERS = "users";
    private static final String TABLE_RESULTS = "results";

    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NICKNAME = "nickname";
    private static final String COLUMN_USERNAME = "username";
    private static final String COLUMN_PASSWORD = "password";
    private static final String COLUMN_ROLE = "role";

    private static final String COLUMN_RESULT_ID = "result_id";
    private static final String COLUMN_USER_ID = "user_id";
    private static final String COLUMN_WINS = "wins";
    private static final String COLUMN_LOSSES = "losses";
    private static final String COLUMN_TOTALPLAY = "totalplay";
    private static final String COLUMN_LATEST_DATE = "latest_date";

    public Databasehelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createUsersTable = "CREATE TABLE " + TABLE_USERS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_NICKNAME + " TEXT,"
                + COLUMN_USERNAME + " TEXT UNIQUE,"
                + COLUMN_PASSWORD + " TEXT, "
                + COLUMN_ROLE + " TEXT" + ")";
        db.execSQL(createUsersTable);

        String createResultsTable = "CREATE TABLE " + TABLE_RESULTS + " ("
                + COLUMN_RESULT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_USER_ID + " INTEGER, "
                + COLUMN_WINS + " INTEGER, "
                + COLUMN_LOSSES + " INTEGER, "
                + COLUMN_TOTALPLAY + " INTEGER, "
                + COLUMN_LATEST_DATE + " TEXT, "
                + "FOREIGN KEY(" + COLUMN_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_ID + ")" + ")";
        db.execSQL(createResultsTable);


        // Insert pre-entered admin user
        insertAdminUser(db);
    }

    private void insertAdminUser(SQLiteDatabase db) {
        ContentValues adminValues = new ContentValues();
        adminValues.put(COLUMN_NICKNAME, "Admin");
        adminValues.put(COLUMN_USERNAME, "admin");
        adminValues.put(COLUMN_PASSWORD, "admin123");  // Use a secure password in production
        adminValues.put(COLUMN_ROLE, "Admin");
        long result = db.insert(TABLE_USERS, null, adminValues);

        if (result == -1) {
            Log.e("Databasehelper", "Failed to insert admin user");
        } else {
            Log.d("Databasehelper", "Admin user inserted successfully");
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RESULTS);
        onCreate(db);
    }

    public List<User> getUsers(String query) {
        List<User> userList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS,
                null,
                COLUMN_USERNAME + " LIKE ?",
                new String[]{"%" + query + "%"},
                null, null, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                User user = new User(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NICKNAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USERNAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PASSWORD)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ROLE))
                );
                userList.add(user);
            }
            cursor.close();
        }

        db.close();
        return userList;
    }

    public List<Result> getResults(String query) {
        List<Result> resultList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_RESULTS,
                null,
                COLUMN_USER_ID + " LIKE ?",
                new String[]{"%" + query + "%"},
                null, null, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                Result result = new Result(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_RESULT_ID)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_USER_ID)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_WINS)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_LOSSES)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_TOTALPLAY)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LATEST_DATE))
                );
                resultList.add(result);
            }
            cursor.close();
        }

        db.close();
        return resultList;
    }

    public void deleteResult(int resultId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_RESULTS, COLUMN_RESULT_ID + " = ?", new String[]{String.valueOf(resultId)});
        db.close();
        Log.d("Databasehelper", "Deleted result with ID: " + resultId);
    }

    // Method to get player results
    public Cursor getPlayerResults(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_RESULTS, null, COLUMN_USER_ID + "=?", new String[]{String.valueOf(userId)}, null, null, null);
    }

    // Method to get player stats
    public Cursor getPlayerStats(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_RESULTS, null, COLUMN_USER_ID + "=?", new String[]{String.valueOf(userId)}, null, null, null);
    }

    public boolean addUser(String username, String password, String role) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, username);
        values.put(COLUMN_PASSWORD, password);
        values.put(COLUMN_ROLE, role);  // Added role to content values

        long result = db.insert(TABLE_USERS, null, values);
        db.close();
        return result != -1;
    }

    public String getUserRole(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS,
                new String[]{COLUMN_ROLE},
                COLUMN_USERNAME + "=? AND " + COLUMN_PASSWORD + "=?",
                new String[]{username, password},
                null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            String role = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ROLE));
            cursor.close();
            db.close();
            return role;
        } else {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
            return null;
        }
    }

    public boolean checkUserExists(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS,
                new String[]{COLUMN_ID},
                COLUMN_USERNAME + "=? AND " + COLUMN_PASSWORD + "=?",
                new String[]{username, password},
                null, null, null);

        int count = cursor.getCount();
        cursor.close();
        db.close();
        return count > 0;
    }

    // New method to check if a user exists based on username only
    public boolean checkUserExists(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS,
                new String[]{COLUMN_ID},
                COLUMN_USERNAME + "=?",
                new String[]{username},
                null, null, null);

        int count = cursor.getCount();
        cursor.close();
        db.close();
        return count > 0;
    }

    public String getNickname(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        String nickname = null;
        Cursor cursor = null;

        try {
            cursor = db.query(TABLE_USERS,
                    new String[]{COLUMN_NICKNAME},
                    COLUMN_USERNAME + "=?",
                    new String[]{username},
                    null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                nickname = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NICKNAME));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }

        return nickname;
    }

    public boolean updatePassword(String username, String newPassword) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Check if the username exists
        if (!checkUserExists(username)) {
            db.close();
            return false;  // Username does not exist in the database
        }

        ContentValues values = new ContentValues();
        values.put(COLUMN_PASSWORD, newPassword);

        // Corrected the WHERE clause to update based on username
        int update = db.update(TABLE_USERS, values,
                COLUMN_USERNAME + "=?",
                new String[]{username});

        db.close();
        return update > 0;  // Returns true if one or more rows were affected
    }

    public void insertResult(int userId, boolean isWin, boolean isDraw) {
        SQLiteDatabase db = this.getWritableDatabase();
        String currentDate = getCurrentDate();

        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_ID, userId);
        values.put(COLUMN_WINS, isWin ? 1 : 0);
        values.put(COLUMN_LOSSES, (!isWin && !isDraw) ? 1 : 0);
        values.put(COLUMN_TOTALPLAY, 1);
        values.put(COLUMN_LATEST_DATE, currentDate);

        long newRowId = db.insert(TABLE_RESULTS, null, values);
        Log.d("Databasehelper", "Inserted new result for user ID: " + userId + ", Row ID: " + newRowId);

        db.close();
    }

    private String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date());
    }

    public void insertLoss(int userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("user_id", userId);
        values.put("losses", 1); // Set losses to 1 for a new loss record

        // Insert the record
        long result = db.insert("results", null, values);

        if (result == -1) {
            Log.e("Databasehelper", "Failed to insert loss for user with ID: " + userId);
        } else {
            Log.d("Databasehelper", "Loss inserted successfully for user with ID: " + userId);
        }

        db.close();
    }

    public List<Result> getResultsByUserId(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        List<Result> results = new ArrayList<>();

        // Query the results table for results associated with the user ID
        String query = "SELECT * FROM " + TABLE_RESULTS +
                " WHERE " + COLUMN_USER_ID + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});

        // Iterate through the cursor to retrieve results
        if (cursor != null && cursor.moveToFirst()) {
            do {
                int resultId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_RESULT_ID));
                int wins = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_WINS));
                int losses = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_LOSSES));
                int totalPlay = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_TOTALPLAY));
                String latestDate = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LATEST_DATE));

                // Create Result object and add to results list
                Result result = new Result(resultId, userId, wins, losses, totalPlay, latestDate);
                results.add(result);
            } while (cursor.moveToNext());

            cursor.close();
        }

        db.close();
        return results;
    }

    public int getUserIdByUsername(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        int userId = -1;

        Cursor cursor = db.query(TABLE_USERS,
                new String[]{COLUMN_ID},
                COLUMN_USERNAME + " = ?",
                new String[]{username},
                null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            userId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID));
            cursor.close();
        }

        db.close();
        return userId;
    }

    public boolean deleteUser(String username) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("users", "username = ?", new String[]{username}) > 0;
    }
}
