package com.jude.educate;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.jude.educate.Model.Faculty;
import com.jude.educate.Model.Student;
import com.jude.educate.Repository.CourseManager;
import com.jude.educate.databinding.ActivityStudentSignUpAcivityBinding;

public class StudentSignUpActivity extends AppCompatActivity {

    // FirebaseAuth
    private FirebaseAuth firebaseAuth;

    private AppCompatButton signUpButton;
    private EditText email;
    private EditText password;
    private EditText age;
    private EditText major;
    private EditText userName;
    private EditText registerNumber;
    private EditText courseCode;

    private ActivityStudentSignUpAcivityBinding binding;

    // COURSEManager class
    CourseManager courseManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // initialize FirebaseAuth
        firebaseAuth = FirebaseAuth.getInstance();

        // inflating views
        binding = ActivityStudentSignUpAcivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // referring views
        signUpButton = binding.signUpButton;
        email = binding.email;
        password = binding.password;
        major = binding.major;
        registerNumber = binding.registerNumber;
        age = binding.age;
        userName = binding.userName;
        courseCode = binding.courseCode;

        // setting onClick listener
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String mEmail = email.getText().toString().trim();
                String mUserName = userName.getText().toString().trim();
                String mRegisterNumber = registerNumber.getText().toString().trim();
                String mPassword = password.getText().toString().trim();
                String mMajor = major.getText().toString().trim();
                String mAge = age.getText().toString().trim();
                String mCourseCode = courseCode.getText().toString().trim();

                if (!TextUtils.isEmpty(mEmail) && !TextUtils.isEmpty(mPassword)
                        && !TextUtils.isEmpty(mMajor) && !TextUtils.isEmpty(mAge)
                        && !TextUtils.isEmpty(mUserName) && !TextUtils.isEmpty(mRegisterNumber)
                        && !TextUtils.isEmpty(mCourseCode)) {

                    // for account creation
                    create_account(mEmail, mPassword, mUserName, mRegisterNumber,mCourseCode);
                } else {
                    Toast.makeText(StudentSignUpActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void create_account(String email, String password, String userName, String registerNo, String courseCode) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(StudentSignUpActivity.this, "Account Created!", Toast.LENGTH_SHORT).show();

                            // getting current user uid
                            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

                            // Save user to the Realtime Database
                            save_user(userName, email,password, courseCode);

                            // creating course
                            courseManager = new CourseManager();
                            courseManager.enrollStudentInCourse(uid,courseCode);

                            // adding student to faculty list
                            addStudentToFacultyList(registerNo,userName,courseCode);

                            // Start MainActivity
                            Intent i = new Intent(StudentSignUpActivity.this, FacultyActivity.class);
                            startActivity(i);
                            finish();
                        } else {
                            Toast.makeText(StudentSignUpActivity.this, "Account Creation Failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void save_user(String name, String email,String password, String courseID) {
        // Student class
        Student student = new Student(name, email, password,"student");

        // getting current user uid
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Firebase
        DatabaseReference db = FirebaseDatabase.getInstance().getReference();


        db.child("users").child(uid).setValue(student).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(StudentSignUpActivity.this, "User saved in database", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(StudentSignUpActivity.this, "Failed to save user: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void addStudentToFacultyList(String studentId, String studentName, String courseId) {
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("users");
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        Query facultyQuery = usersRef.orderByChild("courseId").equalTo(courseId);

        facultyQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot facultySnapshot : snapshot.getChildren()) {
                        Faculty faculty = facultySnapshot.getValue(Faculty.class);
                        if (faculty != null) {
                            faculty.addStudent(uid);

                            usersRef.child(facultySnapshot.getKey()).setValue(faculty)
                                    .addOnCompleteListener(task -> {
                                        if (task.isSuccessful()) {
                                            Log.d("AddStudentToFaculty", "Student added to Faculty's student list successfully.");
                                        } else {
                                            Log.e("AddStudentToFaculty", "Failed to add student to Faculty's student list.", task.getException());
                                        }
                                    });
                        }
                    }
                } else {
                    Log.e("AddStudentToFaculty", "No faculty found with the specified course ID.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("AddStudentToFaculty", "Database error: " + error.getMessage());
            }
        });
    }


}
