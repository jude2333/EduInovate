package com.jude.educate.login;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.jude.educate.AdminActivity;
import com.jude.educate.Model.Admin;
import com.jude.educate.databinding.ActivityAdminSignUpBinding;

public class AdminSignUp extends AppCompatActivity {

    private ActivityAdminSignUpBinding binding;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;

    private Admin admin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdminSignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance();

        // Set up button click listener
        binding.signUpButton.setOnClickListener(v -> handleSignUp());
    }

    private void handleSignUp() {
        String email = binding.email.getText().toString().trim();
        String password = binding.password.getText().toString().trim();
        String username = binding.username.getText().toString().trim();

        if (areFieldsValid(email, password, username)) {
            createUserEmailAccount(email, password, username);
        } else {
            Toast.makeText(this, "No Empty Fields are Allowed!", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean areFieldsValid(String email, String password, String username) {
        return !TextUtils.isEmpty(email) && !TextUtils.isEmpty(password) && !TextUtils.isEmpty(username);
    }

    private void createUserEmailAccount(String email, String password, String username) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        currentUser = firebaseAuth.getCurrentUser();
                        if (currentUser != null) {
                            onAccountCreated(email, password, username);
                        } else {
                            Toast.makeText(AdminSignUp.this, "Account created, but user is not signed in.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        // Log the exception for debugging
                        Log.e("AdminSignUp", "Account creation failed", task.getException());
                        Toast.makeText(AdminSignUp.this, "Account creation failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void onAccountCreated(String email, String password, String username) {
        String userId = currentUser.getUid();
        admin = new Admin(username, email, password, "Admin");

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("users").child(userId).setValue(admin)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(AdminSignUp.this, "Admin account created successfully.", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(AdminSignUp.this, AdminActivity.class));
                        finish();
                    } else {
                        Log.e("AdminSignUp", "Failed to save admin to database", task.getException());
                        Toast.makeText(AdminSignUp.this, "Failed to save admin to database.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

}
