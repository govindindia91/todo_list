package com.example.cleareyes.localoyeproject;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.ArrayList;

public class DatabaseHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "LocalOye";
    private static final String TABLE_NAME = "Tasks";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_TASK_TITLE = "title";
    private static final String COLUMN_TASK_DESCRIPTION = "description";
    private static final String COLUMN_TASK_CREATE_DATE = "createDate";
    private static final String COLUMN_TASK_END_DATE = "endDate";
    private static final String COLUMN_TASK_ISACTIVE = "isActive";
    private static final String COLUMN_TASK_STATE = "state";

    public DatabaseHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        try {
            SQLiteDatabase db1 = this.getWritableDatabase();
            String query = "SELECT * FROM " + TABLE_NAME ;
            Cursor cursor = db1.rawQuery(query, null);
        }
        catch (Exception e) {
            String createContactTable = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "(" + COLUMN_ID + " INTEGER PRIMARY KEY,"
                    + COLUMN_TASK_TITLE + " TEXT,"
                    + COLUMN_TASK_DESCRIPTION + " TEXT,"
                    + COLUMN_TASK_CREATE_DATE + " DATE,"
                    + COLUMN_TASK_END_DATE + " DATE,"
                    + COLUMN_TASK_ISACTIVE + " BOOLEAN,"
                    + COLUMN_TASK_STATE + " TEXT )";
            db.execSQL(createContactTable);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void addTask(Task task) {

       // Log.i("got into add task","true");
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_TASK_TITLE, task.getTaskTitle());
        contentValues.put(COLUMN_TASK_DESCRIPTION, task.getTaskDescription());
        contentValues.put(COLUMN_TASK_CREATE_DATE, task.getStartDate());
        contentValues.put(COLUMN_TASK_END_DATE, task.getEndDate());
        contentValues.put(COLUMN_TASK_ISACTIVE, task.getIsActive());
        contentValues.put(COLUMN_TASK_STATE, task.getTaskState());

        Long success = db.insert(TABLE_NAME, null, contentValues);
        //Log.i("success", String.valueOf(success));
        String query = "SELECT * FROM " + TABLE_NAME ;
        Cursor cursor = db.rawQuery(query, null);
        //Log.i("cursor", String.valueOf(cursor.getCount()) );
        db.close();
    }

    public void updateTask(Task task) {

        Log.i("update","true");
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_TASK_TITLE, task.getTaskTitle());
        contentValues.put(COLUMN_TASK_DESCRIPTION, task.getTaskDescription());
        contentValues.put(COLUMN_TASK_END_DATE, task.getEndDate());
        contentValues.put(COLUMN_TASK_STATE, task.getTaskState());

        /*String query = "UPDATE " + TABLE_NAME + " SET " +
                COLUMN_TASK_TITLE + " = '" + task.getTaskTitle() + "'," +
                COLUMN_TASK_DESCRIPTION + " = '" + task.getTaskDescription() + "'," +
                COLUMN_TASK_END_DATE + " = '" + task.getEndDate() + "'," +
                COLUMN_TASK_STATE + " = '" + task.getTaskState() + "'" +
                " WHERE " + COLUMN_ID + " = " + task.getTaskId();*/

        db.update(TABLE_NAME, contentValues, COLUMN_ID + " = ?", new String[]{String.valueOf(task.getTaskId())});
    }

    public ArrayList<Task> getAllActiveTasks(String taskState, Boolean isIdProvided, int id) {

        //Log.i("getall task", "true");

        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<Task> tasks = new ArrayList<>();

        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_TASK_ISACTIVE + " = 1 AND " + COLUMN_TASK_STATE + " LIKE '"+ taskState+"'";

        String conditionForId = "";
        if(isIdProvided) {
            query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_TASK_ISACTIVE + " = 1 AND " + COLUMN_ID + " = " + id;
        }

        Cursor cursor = db.rawQuery(query, null);

        //Log.i("cursor", String.valueOf(cursor.getCount()) );

        try {
            if(cursor.getCount() > 0) {
                cursor.moveToFirst();
                do {
                    Task task = new Task();
                    task.setTaskId(cursor.getInt(cursor.getColumnIndex(COLUMN_ID)));
                    task.setTaskTitle(cursor.getString(cursor.getColumnIndex(COLUMN_TASK_TITLE)));
                    task.setTaskDescription(cursor.getString(cursor.getColumnIndex(COLUMN_TASK_DESCRIPTION)));
                    task.setTaskState(cursor.getString(cursor.getColumnIndex(COLUMN_TASK_STATE)));
                    task.setIsActive(true);
                    task.setStartDate(cursor.getLong(cursor.getColumnIndex(COLUMN_TASK_CREATE_DATE)));
                    task.setEndDate(cursor.getLong(cursor.getColumnIndex(COLUMN_TASK_END_DATE)));
                    tasks.add(task);
                }
                while (cursor.moveToNext());
            }
            else { }
                //Log.i("cursor null", "true");
        } catch (Exception e) {
            e.printStackTrace();
        }
        db.close();
        return tasks;
    }
}
