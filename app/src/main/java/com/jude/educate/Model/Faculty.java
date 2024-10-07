package com.jude.educate.Model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Faculty implements Serializable{

    private String name;
    private String email;
    private String password;

    private String role;
    private List<String> studentList;
    private List<String> courseCreated;

    // no argument constructor (required by firebase)
    public Faculty() {
        // initialize your fields if necessary
        this.studentList = new ArrayList<String>();

    }

    public Faculty(List<String> courseCreated) {
        this.courseCreated = courseCreated;
    }

    public Faculty(String name, String email, String password, String role) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
        this.studentList = new ArrayList<String>();


    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }



    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }




    // Getter and setter for studentList
    public List<String> getstudentList() {
        return studentList;
    }

    public void setstudentList(List<String> studentList) {
        this.studentList = studentList;
    }

    public List<String> getCourseCreated() {
        return courseCreated;
    }

    public void setCourseCreated(List<String> courseCreated) {
        this.courseCreated = courseCreated;
    }

    // Add student to the course
    public void addStudent(String uid) {
        if(this.studentList == null){
            this.studentList = new ArrayList<>();
        }

        if(!this.studentList.contains(uid)){
            this.studentList.add(uid);
        }

    }
    public void addCourse(String uid) {
        if(this.studentList == null){
            this.studentList = new ArrayList<>();
        }

        if(!this.studentList.contains(uid)){
            this.studentList.add(uid);
        }

    }
}
