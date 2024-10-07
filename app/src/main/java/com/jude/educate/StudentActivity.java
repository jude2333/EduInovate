package com.jude.educate;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jude.educate.Assignment.StudentAssignmentActivity;
import com.jude.educate.Model.Assignment;
import com.jude.educate.databinding.ActivityStudentBinding;
import com.jude.educate.login.LoginActivity;

import java.util.ArrayList;
import java.util.List;

public class StudentActivity extends AppCompatActivity {

    ActivityStudentBinding binding;
    public static String selectedCourse;
    Spinner courseSpinner;  // Declare the spinner here but don't initialize it yet

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityStudentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize the spinner after binding is set up
        courseSpinner = binding.courseSpinner;

        Toolbar toolbar = binding.customToolbar;
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Student Dashboard");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        replaceFragment(new StudentActivityFragment());
        binding.bottomNavigationView.setBackground(null);
        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.home) {
                replaceFragment(new StudentActivityFragment());
            } else if (item.getItemId() == R.id.profile) {
                replaceFragment(new ProfileFragment());
            }
            return true;
        });

        String studentUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DatabaseReference studentCourseEnrolledRef = FirebaseDatabase.getInstance().getReference("users")
                .child(studentUid).child("coursesEnrolled");

        studentCourseEnrolledRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<String> courseList = new ArrayList<>();
                long courseCount = snapshot.getChildrenCount(); // Track the number of courses
                if (courseCount == 0) {
                    // No courses available, set up the spinner with an empty list
                    setupSpinner(courseList);
                    return;
                }

                for (DataSnapshot courseSnapshot : snapshot.getChildren()) {
                    String courseId = courseSnapshot.getValue(String.class);
                    if (courseId != null) {
                        courseList.add(courseId);  // Add the course ID directly to the list
                    }
                }

                // Once all course IDs are added, set up the spinner
                setupSpinner(courseList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });

    }

    private void setupSpinner(List<String> courseList) {
        // Populate the spinner with the course IDs
        ArrayAdapter<String> adapter = new ArrayAdapter<>(StudentActivity.this, android.R.layout.simple_spinner_item, courseList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        courseSpinner.setAdapter(adapter);

        // Set the initial selection to the first course and update the static key
        if (!courseList.isEmpty()) {
            courseSpinner.setSelection(0);  // Set to the first course ID
            selectedCourse = courseList.get(0);  // Set the public static key to the first course ID
            Log.d("selectedCourse", "Selected Course: " + selectedCourse);
        }

        // Listen for spinner item selections
        courseSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // Update the public static key when a course is selected
                selectedCourse = courseList.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Handle case where nothing is selected if necessary
            }
        });
    }






        private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu, which adds the "Sign Out" option to the toolbar's overflow menu
        getMenuInflater().inflate(R.menu.top_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        // Handle sign-out action
        if (id == R.id.action_sign_out) {
            FirebaseAuth.getInstance().signOut();

            Intent intent = new Intent(StudentActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void fetchStudentEnrolledCourses(String studentId) {
        DatabaseReference studentRef = FirebaseDatabase.getInstance().getReference("users")
                .child(studentId).child("enrolledCourses");

        studentRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<String> enrolledCourseIds = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Log.d("Course ID", dataSnapshot.getKey());
                    enrolledCourseIds.add(dataSnapshot.getKey()); // Get courseId
                }
                Log.d("Total Courses", "Courses count: " + enrolledCourseIds.size());
                // Now that we have the course IDs, fetch the assignments for those courses
//                fetchAssignmentsForEnrolledCourses(enrolledCourseIds);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle potential errors
                Toast.makeText(StudentActivity.this, "Failed to load enrolled courses", Toast.LENGTH_SHORT).show();
            }
        });
    }

}




