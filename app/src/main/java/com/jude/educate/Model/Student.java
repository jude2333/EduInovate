package com.jude.educate.Model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Student implements Serializable {

    private String name;
    private String email;

    private String password;
    private String role;

    private List<String> coursesEnrolled;

    public Student() {
    }


    public Student(List<String> coursesEnrolled) {
        this.coursesEnrolled = new ArrayList<String>();
    }

    public Student(String name, String email, String password, String role) {
        this.name = name;
        this.email = email;

        this.password = password;
        this.role = role;
        this.coursesEnrolled = new ArrayList<String>();

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


    public List<String> getCoursesEnrolled() {
        return coursesEnrolled;
    }

    public void setCoursesEnrolled(List<String> coursesEnrolled) {
        this.coursesEnrolled = coursesEnrolled;
    }

    public void addCourse(String uid) {
        if(this.coursesEnrolled == null){
            this.coursesEnrolled= new ArrayList<>();
        }

        if(!this.coursesEnrolled.contains(uid)){
            this.coursesEnrolled.add(uid);
        }

    }


}
