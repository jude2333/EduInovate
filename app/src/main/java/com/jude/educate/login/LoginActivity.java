package com.jude.educate.login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.util.Patterns;
import android.view.MotionEvent;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jude.educate.AdminActivity;
import com.jude.educate.FacultyActivity;
import com.jude.educate.R;
import com.jude.educate.StudentActivity;

import java.lang.ref.WeakReference;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    private AppCompatButton signUpButton, loginBtn;
    private EditText emailEt, passEt;
    private boolean isPasswordVisible = false;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference usersRef;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initFirebase();
        initUI();
        setupListeners();
    }

    private void initFirebase() {
        firebaseAuth = FirebaseAuth.getInstance();
        usersRef = FirebaseDatabase.getInstance().getReference("users");
    }

    private void initUI() {
        signUpButton = findViewById(R.id.signUpButton);
        loginBtn = findViewById(R.id.loginButton);
        emailEt = findViewById(R.id.emailET);
        passEt = findViewById(R.id.passwordET);
    }

    private void setupListeners() {
        signUpButton.setOnClickListener(view -> {
            Intent intent = new Intent(LoginActivity.this, AdminSignUp.class);
            startActivity(intent);
        });

        loginBtn.setOnClickListener(v -> {
            String email = emailEt.getText().toString().trim();
            String pass = passEt.getText().toString().trim();
            loginEmailPassUser(email, pass);
        });

        passEt.setOnTouchListener((v, event) -> {
            final int DRAWABLE_RIGHT = 2;
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (event.getRawX() >= (passEt.getRight() - passEt.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                    togglePasswordVisibility();
                    return true;
                }
            }
            return false;
        });
    }

    private void togglePasswordVisibility() {
        if (isPasswordVisible) {
            passEt.setTransformationMethod(PasswordTransformationMethod.getInstance());
            passEt.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icon_openeye, 0);
        } else {
            passEt.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            passEt.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icon_closeeye, 0);
        }
        isPasswordVisible = !isPasswordVisible;
        passEt.setSelection(passEt.getText().length());
    }

    private void loginEmailPassUser(String email, String pwd) {
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(pwd)) {
            Toast.makeText(this, "Email and Password cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_SHORT).show();
            return;
        }

        if (pwd.length() < 6) {
            Toast.makeText(this, "Password must be at least 6 characters long", Toast.LENGTH_SHORT).show();
            return;
        }

        showProgressDialog("Logging in...");

        firebaseAuth.signInWithEmailAndPassword(email, pwd)
                .addOnCompleteListener(this, task -> {
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    if (task.isSuccessful()) {
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        if (user != null) {
                            fetchUserRoleAndRedirect(user.getUid());
                        } else {
                            Log.e(TAG, "User is null after successful login");
                            showToast("Login failed. Please try again.");
                        }
                    } else {
                        Log.e(TAG, "signInWithEmail:failure", task.getException());
                        String errorMessage = "Authentication failed";
                        if (task.getException() != null) {
                            errorMessage += ": " + task.getException().getMessage();
                        }
                        showToast(errorMessage);
                    }
                });
    }

    private void fetchUserRoleAndRedirect(String uid) {
        Log.d(TAG, "Fetching user role for UID: " + uid);
        usersRef.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String role = dataSnapshot.child("role").getValue(String.class);
                    Log.d(TAG, "User role fetched: " + role);
                    if (role != null) {
                        redirectToActivityBasedOnRole(role);
                    } else {
                        Log.e(TAG, "Role is null for UID: " + uid);
                        showToast("User role not found. Please contact support.");
                    }
                } else {
                    Log.e(TAG, "User data not found for UID: " + uid);
                    showToast("User data not found. Please contact support.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "fetchUserRoleAndRedirect:onCancelled", databaseError.toException());
                showToast("Failed to retrieve user data. Please try again.");
            }
        });
    }

    private void redirectToActivityBasedOnRole(String role) {
        Log.d(TAG, "Redirecting based on role: " + role);
        if (role == null) {
            showToast("No role assigned to this user.");
            return;
        }

        Intent intent;
        switch (role.toLowerCase()) {
            case "admin":
                intent = new Intent(LoginActivity.this, AdminActivity.class);
                break;
            case "faculty":
                intent = new Intent(LoginActivity.this, FacultyActivity.class);
                break;
            case "student":
                intent = new Intent(LoginActivity.this, StudentActivity.class);
                break;
            default:
                Log.e(TAG, "Unknown role: " + role);
                showToast("Unknown role: " + role);
                return;
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void showProgressDialog(String message) {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage(message);
            progressDialog.setCancelable(false);
        }
        progressDialog.show();
    }

    private void showToast(String message) {
        WeakReference<LoginActivity> activityRef = new WeakReference<>(LoginActivity.this);
        LoginActivity activity = activityRef.get();
        if (activity != null && !activity.isFinishing()) {
            Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
        }
    }
}
