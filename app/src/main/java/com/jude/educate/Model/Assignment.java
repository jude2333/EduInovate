package com.jude.educate.Model;

import java.io.Serializable;

public class Assignment implements Serializable {
    private String title;
    private String description;
    private int maxMarks;
    private String facultyID;
    private String submittedTime;
    private String pdfLink;

    private String courseId;
    private String assignmentID;

    // Empty constructor required for Firebase
    public Assignment() {
    }



    public Assignment(String title, String description, int maxMarks, String facultyID, String submittedTime, String pdfLink, String courseId, String assignmentID) {
        this.title = title;
        this.description = description;
        this.maxMarks = maxMarks;
        this.facultyID = facultyID;
        this.submittedTime = submittedTime;
        this.pdfLink = pdfLink;
        this.courseId = courseId;
        this.assignmentID = assignmentID;
    }

    // Getters and Setters
    public String getTitle() {
        return title;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getMaxMarks() {
        return maxMarks;
    }

    public void setMaxMarks(int maxMarks) {
        this.maxMarks = maxMarks;
    }

    public String getfacultyID() {
        return facultyID;
    }

    public void setfacultyID(String facultyID) {
        this.facultyID = facultyID;
    }

    public String getsubmittedTime() {
        return submittedTime;
    }

    public void setsubmittedTime(String submittedTime) {
        this.submittedTime = submittedTime;
    }

    public String getPdfLink() {
        return pdfLink;
    }

    public void setPdfLink(String pdfLink) {
        this.pdfLink = pdfLink;
    }
    public String getAssignmentID() {
        return assignmentID;
    }

    public void setAssignmentID(String assignmentID) {
        this.assignmentID = assignmentID;
    }
}
