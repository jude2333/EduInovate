package com.jude.educate;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jude.educate.databinding.ActivityMainBinding;
import com.jude.educate.login.LoginActivity;

import java.util.ArrayList;
import java.util.List;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

public class FacultyActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    public static String selectedCourse; // This will hold the currently selected course name

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Set up the toolbar with Spinner
        Toolbar toolbar = binding.customToolbar;
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Faculty Portal");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }


        Spinner courseSpinner = binding.courseSpinner;

        replaceFragment(new MainActivityFragment());
        binding.bottomNavigationView.setBackground(null);
        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.home) {
                replaceFragment(new MainActivityFragment());
            } else if (item.getItemId() == R.id.profile) {
                replaceFragment(new ProfileFragment());
            }
            return true;
        });

        binding.fab.setOnClickListener(v -> {
            replaceFragment(new StudentListFragment());
        });

        String facultyId = FirebaseAuth.getInstance().getCurrentUser().getUid();

//      ftch courses and populate spinner

        DatabaseReference facultyCoursesRef = FirebaseDatabase.getInstance().getReference("users")
                .child(facultyId).child("coursesCreated");
        facultyCoursesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<String> courseList = new ArrayList<>();
                long courseCount = snapshot.getChildrenCount(); // Track the number of courses to fetch
                if (courseCount == 0) {
                    // No courses available, set up the spinner with an empty list
                    setupSpinner(courseList);
                    return;
                }

                for (DataSnapshot courseSnapshot : snapshot.getChildren()) {
                    String courseId = courseSnapshot.getValue(String.class);

                    DatabaseReference courseRef = FirebaseDatabase.getInstance().getReference("courses").child(courseId);
                    courseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot courseData) {
                            String status = courseData.child("status").getValue(String.class);
                            if ("approved".equals(status)) {
                                String courseCode = courseData.child("courseId").getValue(String.class);
                                courseList.add(courseCode);  // Add the course code instead of the name
                            }

                            // When all courses are processed, set up the spinner
                            if (courseList.size() == courseCount) {
                                setupSpinner(courseList); // Set up spinner after all courses are fetched
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            // Handle error
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });

    }

    private void setupSpinner(List<String> courseList) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(FacultyActivity.this, android.R.layout.simple_spinner_item, courseList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.courseSpinner.setAdapter(adapter);

        // set the initial selection to the first course and update the static key
        if (!courseList.isEmpty()) {
            binding.courseSpinner.setSelection(0);  // Set to first course code
            selectedCourse = courseList.get(0);  // Set the public static key to the first course code
            Log.d("selectedCourse", "Selected Course: " + selectedCourse);
        }

        // listn for spinner item selections
        binding.courseSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // Update the public static key when a course is selected
                selectedCourse = courseList.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // hndle case where nothing is selected if necessary
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
        getMenuInflater().inflate(R.menu.top_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        // Handle sign-out action
        if (id == R.id.action_sign_out) {
            FirebaseAuth.getInstance().signOut();

            Intent intent = new Intent(FacultyActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}

