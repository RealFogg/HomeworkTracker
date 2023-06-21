package com.example.homeworktracker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.ArrayList;

public class AddEditCourseActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText courseEditText;
    private Spinner courseColorSpinner;
    private Button courseSaveButton;
    private Button courseCancelButton;

    private String addEditMode = "";
    private int courseId = 0;
    private String courseCode = "";
    private String courseColor = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_course);

        // Widget references
        courseEditText = (EditText) findViewById(R.id.courseEditText);
        courseColorSpinner = (Spinner) findViewById(R.id.courseColorSpinner);
        courseSaveButton = (Button) findViewById(R.id.courseSaveButton);
        courseCancelButton = (Button) findViewById(R.id.courseCancelButton);

        // Listeners
        courseSaveButton.setOnClickListener(this);
        courseCancelButton.setOnClickListener(this);

        // Retrieve the editMode from the intent
        Intent intent = getIntent();
        addEditMode = intent.getStringExtra("addEditMode");

        AssignmentsDB db = new AssignmentsDB(this);

        if (addEditMode.equals("edit")) {
            // Get the courseId from the intent
            courseId = intent.getIntExtra("courseId", 0);
            // Create a temporary course object to retrieve data from the database
            Course tempCourse = db.getCourse(courseId);
            // Use the temporary course to get the course code and color
            courseCode = tempCourse.getCourseCode();
            courseColor = tempCourse.getColor();

            // Set the course editText text to the course code
            courseEditText.setText(courseCode);
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                    R.array.colors, android.R.layout.simple_spinner_dropdown_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            int colorPosition = adapter.getPosition(courseColor);
            // Set the color spinner text to the course color
            courseColorSpinner.setSelection(colorPosition);
        }
    }

    private void saveCourse() {
        Intent intent = getIntent();
        String addEditMode = intent.getStringExtra("addEditMode");

        // Get the course name and color from the input fields
        String courseString = courseEditText.getText().toString();
        String courseColorString = courseColorSpinner.getSelectedItem().toString();

        // Save the data to the database
        AssignmentsDB db = new AssignmentsDB(this);

        // If in edit mode update the database
        if (addEditMode.equals("edit")) {
            int courseId = intent.getIntExtra("courseId", 0);
            Course course = new Course(courseId, courseString, courseColorString);
            db.updateCourse(course);
        }
        else if (addEditMode.equals("add")) {

            // Check if the courseIds are incremental
            int i = 1;
            boolean isIncremental = true;
            ArrayList<Integer> courseIDs = db.getCourseIDs();
            for (int courseId : courseIDs) {
                if (courseId != i) {
                    isIncremental = false;
                }
                i += 1;
            }

            /* If courses table is not empty and does not start with an Id of 1 then update all courses
             *  to by setting their id's to one higher than the last starting at id 1 */
            ArrayList<Course> courses = db.getCourses();
            if (!courses.isEmpty() && !isIncremental) {
                int j = 1;
                // Reset ID's to increment from 1
                for (Course course : courses) {
                    course.setCourseId(j);
                    db.updateCourse(course);
                    j += 1;
                }
            }

            int courseId;
            if (courses.isEmpty()) {
                // If no courses in database make sure first course starts with an ID of 1
                courseId = 1;
            } else {
                // Else increment each course's ID 1 higher than the last
                int numCourses = courses.size();
                courseId = numCourses + 1;
            }

            // Create and insert the new course into the database
            Course course = new Course(courseId, courseString, courseColorString);
            db.insertCourse(course);
        }

        // Finish the activity
        this.finish();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.courseSaveButton:
                saveCourse();
                break;
            case R.id.courseCancelButton:
                this.finish();
                break;
        }
    }
}