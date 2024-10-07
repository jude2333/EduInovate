package com.jude.educate.Cource;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jude.educate.FacultyActivity;
import com.jude.educate.Model.Course;
import com.jude.educate.databinding.ActivityCourceCreationBinding;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

public class CourceCreation extends AppCompatActivity {
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int COURSE_ID_LENGTH = 5;
    private static final SecureRandom RANDOM = new SecureRandom();

    FacultyActivity facultyActivity = new FacultyActivity();

    private ActivityCourceCreationBinding binding;
    private FirebaseAuth firebaseAuth;  // FirebaseAuth to get current user

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        binding = ActivityCourceCreationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance();

        // Handling course creation on button click
        binding.createCource.setOnClickListener(v -> {
            String courseName = binding.courseName.getText().toString().trim();

            // Get the current logged-in faculty's UID
            FirebaseUser currentUser = firebaseAuth.getCurrentUser();
            if (currentUser != null) {
                String facultyId = currentUser.getUid();  // Retrieve the faculty's UID
                if (!courseName.isEmpty()) {

                    String courseId = createCourse(courseName, facultyId);

                    Log.d("CourceCreation", "Course created with ID: " + courseId);
                } else {
                    Log.e("CourceCreation", "Course name is empty!");
                }
            } else {
                Log.e("CourceCreation", "No logged-in user!");
            }
        });
    }

    public String createCourse(String courseName, String facultyId) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("courses");
        DatabaseReference facultyReference = FirebaseDatabase.getInstance().getReference("users").child(facultyId);

        String courseId = generateCourseId();

        // Create a new Course object
        Course course = new Course(courseId, courseName, facultyId, "pending");

        // Save the course object to Firebase under the courseId
        databaseReference.child(courseId).setValue(course)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // After successfully creating the course, add the courseId to the faculty node
                        facultyReference.child("coursesCreated").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                List<String> coursesCreated;

                                // If coursesCreated doesn't exist, initialize the list
                                if (snapshot.exists()) {
                                    coursesCreated = (List<String>) snapshot.getValue();
                                } else {
                                    coursesCreated = new ArrayList<>();
                                }

                                // Add the new courseId to the list if it's not already there
                                if (!coursesCreated.contains(courseId)) {
                                    coursesCreated.add(courseId);
                                    facultyReference.child("coursesCreated").setValue(coursesCreated);
                                }

                                // Navigate to FacultyActivity only if course creation and faculty update succeeds
                                Intent intent = new Intent(CourceCreation.this, FacultyActivity.class);
                                startActivity(intent);
                                finish();
                                Toast.makeText(CourceCreation.this, "Course created and added to faculty!", Toast.LENGTH_SHORT).show();
                                Log.d("CreateCourse", "Course created and added to faculty with ID: " + courseId);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Log.e("CreateCourse", "Failed to update faculty node.", error.toException());
                            }
                        });

                    } else {
                        Log.e("CreateCourse", "Failed to create course.", task.getException());
                    }
                });

        return courseId;
    }


    // Method to generate a 5-character long course ID
    private String generateCourseId() {
        StringBuilder courseId = new StringBuilder(COURSE_ID_LENGTH);
        for (int i = 0; i < COURSE_ID_LENGTH; i++) {
            courseId.append(CHARACTERS.charAt(RANDOM.nextInt(CHARACTERS.length())));
        }
        return courseId.toString();
    }
}
