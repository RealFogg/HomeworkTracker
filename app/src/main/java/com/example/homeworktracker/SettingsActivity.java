package com.example.homeworktracker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    private Button addCourseButton;
    private Button removeCourseButton;
    private ListView courseListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Widget References
        addCourseButton = (Button) findViewById(R.id.addCourseButton);
        removeCourseButton = (Button) findViewById(R.id.removeCourseButton);
        courseListView = (ListView) findViewById(R.id.courseListView);

        // Listeners
        addCourseButton.setOnClickListener(this);
        removeCourseButton.setOnClickListener(this);
        courseListView.setOnItemClickListener(this);

        refreshList();
    }

    private void addCourse() {
        Intent intent = new Intent(this, AddEditCourseActivity.class);
        intent.putExtra("addEditMode", "add");
        this.startActivity(intent);
    }

    private void removeCourse(AssignmentsDB db, ArrayList<Integer> courseIds) {
        db.deleteCourse(courseIds.get(courseIds.size()-1));
        refreshList();
    }

    private void refreshList() {
        AssignmentsDB db = new AssignmentsDB(this);
        ArrayList<Course> courses = db.getCourses();
        ArrayList<HashMap<String, String>> courseData = new ArrayList<>();

        // Add each course in the database to the course data arraylist
        for (Course course : courses) {
            // Put each courses code and color in a hashmap
            HashMap<String, String> map = new HashMap<>();
            map.put("courseTitle", "Course " + String.valueOf(course.getCourseId()));
            map.put("courseCode", course.getCourseCode());
            map.put("courseColor", course.getColor());
            courseData.add(map);
        }

        // Get a reference to the course item layout
        int resource = R.layout.course_item;
        // The Key values used in the course hashmaps
        String[] from = {"courseTitle", "courseCode", "courseColor"};
        // Where the data from the hashmaps will be displayed
        int[] to = {R.id.courseTitleTextView, R.id.courseCodeTextView, R.id.courseColorTextView};

        // Adapter to create the course item so I can add it to the courseList
        SimpleAdapter adapter = new SimpleAdapter(this, courseData, resource, from, to);
        courseListView.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshList();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.addCourseButton) {
            addCourse();
        }
        else if (view.getId() == R.id.removeCourseButton) {
            AssignmentsDB db = new AssignmentsDB(this);
            ArrayList<Integer> courseIds = db.getCourseIDs();
            if (courseIds.size() > 0) {
                removeCourse(db, courseIds);
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(this, AddEditCourseActivity.class);
        intent.putExtra("addEditMode", "edit");
        int courseId = position + 1; // If courses are added to database properly this should work
        intent.putExtra("courseId", courseId);
        startActivity(intent);
    }
}