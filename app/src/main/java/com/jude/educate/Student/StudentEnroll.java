package com.jude.educate.Student;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.jude.educate.FacultyActivity;
import com.jude.educate.Model.Course;
import com.jude.educate.Model.Faculty;
import com.jude.educate.Model.Student;
import com.jude.educate.StudentDetailsActivity;
import com.jude.educate.databinding.ActivityStudentEnrollBinding;

import java.security.SecureRandom;

public class StudentEnroll extends AppCompatActivity {

    private Button enrollStudent;
    private ActivityStudentEnrollBinding binding;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;

    private DatabaseReference userRef;

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int PASSWORD_LENGTH = 8;
    private static final SecureRandom RANDOM = new SecureRandom();

    String selectedSpinnerCourseId = FacultyActivity.selectedCourse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Binding layout
        binding = ActivityStudentEnrollBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();

        if (currentUser != null) {
            userRef = FirebaseDatabase.getInstance().getReference().child("users").child(currentUser.getUid());
        } else {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
            finish(); // Exit if not authenticated
        }


        enrollStudent = binding.enrollButton;


        enrollStudent.setOnClickListener(v -> {
            String studentName = binding.studentname.getText().toString().trim();
            String studentEmail = binding.studentemail.getText().toString().trim();

            if (studentName.isEmpty() || studentEmail.isEmpty()) {
                Toast.makeText(StudentEnroll.this, "Please enter all required fields", Toast.LENGTH_SHORT).show();
            } else {
                checkCourseStatus(studentName, studentEmail);
            }
        });
    }

    private void checkCourseStatus(String studentName, String studentEmail) {
        if (currentUser != null) {
            String facultyId = currentUser.getUid();

            // query the "courses" node to find the course where the facultyId matches the current user's UID
            DatabaseReference coursesRef = FirebaseDatabase.getInstance().getReference().child("courses");
            Query courseQuery = coursesRef.orderByChild("facultyId").equalTo(facultyId);

            courseQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        for (DataSnapshot courseSnapshot : snapshot.getChildren()) {
                            String courseStatus = courseSnapshot.child("status").getValue(String.class);

                            if ("approved".equalsIgnoreCase(courseStatus)) {
                                // course is approved, proceed with creating the student account
                                createStudentAccount(studentName, studentEmail);
                            } else {

                                Toast.makeText(StudentEnroll.this, "Course is pending approval. Student cannot be enrolled yet.", Toast.LENGTH_SHORT).show();
                            }
                            break;
                        }
                    } else {
                        Toast.makeText(StudentEnroll.this, "No course found for this faculty.", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("FetchCourse", "Error fetching courses: " + error.getMessage());
                }
            });

        } else {
            Toast.makeText(this, "Failed to check course status", Toast.LENGTH_SHORT).show();
        }
    }

    private void createStudentAccount(String studentName, String studentEmail) {
        String password = generatePassword();

        firebaseAuth.createUserWithEmailAndPassword(studentEmail, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser newStudentUser = task.getResult().getUser();
                        String studentUID = newStudentUser.getUid();

                        onAccountCreated(studentName, studentEmail, password, studentUID);

                    } else {
                        Toast.makeText(StudentEnroll.this, "Account creation failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void onAccountCreated(String username, String email, String password, String studentUID) {
        if (currentUser != null) {
            String facultyId = currentUser.getUid();

            DatabaseReference coursesRef = FirebaseDatabase.getInstance().getReference().child("courses");
            Query courseQuery = coursesRef.orderByChild("facultyId").equalTo(facultyId);

            courseQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        for (DataSnapshot courseSnapshot : snapshot.getChildren()) {
                            String courseId = courseSnapshot.getKey();  // Get the course ID

                            String courseStatus = courseSnapshot.child("status").getValue(String.class);

                            if ("approved".equalsIgnoreCase(courseStatus)) {
                                Student student = new Student(username, email, password, "student");

                                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                                databaseReference.child("users").child(studentUID).setValue(student)
                                        .addOnCompleteListener(task -> {
                                            if (task.isSuccessful()) {
                                                enrollStudentInCourse(studentUID, courseId);

                                                addStudentToFacultyList(studentUID, facultyId);

                                                Toast.makeText(StudentEnroll.this, "Account created and enrolled successfully", Toast.LENGTH_SHORT).show();

                                                // Redirect to the student details activity
                                                Intent intent = new Intent(StudentEnroll.this, StudentDetailsActivity.class);
                                                intent.putExtra("student",student);
                                                startActivity(intent);
                                            } else {
                                                Toast.makeText(StudentEnroll.this, "Failed to save student to database", Toast.LENGTH_SHORT).show();
                                            }
                                        });

                            } else {
                                Toast.makeText(StudentEnroll.this, "Course is pending approval. Student cannot be enrolled yet.", Toast.LENGTH_SHORT).show();
                            }
                            break;
                        }
                    } else {
                        Toast.makeText(StudentEnroll.this, "No course found for this faculty.", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("FetchCourse", "Error fetching courses: " + error.getMessage());
                }
            });

        } else {
            Toast.makeText(this, "Failed to create account", Toast.LENGTH_SHORT).show();
        }
    }

    public static String generatePassword() {
        StringBuilder password = new StringBuilder(PASSWORD_LENGTH);
        for (int i = 0; i < PASSWORD_LENGTH; i++) {
            password.append(CHARACTERS.charAt(RANDOM.nextInt(CHARACTERS.length())));
        }
        return password.toString();
    }

    // Add the student UID to the faculty's student list
    private void addStudentToFacultyList(String studentUID, String facultyId) {
        DatabaseReference facultyRef = FirebaseDatabase.getInstance().getReference().child("users").child(facultyId);

        facultyRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Faculty faculty = snapshot.getValue(Faculty.class);
                    if (faculty != null) {
                        faculty.addStudent(studentUID);

                        facultyRef.setValue(faculty)
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        Log.d("AddStudentToFaculty", "Student added to Faculty's student list successfully.");
                                    } else {
                                        Log.e("AddStudentToFaculty", "Failed to add student to Faculty's student list.", task.getException());
                                    }
                                });
                    }
                } else {
                    Log.e("AddStudentToFaculty", "Faculty not found.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("AddStudentToFaculty", "Database error: " + error.getMessage());
            }
        });
    }

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
