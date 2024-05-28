package com.jude.educate.Model;

import java.util.Map;

public class Student {

    private String name;
    private String email;
    private String age;
    private String major;
    private String password;
    private String registerNumber;


    public Student(String name, String email, String age,  String major, String password, String registerNumber) {
        this.name = name;
        this.email = email;
        this.age = age;
        this.major = major;
        this.password = password;
        this.registerNumber = registerNumber;
        
    }
    

//    for firebase empty constructor
    public Student() {
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

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }
    

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRegisterNumber() {
        return registerNumber;
    }

    public void setRegisterNumber(String registerNumber) {
        this.registerNumber = registerNumber;
    }
}
