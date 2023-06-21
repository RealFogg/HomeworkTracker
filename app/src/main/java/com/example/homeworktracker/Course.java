package com.example.homeworktracker;

public class Course {

    private int courseId;
    private String courseCode;
    private String color;

    public Course() {
        courseId = 0;
        courseCode = "";
        color = "";
    }
    public Course(int courseId, String courseCode, String color) {
        this.courseId = courseId;
        this.courseCode = courseCode;
        this.color = color;
    }

    public int getCourseId() {return courseId;}
    public void setCourseId(int courseId) {this.courseId = courseId;}

    public String getCourseCode() {return courseCode;}
    public void setCourseCode(String courseCode) {this.courseCode = courseCode;}

    public String getColor() {return color;}
    public void setColor(String color) {this.color = color;}
}
