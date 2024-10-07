package com.jude.educate.Repository;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jude.educate.Model.Course;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CourseManager {

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int COURSE_ID_LENGTH = 5;
    private static final SecureRandom RANDOM = new SecureRandom();

    // mthd to generate a 5-character long course ID
    private String generateCourseId() {
        StringBuilder courseId = new StringBuilder(COURSE_ID_LENGTH);
        for (int i = 0; i < COURSE_ID_LENGTH; i++) {
            courseId.append(CHARACTERS.charAt(RANDOM.nextInt(CHARACTERS.length())));
        }
        return courseId.toString();
    }

    // method to create a course using the Course model
    public String createCourse(String courseName, String facultyId) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        String courseId = generateCourseId();

        // Create a new Course object
        Course course = new Course(courseId, courseName, facultyId,"pending");

        // Save the course object to Firebase
        databaseReference.child("courses").child(courseId).setValue(course)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("CreateCourse", "Course created successfully with ID: " + courseId);
                    } else {
                        Log.e("CreateCourse", "Failed to create course.", task.getException());
                    }
                });


        return courseId;
    }


    // method to enroll a student in a course using the Course model
    public void enrollStudentInCourse(String uid,String courseId) {
        DatabaseReference courseRef = FirebaseDatabase.getInstance().getReference().child("courses").child(courseId);

        courseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Course course = snapshot.getValue(Course.class);
                    if (course != null) {
                        course.addStudent(uid);

                        courseRef.setValue(course)
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        Log.d("EnrollStudent", "Student enrolled in course successfully.");
                                        // Call the method to add student to faculty list
//                                        addStudentToFacultyList(studentId, studentName, courseId);
                                    } else {
                                        Log.e("EnrollStudent", "Failed to enroll student in course.", task.getException());
                                    }
                                });
                    }
                } else {
                    Log.e("EnrollStudent", "Course does not exist.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("EnrollStudent", "Error checking if course exists: " + error.getMessage());
            }
        });
    }

}

