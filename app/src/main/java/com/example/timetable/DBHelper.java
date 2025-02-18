package com.example.timetable;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "timetable.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "timetable";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_SUBJECT = "subject";
    private static final String COLUMN_DAY = "day";
    private static final String COLUMN_TIME = "time";
    private static final String COLUMN_LOCATION = "location";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_SUBJECT + " TEXT NOT NULL, " +
                COLUMN_DAY + " TEXT NOT NULL, " +
                COLUMN_TIME + " TEXT NOT NULL, " +
                COLUMN_LOCATION + " TEXT NOT NULL)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean addSchedule(String subject, String day, String time, String location) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_SUBJECT, subject);
        values.put(COLUMN_DAY, day);
        values.put(COLUMN_TIME, time);
        values.put(COLUMN_LOCATION, location);
        long result = db.insert(TABLE_NAME, null, values);
        return result != -1;
    }

    public Cursor getSchedule() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
    }

    public boolean updateSchedule(int id, String subject, String day, String time, String location) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_SUBJECT, subject);
        values.put(COLUMN_DAY, day);
        values.put(COLUMN_TIME, time);
        values.put(COLUMN_LOCATION, location);
        return db.update(TABLE_NAME, values, COLUMN_ID + "=?", new String[]{String.valueOf(id)}) > 0;
    }

    public boolean deleteSchedule(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME, COLUMN_ID + "=?", new String[]{String.valueOf(id)}) > 0;
    }
}
