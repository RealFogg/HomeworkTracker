package com.example.homeworktracker;

public class Assignment {

    // Instance variables
    private int assignmentID;
    private String courseID;
    private String name;
    private String dueDate;
    private String color;

    // Constructors
    public Assignment() {
        courseID = "";
        name = "";
        dueDate = "";
        color = "";
    }
    public Assignment(String courseID, String name, String dueDate, String color) {
        this.courseID = courseID;
        this.name = name;
        this.dueDate = dueDate;
        this.color = color;
    }
    public Assignment(int assignmentID, String courseID, String name, String dueDate, String color) {
        this.assignmentID = assignmentID;
        this.courseID = courseID;
        this.name = name;
        this.dueDate = dueDate;
        this.color = color;
    }

    // Getter and setters
    public int getAssignmentID() { return assignmentID; }
    public void setAssignmentID(int assignmentID) { this.assignmentID = assignmentID; }

    public String getCourseID() { return courseID; }
    public void setCourseID(String courseID) { this.courseID = courseID; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDueDate() { return dueDate; }
    public void setDueDate(String dueDate) { this.dueDate = dueDate; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }
}
