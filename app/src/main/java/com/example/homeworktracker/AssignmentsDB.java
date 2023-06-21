package com.example.homeworktracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

public class AssignmentsDB {
    // DB constants
    public static final String DB_NAME = "assignments.db";
    public static final int DB_VERSION = 3;

    // Assignment table constants
    public static final String ASSIGNMENT_TABLE = "assignment";

    public static final String ASSIGNMENT_ID = "_id";
    public static final int ASSIGNMENT_ID_COL = 0;

    public static final String ASSIGNMENT_COURSE_ID = "course_id";
    public static final int ASSIGNMENT_COURSE_ID_COL = 1;

    public static final String ASSIGNMENT_NAME = "assignment_name";
    public static final int ASSIGNMENT_NAME_COL = 2;

    public static final String DUE_DATE = "due_date";
    public static final int DUE_DATE_COL = 3;

    public static final String COLOR = "color";
    public static final int COLOR_COL = 4;

    // Course table constants
    public static final String COURSE_TABLE = "course";

    public static final String COURSE_ID = "_id";
    public static final int COURSE_ID_COL = 0;

    public static final String COURSE_CODE = "course_code";
    public static final int COURSE_CODE_COL = 1;

    public static final String COURSE_COLOR = "color";
    public static final int COURSE_COLOR_COL = 2;

    // Create and Drop tables
    public static final String CREATE_ASSIGNMENT_TABLE = "CREATE TABLE " + ASSIGNMENT_TABLE + " (" +
            ASSIGNMENT_ID        + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            ASSIGNMENT_COURSE_ID + " TEXT NOT NULL, " +
            ASSIGNMENT_NAME      + " TEXT NOT NULL, " +
            DUE_DATE             + " TEXT NOT NULL, " +
            COLOR                + " TEXT NOT NULL);";

    public static final String DROP_ASSIGNMENT_TABLE = "DROP TABLE IF EXISTS " + ASSIGNMENT_TABLE;

    // Course Table
    public static final String CREATE_COURSE_TABLE = "CREATE TABLE " + COURSE_TABLE + " (" +
            COURSE_ID + " INTEGER PRIMARY KEY, " +
            COURSE_CODE + " TEXT NOT NULL, " +
            COURSE_COLOR + " TEXT NOT NULL);";

    public static final String DROP_COURSE_TABLE = "DROP TABLE IF EXISTS " + COURSE_TABLE;

    // Helper
    private static class DBHelper extends SQLiteOpenHelper {
        // Add a constructor which will pass its SQLiteOpenHelper super class four arguments
        public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        // If Android doesn't find a database on the device then create one in onCreate
        @Override
        public void onCreate(SQLiteDatabase db) {
            // Create table
            db.execSQL(CREATE_ASSIGNMENT_TABLE);
            db.execSQL(CREATE_COURSE_TABLE);
        }

        // Use onUpgrade method to drop and recreate the database.
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.d("Homework Tracker", "Upgrading db from version " + oldVersion + " to " + newVersion);
            db.execSQL(AssignmentsDB.DROP_ASSIGNMENT_TABLE);
            db.execSQL(AssignmentsDB.DROP_COURSE_TABLE);
            onCreate(db);
        }
    }

    // Database and database helper objects
    private SQLiteDatabase db;
    private DBHelper dbHelper;

    // Constructor
    public AssignmentsDB(Context context) {
        dbHelper = new DBHelper(context, DB_NAME, null, DB_VERSION);
    }

    // Private Methods
    private void openReadableDB() { db = dbHelper.getReadableDatabase(); }
    private void openWriteableDB() { db = dbHelper.getWritableDatabase(); }
    private void closeDB() {
        if (db != null)
            db.close();
    }

    // Public methods
    /** ****************             Methods for Assignment table              ****************** **/
    // Method to get a list of all assignments
    public ArrayList<Assignment> getSortedAssignments() {
        ArrayList<Assignment> assignments = new ArrayList<>();
        openReadableDB();

        // Sorts by Due Date first then courseId
        Cursor cursor = db.query(ASSIGNMENT_TABLE,
                null, null, null,
                null, null, DUE_DATE + " ASC, " + ASSIGNMENT_COURSE_ID + " ASC" );
        while (cursor.moveToNext()) {
            assignments.add(getAssignmentFromCursor(cursor));
        }
        if (cursor != null)
            cursor.close();
        closeDB();

        return assignments;
    }

    private static Assignment getAssignmentFromCursor(Cursor cursor) {
        if (cursor == null || cursor.getCount() == 0) {
            return null;
        }
        else {
            try {
                Assignment assignment = new Assignment(
                        cursor.getInt(ASSIGNMENT_ID_COL),
                        cursor.getString(ASSIGNMENT_COURSE_ID_COL),
                        cursor.getString(ASSIGNMENT_NAME_COL),
                        cursor.getString(DUE_DATE_COL),
                        cursor.getString(COLOR_COL));
                return assignment;
            }
            catch (Exception e) {
                return null;
            }
        }
    }

    public boolean assignmentIdIsDatabased(int id) {
        String where = ASSIGNMENT_ID + "= ?";
        String[] whereArgs = { Integer.toString(id) };

        this.openReadableDB();
        Cursor cursor = db.query(ASSIGNMENT_TABLE, null, where,
                whereArgs, null, null, null);
        boolean isDBed = cursor.moveToFirst();
        if (cursor != null)
            cursor.close();
        closeDB();

        return isDBed;
    }

    public long insertAssignment(Assignment assignment) {
        ContentValues cv = new ContentValues();
        cv.put(ASSIGNMENT_ID, assignment.getAssignmentID());
        cv.put(ASSIGNMENT_COURSE_ID, assignment.getCourseID());
        cv.put(ASSIGNMENT_NAME, assignment.getName());
        cv.put(DUE_DATE, assignment.getDueDate());
        cv.put(COLOR, assignment.getColor());

        Log.d("Homework", "Inserting new item into database at " + assignment.getAssignmentID());

        this.openWriteableDB();
        long rowID = db.insert(ASSIGNMENT_TABLE, null, cv);
        this.closeDB();

        return rowID;
    }

    public int deleteAssignment(long id) {
        String where = ASSIGNMENT_ID + "= ?";
        String[] whereArgs = { String.valueOf(id) };

        Log.d("Homework", "Deleting an item from database at " + id);

        this.openWriteableDB();
        int rowCount = db.delete(ASSIGNMENT_TABLE, where, whereArgs);
        closeDB();

        return rowCount;
    }

    public int deleteAssignment(Assignment assignment) {
        String where = ASSIGNMENT_ID + "= ?";
        long id = assignment.getAssignmentID();
        String[] whereArgs = { String.valueOf(id) };

        Log.d("Homework", "Deleting an item from database at " + assignment.getAssignmentID());

        this.openWriteableDB();
        int rowCount = db.delete(ASSIGNMENT_TABLE, where, whereArgs);
        this.closeDB();

        return rowCount;
    }

    /** ****************             Methods for settings table              ****************** **/
    public ArrayList<Course> getCourses() {
        ArrayList<Course> courses = new ArrayList<>();
        openReadableDB();

        Cursor cursor = db.query(COURSE_TABLE,
                null, null, null,
                null, null, null);
        while (cursor.moveToNext()) {
            courses.add(getCourseFromCursor(cursor));
        }
        if (cursor != null)
            cursor.close();
        closeDB();

        return courses;
    }

    public Course getCourse(int id) {
        String where = COURSE_ID + "= ?";
        String[] whereArgs = { Integer.toString(id) };

        this.openReadableDB();
        Cursor cursor = db.query(COURSE_TABLE, null, where,
                whereArgs, null, null, null);
        cursor.moveToFirst();
        Course course = getCourseFromCursor(cursor);
        if (cursor != null)
            cursor.close();
        this.closeDB();

        return course;
    }

    public Course getCourse(String courseCode) {
        String where = COURSE_CODE + "= ?";
        String[] whereArgs = { courseCode };

        this.openReadableDB();
        Cursor cursor = db.query(COURSE_TABLE, null, where,
                whereArgs, null, null, null);
        cursor.moveToFirst();
        Course course = getCourseFromCursor(cursor);
        if (cursor != null)
            cursor.close();
        this.closeDB();

        return course;
    }

    private static Course getCourseFromCursor(Cursor cursor) {
        if (cursor == null || cursor.getCount() == 0) {
            return null;
        }
        else {
            try {
                Course course = new Course(
                        cursor.getInt(COURSE_ID_COL),
                        cursor.getString(COURSE_CODE_COL),
                        cursor.getString(COURSE_COLOR_COL));
                return course;
            }
            catch (Exception e) {
                return null;
            }
        }
    }

    public ArrayList<Integer> getCourseIDs() {
        ArrayList<Integer> courseIDs = new ArrayList<>();
        openReadableDB();

        Cursor cursor = db.query(COURSE_TABLE,
                null, null, null,
                null, null, COURSE_ID + " ASC");
        while (cursor.moveToNext()) {
            if (cursor == null || cursor.getCount() == 0) {
                return null;
            }
            else {
                try {
                    int courseNum = cursor.getInt(COURSE_ID_COL);
                    courseIDs.add(courseNum);
                }
                catch (Exception e) {
                    return null;
                }
            }
        }
        if (cursor != null)
            cursor.close();
        closeDB();

        return courseIDs;
    }

    public long insertCourse(Course course) {
        ContentValues cv = new ContentValues();
        cv.put(COURSE_ID, course.getCourseId());
        cv.put(COURSE_CODE, course.getCourseCode());
        cv.put(COURSE_COLOR, course.getColor());

        Log.d("Homework", "Inserting new item into Course database");

        this.openWriteableDB();
        long rowID = db.insert(COURSE_TABLE, null, cv);
        this.closeDB();

        return rowID;
    }

    public void updateCourse(Course course) {
        ContentValues cv = new ContentValues();
        cv.put(COURSE_ID, course.getCourseId());
        cv.put(COURSE_CODE, course.getCourseCode());
        cv.put(COURSE_COLOR, course.getColor());

        String where = COURSE_ID + "= ?";
        String[] whereArgs = { String.valueOf(course.getCourseId()) };

        Log.d("Homework", "Updating an item in database at ");

        this.openWriteableDB();
        int rowCount = db.update(COURSE_TABLE, cv, where, whereArgs);
        closeDB();
    }

    public void deleteCourse(long id) {
        String where = COURSE_ID + "= ?";
        String[] whereArgs = { String.valueOf(id) };

        Log.d("Homework", "Deleting an item from database at ");

        this.openWriteableDB();
        int rowCount = db.delete(COURSE_TABLE, where, whereArgs);
        Log.d("Homework", "Deleting an item from database at row: " + rowCount);
        closeDB();
    }
}
