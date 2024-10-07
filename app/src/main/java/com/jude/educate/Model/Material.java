package com.jude.educate.Model;


import java.io.Serializable;

public class Material implements Serializable {
    private String title;
    private String description;
    private String facultyID;
    private String submittedTime;
    private String pdfLink;

    private String courseId;
    private String materialId;

    public Material(String title, String description, String facultyID, String submittedTime, String pdfLink, String courseId, String materialId) {
        this.title = title;
        this.description = description;
        this.facultyID = facultyID;
        this.submittedTime = submittedTime;
        this.pdfLink = pdfLink;
        this.courseId = courseId;
        this.materialId = materialId;
    }

    // Empty constructor required for Firebase
    public Material() {
    }

    public String getTitle() {
        return title;
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

    public String getFacultyID() {
        return facultyID;
    }

    public void setFacultyID(String facultyID) {
        this.facultyID = facultyID;
    }

    public String getSubmittedTime() {
        return submittedTime;
    }

    public void setSubmittedTime(String submittedTime) {
        this.submittedTime = submittedTime;
    }

    public String getPdfLink() {
        return pdfLink;
    }

    public void setPdfLink(String pdfLink) {
        this.pdfLink = pdfLink;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getMaterialId() {
        return materialId;
    }

    public void setMaterialId(String materialId) {
        this.materialId = materialId;
    }
}
