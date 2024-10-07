package com.jude.educate.Model;

import java.io.Serializable;
import java.util.Map;

public class Poll implements Serializable {

    private String pollId;
    private String question;
    private Map<String, String> options; // Key as optionId and value as option text
    private int option1Votes;
    private int option2Votes;
    private String facultyId;
    private String courseId;
    private int totalVotes;
    private Map<String, PollSubmission> submissions; // Key as studentId and value as Submission object;
    private String date;

    // Default constructor required for calls to DataSnapshot.getValue(Poll.class)
    public Poll() {
    }



    // Constructor
    public Poll(String pollId, String question, Map<String, String> options, String facultyId, String courseId,String date) {
        this.pollId = pollId;
        this.question = question;
        this.options = options;
        this.option1Votes = 0; // Initial votes set to 0
        this.option2Votes = 0;
        this.facultyId = facultyId;
        this.courseId = courseId;
        this.totalVotes = 0; // Initial total votes set to 0
        this.date = date;
    }

    // Getters and Setters
    public String getPollId() {
        return pollId;
    }

    public void setPollId(String pollId) {
        this.pollId = pollId;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public Map<String, String> getOptions() {
        return options;
    }

    public void setOptions(Map<String, String> options) {
        this.options = options;
    }

    public int getOption1Votes() {
        return option1Votes;
    }

    public void setOption1Votes(int option1Votes) {
        this.option1Votes = option1Votes;
    }

    public int getOption2Votes() {
        return option2Votes;
    }

    public void setOption2Votes(int option2Votes) {
        this.option2Votes = option2Votes;
    }

    public String getFacultyId() {
        return facultyId;
    }

    public void setFacultyId(String facultyId) {
        this.facultyId = facultyId;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public int getTotalVotes() {
        return totalVotes;
    }

    public void setTotalVotes(int totalVotes) {
        this.totalVotes = totalVotes;
    }

    public Map<String, PollSubmission> getSubmissions() {
        return submissions;
    }

    public void setSubmissions(Map<String, PollSubmission> submissions) {
        this.submissions = submissions;
    }

    // Method to update the votes
    public void addVote(String option) {
        if (option.equals("option1")) {
            this.option1Votes++;
        } else if (option.equals("option2")) {
            this.option2Votes++;
        }
        this.totalVotes++;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
