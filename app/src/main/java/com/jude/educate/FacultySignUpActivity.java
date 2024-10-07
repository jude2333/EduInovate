package com.jude.educate;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.jude.educate.Model.Faculty;
import com.jude.educate.Repository.CourseManager;
import com.jude.educate.databinding.ActivityFacultySignUpBinding;

public class FacultySignUpActivity extends AppCompatActivity {

    private ActivityFacultySignUpBinding binding;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentUser;

    private Faculty faculty;
    private CourseManager courseManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFacultySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();

        // initializing the authStateListener
        setUpAuthStateListener();

        binding.signUpButton.setOnClickListener(v -> handleSignUp());
    }

    private void setUpAuthStateListener() {
        authStateListener = firebaseAuth -> {
            currentUser = firebaseAuth.getCurrentUser();
            if (currentUser != null) {
                // user is signed in
            } else {
                // user is signed out
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (authStateListener != null) {
            firebaseAuth.addAuthStateListener(authStateListener);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (authStateListener != null) {
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
    }

    private void handleSignUp() {
        String email = binding.email.getText().toString().trim();
        String password = binding.password.getText().toString().trim();
        String username = binding.username.getText().toString().trim();
        String major = binding.major.getText().toString().trim();
        String age = binding.age.getText().toString().trim();
        String register = binding.registerNumber.getText().toString().trim();
        String courseName = binding.course.getText().toString().trim();

        if (areFieldsValid(email, password, username)) {
            createUserEmailAccount(email, password, username, major, age, register, courseName);
        } else {
            Toast.makeText(this, "No Empty Fields are Allowed!", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean areFieldsValid(String email, String password, String username) {
        return !TextUtils.isEmpty(email) && !TextUtils.isEmpty(password) && !TextUtils.isEmpty(username);
    }

    private void createUserEmailAccount(String email, String password, String username, String major, String age, String register, String courseName) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        onAccountCreated(email, password, username, major, age, register, courseName);
                    } else {
                        Toast.makeText(FacultySignUpActivity.this, "Account creation failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void onAccountCreated(String email, String password, String username, String major, String age, String register, String courseName) {
        currentUser = firebaseAuth.getCurrentUser();
        String userId = currentUser.getUid();

        // create course and get courseId
        courseManager = new CourseManager();
        String courseId = courseManager.createCourse(courseName, register);

        // create faculty object
//        faculty = new Faculty(username,email, age, password,major, "faculty", register, courseId);

        // save faculty to database
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("users").child(userId).setValue(faculty);


        Toast.makeText(this, "Account created successfully", Toast.LENGTH_SHORT).show();

        // switch to MainActivity after 2 seconds
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            startActivity(new Intent(FacultySignUpActivity.this, FacultyActivity.class));
            finish();
        }, 2000);
    }
}
