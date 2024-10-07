package com.jude.educate.Model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Course implements Serializable {
    private String courseId;
    private String courseName;
    private String facultyId;
    private String status;
    private List<String> students;


    // default constructor required for calls to DataSnapshot.getValue(Course.class)
    public Course() {
        this.students = new ArrayList<>();

    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    // Constructor with parameters
    public Course(String courseId, String courseName, String facultyId,String status) {
        this.courseId = courseId;
        this.courseName = courseName;
        this.facultyId = facultyId;
        this.status = status;
        this.students = new ArrayList<>();

    }

    // Add student to the course
    public void addStudent(String uid) {
        // Ensure the students map is initialized
        if (this.students == null) {
            this.students = new ArrayList<>();
        }
        if(!this.students.contains(uid)){
            this.students.add(uid);
        }

    }

    // Getter and setter for students
    public List<String> getStudents() {
        return students;
    }

    public void setStudents(List<String> students) {
        this.students = students;
    }

    // Getters and Setters
    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getFacultyId() {
        return facultyId;
    }

    public void setFacultyId(String facultyId) {
        this.facultyId = facultyId;
    }


}
