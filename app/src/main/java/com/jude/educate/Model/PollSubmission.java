package com.jude.educate.Model;

import java.util.Date;
import java.util.Map;

public class PollSubmission {
    private String studentId;
    private String option;

    public PollSubmission() {

    }
    public PollSubmission(String studentId, String option) {
        this.studentId = studentId;
        this.option = option;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getOption() {
        return option;
    }

    public void setOption(String option) {
        this.option = option;
    }
}
