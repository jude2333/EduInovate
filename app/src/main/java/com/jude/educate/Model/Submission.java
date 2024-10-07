package com.jude.educate.Model;

import java.io.Serializable;

public class Submission implements Serializable {

    private String studentName;
    private String studentUid;
    private String submissionDate;
    private Integer score;
    private String attachmentUrl;

    // Empty constructor required for Firebase
    public Submission() {}

    // Parameterized constructor
    public Submission(String submissionDate, Integer score, String attachmentUrl, String studentName, String studentUid) {
        this.submissionDate = submissionDate;
        this.score = score;  // No need to set this to null explicitly.
        this.attachmentUrl = attachmentUrl;
        this.studentName = studentName;
        this.studentUid = studentUid;
    }

    // Getters and Setters
    public String getSubmissionDate() {
        return submissionDate;
    }

    public void setSubmissionDate(String submissionDate) {
        this.submissionDate = submissionDate;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public String getAttachmentUrl() {
        return attachmentUrl;
    }

    public void setAttachmentUrl(String attachmentUrl) {
        this.attachmentUrl = attachmentUrl;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getStudentUid() {
        return studentUid;
    }

    public void setStudentUid(String studentUid) {
        this.studentUid = studentUid;
    }
}
