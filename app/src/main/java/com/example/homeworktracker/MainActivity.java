package com.example.homeworktracker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText assignmentNameEditText;
    private Button addItemButton;
    private EditText dueDateEditText;
    private LinearLayout layout;
    private Spinner courseSpinner;
    private Button addCoursesButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get references to widgets
        assignmentNameEditText = (EditText) findViewById(R.id.assignmentNameEditText);
        addItemButton = (Button) findViewById(R.id.addItemButton);
        dueDateEditText = (EditText) findViewById(R.id.dueDateEditText);
        layout = (LinearLayout) findViewById(R.id.assignmentListLayout);
        courseSpinner = (Spinner) findViewById(R.id.courseSpinner);
        addCoursesButton = (Button) findViewById(R.id.addCoursesButton);

        // Set Listeners
        addItemButton.setOnClickListener(this);
        addCoursesButton.setOnClickListener(this);

        refreshCourseSpinner();
        refreshAssignmentList();
    }

    private void addAssignmentCard() {
        String assignmentNameString = assignmentNameEditText.getText().toString();
        String courseCodeString = courseSpinner.getSelectedItem().toString();
        String dueDateString = dueDateEditText.getText().toString();

        // Verify all fields are filled
        if (assignmentNameString.equals("") || courseCodeString.equals("") || dueDateString.equals("")) {
            Toast.makeText(this, "Please fill in the required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Verify date
        String[] dateSplit = dueDateString.split("/");
        if (!verifyDate(dateSplit)) {
            return;
        }

        // Inflate the card view
        View cardView = getLayoutInflater().inflate(R.layout.assignment_card, null);

        // Widget references for the created card view
        ImageView colorIndicatorImageView = (ImageView) cardView.findViewById(R.id.colorIndicatorImageView);
        TextView dueDateTextView = (TextView) cardView.findViewById(R.id.dueDateTextView);
        TextView courseIdTextView = (TextView) cardView.findViewById(R.id.courseIdTextView);
        TextView assignmentNameTextView = (TextView) cardView.findViewById(R.id.assignmentNameTextView);
        Button removeButton = (Button) cardView.findViewById(R.id.removeButton);

        // Create a database object and Create a course object using string from the courseSpinner
        AssignmentsDB db = new AssignmentsDB(this);
        Course course = db.getCourse(courseCodeString);

        // Set the color for the image view using the course color from the course object
        String colorString = "";
        switch(course.getColor()) {
            case "Purple":
                colorIndicatorImageView.setImageResource(R.drawable.ic_purple_square);
                colorIndicatorImageView.setContentDescription("purple");
                colorString = "purple";
                break;
            case "Blue":
                colorIndicatorImageView.setImageResource(R.drawable.ic_blue_square);
                colorIndicatorImageView.setContentDescription("blue");
                colorString = "blue";
                break;
            case "Green":
                colorIndicatorImageView.setImageResource(R.drawable.ic_green_square);
                colorIndicatorImageView.setContentDescription("green");
                colorString = "green";
                break;
            case "Yellow":
                colorIndicatorImageView.setImageResource(R.drawable.ic_yellow_orange_square);
                colorIndicatorImageView.setContentDescription("yellow");
                colorString = "yellow";
                break;
            case "Red":
                colorIndicatorImageView.setImageResource(R.drawable.ic_red_square);
                colorIndicatorImageView.setContentDescription("red");
                colorString = "red";
                break;
            default:
                colorIndicatorImageView.setImageResource(R.drawable.ic_teal_square);
                colorIndicatorImageView.setContentDescription("teal");
                colorString = "teal";
                break;
        }

        dueDateTextView.setText(dueDateString);
        dueDateEditText.setText("");

        courseIdTextView.setText(courseCodeString);

        assignmentNameTextView.setText(assignmentNameString);
        assignmentNameEditText.setText("");

        // Increments an iterator and checks if it has been used as an assignment id yet
        // This prevents me from using an existing id
        int i = 0;
        boolean done = false;
        while (!done) {
            i += 1;
            if (!db.assignmentIdIsDatabased(i)) {
                done = true;
            }
        }

        // Convert the user entered date into a format that can be used in the database
        String dbDueDateString = dateSplit[2] + "-" + dateSplit[0] + "-" + dateSplit[1];

        // Create the assignment using the data gathered so far
        Assignment assignment = new Assignment(i, courseCodeString, assignmentNameString, dbDueDateString, colorString);

        // Add assignment to database
        long rowID = db.insertAssignment(assignment);

        //This works because the listener is connected to the removeButton which is connected to
        //the cardView I am creating. This means that when I add the cardView to the activity_main's
        //LinearLayout the listener persists through the cardView.
        removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Delete the assignment from the database and remove it from the layout view
                db.deleteAssignment(rowID);
                layout.removeView(cardView);
            }
        });

        // Add the Card to the linear layout
        layout.addView(cardView);

        // Refresh the assignment list
        refreshAssignmentList();
    }

    // Method to verify if the date entered is valid
    private boolean verifyDate(String[] dateSplit) {
        // Verify the length of the month, day, and year
        if (dateSplit.length != 3) {
            Toast.makeText(this, "Please enter a valid date", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (dateSplit[0].length() != 2 || dateSplit[1].length() != 2) {
            Toast.makeText(this, "Month and Day should be 2 characters", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (dateSplit[2].length() != 4) {
            Toast.makeText(this, "Year should be 4 characters", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Verify the contents of the month, day, year
        int month = Integer.parseInt(dateSplit[0]);
        int day = Integer.parseInt(dateSplit[1]);
        int year = Integer.parseInt(dateSplit[2]);
        if (month < 1 || month > 12) {
            Toast.makeText(this, "Please enter a valid month", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (day < 1 || day > 31) {
            Toast.makeText(this, "Please enter a valid day", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (year < 2023) {
            Toast.makeText(this, "Please enter a valid year", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Return true if date is valid
        return true;
    }

    // Method to refresh the contents of the course spinner
    private void refreshCourseSpinner() {
        AssignmentsDB db = new AssignmentsDB(this);
        ArrayList<Course> courses = db.getCourses();
        ArrayList<String> courseList = new ArrayList<>();

        if (courses.isEmpty()) {
            addCoursesButton.setVisibility(View.VISIBLE);
            courseSpinner.setVisibility(View.INVISIBLE);
        }
        else {
            addCoursesButton.setVisibility(View.INVISIBLE);
            courseSpinner.setVisibility(View.VISIBLE);
            for (Course course : courses) {
                String courseCode = course.getCourseCode();
                courseList.add(courseCode);
            }
        }

        // Create an array adapter for the course spinner using the course list
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, courseList); // try simple_spinner_item if this works
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        courseSpinner.setAdapter(arrayAdapter);
    }

    // Method to refresh the list of assignments
    private void refreshAssignmentList() {
        AssignmentsDB db = new AssignmentsDB(this);
        ArrayList<Assignment> assignments = db.getSortedAssignments();

        // If assignments table in database is empty don't refresh the list
        if (assignments.isEmpty()) {
            return;
        }

        // Remove the current assignment cards
        layout.removeAllViews();

        // Add all the assignments from the database as cards
        for(Assignment assignment : assignments) {
            // Inflate the card view
            View cardView = getLayoutInflater().inflate(R.layout.assignment_card, null);

            // Widget references for the card view
            ImageView colorIndicatorImageView = (ImageView) cardView.findViewById(R.id.colorIndicatorImageView);
            TextView dueDateTextView = (TextView) cardView.findViewById(R.id.dueDateTextView);
            TextView courseIdTextView = (TextView) cardView.findViewById(R.id.courseIdTextView);
            TextView assignmentNameTextView = (TextView) cardView.findViewById(R.id.assignmentNameTextView);
            Button removeButton = (Button) cardView.findViewById(R.id.removeButton);

            // Get assignment information from the database
            String courseIdString = assignment.getCourseID();
            String assignmentNameString = assignment.getName();
            String dueDateString = assignment.getDueDate();
            String colorString = assignment.getColor();

            // Check if Assignment table's color matches Course table's color
            ArrayList<Course> courses = db.getCourses();
            for (Course course : courses) {
                if (courseIdString.equals(course.getCourseCode())) {
                    if (!colorString.equals(course.getColor().toLowerCase())) {
                        // If Colors don't match change the color to the course Table's color
                        colorString = course.getColor().toLowerCase();
                    }
                }
            }

            // Set the information in the inflated card
            courseIdTextView.setText(courseIdString);

            assignmentNameTextView.setText(assignmentNameString);
            assignmentNameEditText.setText("");

            // Convert the due date back to my date format
            String[] dateSplit = dueDateString.split("-");
            String cardDueDateString = dateSplit[1] + "/" + dateSplit[2] + "/" + dateSplit[0];

            dueDateTextView.setText(cardDueDateString);
            dueDateEditText.setText("");

            switch(colorString) {
                case "purple":
                    colorIndicatorImageView.setImageResource(R.drawable.ic_purple_square);
                    colorIndicatorImageView.setContentDescription("purple");
                    break;
                case "blue":
                    colorIndicatorImageView.setImageResource(R.drawable.ic_blue_square);
                    colorIndicatorImageView.setContentDescription("blue");
                    break;
                case "green":
                    colorIndicatorImageView.setImageResource(R.drawable.ic_green_square);
                    colorIndicatorImageView.setContentDescription("green");
                    break;
                case "yellow":
                    colorIndicatorImageView.setImageResource(R.drawable.ic_yellow_orange_square);
                    colorIndicatorImageView.setContentDescription("yellow");
                    break;
                case "red":
                    colorIndicatorImageView.setImageResource(R.drawable.ic_red_square);
                    colorIndicatorImageView.setContentDescription("red");
                    break;
                default:
                    colorIndicatorImageView.setImageResource(R.drawable.ic_teal_square);
                    colorIndicatorImageView.setContentDescription("teal");
                    break;
            }

            removeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Remove assignment from database and activity
                    db.deleteAssignment(assignment);
                    layout.removeView(cardView);
                }
            });

            // Add the Card to the linear layout
            layout.addView(cardView);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        refreshCourseSpinner();
        refreshAssignmentList();
    }

    /* *** Button Listener *** */
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.addItemButton:
                addAssignmentCard();
                break;
            case R.id.addCoursesButton:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_homework_tracker_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}