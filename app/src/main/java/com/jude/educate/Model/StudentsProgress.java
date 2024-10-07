package com.jude.educate.Model;

import java.io.Serializable;

public class StudentsProgress implements Serializable {
    private String studentName;
    private int progress;



    public StudentsProgress(){

    }
    public StudentsProgress(String studentName, int progress) {
        this.studentName = studentName;
        this.progress = progress;
    }

    public String getStudentName() {
        return studentName;
    }

    public int getProgress() {
        return progress;
    }
}
