package com.example.cleareyes.localoyeproject;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


public class CreateTask extends ActionBarActivity {

    EditText titleEditText;
    EditText noteEditText;
    Button dateButton, createTaskButton, completeTaskButton;
    Calendar c = Calendar.getInstance();
    int mYear = c.get(Calendar.YEAR);
    int mMonth = c.get(Calendar.MONTH);
    int mDay = c.get(Calendar.DAY_OF_MONTH);
    Date endDate = null;
    int id;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(CreateTask.this, MainActivity.class);
        startActivity(intent);
        CreateTask.this.finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_task);
        Intent intent = getIntent();

        id = intent.getIntExtra("id", -1);
        if(id != -1)
            getSupportActionBar().setTitle("Create New Task");
        else
            getSupportActionBar().setTitle("Update Task Details");

        titleEditText = (EditText) findViewById(R.id.titleEditText);
        noteEditText = (EditText) findViewById(R.id.noteEditText);
        dateButton = (Button) findViewById(R.id.selectDateButton);
        createTaskButton = (Button) findViewById(R.id.createTaskButton);
        completeTaskButton = (Button) findViewById(R.id.completeTaskButton);

        titleEditText.getBackground().clearColorFilter();
        noteEditText.getBackground().clearColorFilter();

        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog dialog = new DatePickerDialog(CreateTask.this,
                        new mDateSetListener(), mYear, mMonth, mDay);
                dialog.show();
            }
        });

        if(id != -1) {
            final DatabaseHandler databaseHandler = new DatabaseHandler(CreateTask.this, "LocalOye", null ,1);
            ArrayList<Task> tasks = new ArrayList<>();
            tasks = databaseHandler.getAllActiveTasks("", true, id);

            endDate = new Date(tasks.get(0).getEndDate());
            /*mYear = endDate.getYear();
            mMonth = endDate.getMonth();
            mDay = endDate.getDate();*/
            dateButton.setText(endDate.getMonth()+1+"/"+endDate.getDate()+"/" + endDate.getYear());

            Log.i("state", tasks.get(0).getTaskState().toString());
            if(tasks.get(0).getTaskState().toString().equals("completed")) {

                createTaskButton.setVisibility(View.INVISIBLE);
                completeTaskButton.setVisibility(View.INVISIBLE);
                titleEditText.setEnabled(false);
                noteEditText.setEnabled(false);
                dateButton.setEnabled(false);
                titleEditText.setText(tasks.get(0).getTaskTitle());
                noteEditText.setText(tasks.get(0).getTaskDescription());
                endDate = new Date(tasks.get(0).getEndDate());

            } else {
                createTaskButton.setText("  Update Task  ");
                completeTaskButton.setVisibility(View.VISIBLE);
                titleEditText.setText(tasks.get(0).getTaskTitle());
                noteEditText.setText(tasks.get(0).getTaskDescription());
                endDate = new Date(tasks.get(0).getEndDate());

                final ArrayList<Task> finalTasks = tasks;
                completeTaskButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finalTasks.get(0).setTaskTitle(titleEditText.getText().toString());
                        finalTasks.get(0).setTaskDescription(noteEditText.getText().toString());
                        finalTasks.get(0).setEndDate(endDate.getTime());
                        finalTasks.get(0).setTaskState("completed");
                        databaseHandler.updateTask(finalTasks.get(0));
                        Intent intent = new Intent(CreateTask.this, MainActivity.class);
                        startActivity(intent);
                        CreateTask.this.finish();
                    }
                });
            }
        }
        else {
            titleEditText.requestFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
        }


        createTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(titleEditText.getText().toString().trim().length() > 0) {
                    if(noteEditText.getText().toString().trim().length() > 0) {
                        if(endDate != null) {
                            Task task = new Task();
                            task.setTaskTitle(titleEditText.getText().toString());
                            task.setTaskDescription(noteEditText.getText().toString());
                            task.setStartDate(System.currentTimeMillis());
                            task.setEndDate(endDate.getTime());
                            task.setIsActive(true);
                            if(endDate.getYear() >= mYear && endDate.getMonth() >= mMonth && endDate.getDate() >= mDay) {
                                task.setTaskState("current");
                            }
                            else
                                task.setTaskState("pending");
                            DatabaseHandler databaseHandler = new DatabaseHandler(CreateTask.this, "LocalOye", null ,1);
                            if(id != -1) {
                                task.setTaskId(id);
                                databaseHandler.updateTask(task);
                            }
                            else {
                                databaseHandler.addTask(task);
                            }
                            Intent intent = new Intent(CreateTask.this, MainActivity.class);
                            startActivity(intent);
                            CreateTask.this.finish();
                        }
                        else {
                            Toast.makeText(CreateTask.this, "Please select task completion date", Toast.LENGTH_SHORT).show();
                            Log.i("date", String.valueOf(endDate));
                        }
                    }
                    else
                        Toast.makeText(CreateTask.this, "Please enter the note", Toast.LENGTH_SHORT).show();
                }
                else
                    Toast.makeText(CreateTask.this, "Please enter the note title", Toast.LENGTH_SHORT).show();
            }
        });
    }

    class mDateSetListener implements DatePickerDialog.OnDateSetListener {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            // TODO Auto-generated method stub
            // getCalender();
            try {
                int mYear = year;
                int mMonth = monthOfYear;
                int mDay = dayOfMonth;
                dateButton.setText(new StringBuilder()
                        // Month is 0 based so add 1
                        .append(mMonth + 1).append("/").append(mDay).append("/")
                        .append(mYear).append(" "));
                endDate = new Date();
                endDate.setYear(year);
                endDate.setMonth(monthOfYear);
                endDate.setDate(dayOfMonth);
                endDate.setHours(0);
                endDate.setMinutes(0);
                endDate.setSeconds(0);
            }catch (Exception e) {
                endDate = null;
                Log.i("date is null", "true");
            }
        }
    }

}
