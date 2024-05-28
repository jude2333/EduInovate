package com.jude.educate;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.jude.educate.Model.Student;
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

    private ActivityStudentSignUpAcivityBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize FirebaseAuth
        firebaseAuth = FirebaseAuth.getInstance();

        // Inflating views
        binding = ActivityStudentSignUpAcivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Referring views
        signUpButton = binding.signUpButton;
        email = binding.email;
        password = binding.password;
        major = binding.major;
        registerNumber = binding.registerNumber;
        age = binding.age;
        userName = binding.userName;

        // Setting onClick listener
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String mEmail = email.getText().toString().trim();
                String mUserName = userName.getText().toString().trim();
                String mRegisterNumber = registerNumber.getText().toString().trim();
                String mPassword = password.getText().toString().trim();
                String mMajor = major.getText().toString().trim();
                String mAge = age.getText().toString().trim();

                if (!TextUtils.isEmpty(mEmail) && !TextUtils.isEmpty(mPassword)
                        && !TextUtils.isEmpty(mMajor) && !TextUtils.isEmpty(mAge)
                        && !TextUtils.isEmpty(mUserName) && !TextUtils.isEmpty(mRegisterNumber)) {

                    // For account creation
                    create_account(mEmail, mPassword, mUserName, mAge, mMajor, mRegisterNumber);
                } else {
                    Toast.makeText(StudentSignUpActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void create_account(String email, String password, String userName, String age, String major, String registerNo) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(StudentSignUpActivity.this, "Account Created!", Toast.LENGTH_SHORT).show();

                            // Save user to the Realtime Database
                            save_user(userName, email, age, major, password, registerNo);

                            // Start MainActivity
                            Intent i = new Intent(StudentSignUpActivity.this, MainActivity.class);
                            startActivity(i);
                            finish();
                        } else {
                            Toast.makeText(StudentSignUpActivity.this, "Account Creation Failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void save_user(String name, String email, String age, String major, String password, String registerNo) {
        // Student class
        Student student = new Student(name, email, age, major, password, registerNo);

        // Firebase
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("Students");

        db.child(registerNo).setValue(student).addOnCompleteListener(new OnCompleteListener<Void>() {
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
}
