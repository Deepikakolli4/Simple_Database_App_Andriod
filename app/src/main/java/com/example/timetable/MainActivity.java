package com.example.timetable;

import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    DBHelper dbHelper;
    EditText subjectInput, timeInput, locationInput;
    Spinner daySpinner;
    Button addButton, updateButton;
    ListView scheduleList;
    ArrayList<String> scheduleArray;
    ArrayAdapter<String> scheduleAdapter;
    ArrayList<Integer> scheduleIds;
    int selectedId = -1;  // To store the selected item’s ID for update

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DBHelper(this);
        subjectInput = findViewById(R.id.subjectInput);
        timeInput = findViewById(R.id.timeInput);
        locationInput = findViewById(R.id.locationInput);
        daySpinner = findViewById(R.id.daySpinner);
        addButton = findViewById(R.id.addButton);
        updateButton = findViewById(R.id.updateButton);
        scheduleList = findViewById(R.id.scheduleList);

        // Populate Spinner with Days
        String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, days);
        daySpinner.setAdapter(adapter);

        loadSchedule();

        // Add New Schedule
        addButton.setOnClickListener(view -> {
            String subject = subjectInput.getText().toString().trim();
            String day = daySpinner.getSelectedItem().toString();
            String time = timeInput.getText().toString().trim();
            String location = locationInput.getText().toString().trim();

            if (!subject.isEmpty() && !time.isEmpty() && !location.isEmpty()) {
                if (dbHelper.addSchedule(subject, day, time, location)) {
                    Toast.makeText(this, "Schedule added!", Toast.LENGTH_SHORT).show();
                    clearInputs();
                    loadSchedule();
                }
            } else {
                Toast.makeText(this, "Fill all fields!", Toast.LENGTH_SHORT).show();
            }
        });

        // Select an item from the list to update
        scheduleList.setOnItemClickListener((parent, view, position, id) -> {
            selectedId = scheduleIds.get(position);  // Get ID of selected item
            String[] data = scheduleArray.get(position).split(" - | at | in ");

            // Populate input fields with selected schedule
            subjectInput.setText(data[0]);
            timeInput.setText(data[1]);
            locationInput.setText(data[3]);
            daySpinner.setSelection(getDayPosition(data[2]));

            addButton.setVisibility(View.GONE);
            updateButton.setVisibility(View.VISIBLE);
        });

        // Update Schedule
        updateButton.setOnClickListener(view -> {
            if (selectedId == -1) {
                Toast.makeText(this, "No schedule selected!", Toast.LENGTH_SHORT).show();
                return;
            }

            String subject = subjectInput.getText().toString().trim();
            String day = daySpinner.getSelectedItem().toString();
            String time = timeInput.getText().toString().trim();
            String location = locationInput.getText().toString().trim();

            if (!subject.isEmpty() && !time.isEmpty() && !location.isEmpty()) {
                if (dbHelper.updateSchedule(selectedId, subject, day, time, location)) {
                    Toast.makeText(this, "Schedule updated!", Toast.LENGTH_SHORT).show();
                    clearInputs();
                    loadSchedule();
                    addButton.setVisibility(View.VISIBLE);
                    updateButton.setVisibility(View.GONE);
                    selectedId = -1; // Reset selection
                }
            } else {
                Toast.makeText(this, "Fill all fields!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadSchedule() {
        scheduleArray = new ArrayList<>();
        scheduleIds = new ArrayList<>();
        Cursor cursor = dbHelper.getSchedule();

        while (cursor.moveToNext()) {
            scheduleIds.add(cursor.getInt(0));
            scheduleArray.add(cursor.getString(1) + " - " + cursor.getString(2) + " at " + cursor.getString(3) + " in " + cursor.getString(4));
        }

        scheduleAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, scheduleArray);
        scheduleList.setAdapter(scheduleAdapter);
    }

    private void clearInputs() {
        subjectInput.setText("");
        timeInput.setText("");
        locationInput.setText("");
        daySpinner.setSelection(0);
    }

    private int getDayPosition(String day) {
        String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
        for (int i = 0; i < days.length; i++) {
            if (days[i].equals(day)) {
                return i;
            }
        }
        return 0;
    }
}
