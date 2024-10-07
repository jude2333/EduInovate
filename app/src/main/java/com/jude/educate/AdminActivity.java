package com.jude.educate;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.jude.educate.Adapter.PendingCourseListAdapter;
import com.jude.educate.Cource.CourseDetailsActivity;
import com.jude.educate.Model.Course;
import com.jude.educate.Model.Faculty;
import com.jude.educate.databinding.ActivityAdminBinding;
import com.jude.educate.login.LoginActivity;

import java.security.SecureRandom;
import java.util.ArrayList;

public class AdminActivity extends AppCompatActivity {

    private Button addFaculty;
    private ActivityAdminBinding binding;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;

    private DatabaseReference userRef;

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int PASSWORD_LENGTH = 8;
    private static final SecureRandom RANDOM = new SecureRandom();

    // for pending course list
    private RecyclerView pendingCoursesRecyclerView;
    private PendingCourseListAdapter pendingCourseListAdapter;
    private ArrayList<Course> pendingCourseList;
    private DatabaseReference coursesDatabase;

    private final ActivityResultLauncher<Intent> courseDetailsLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Course updatedCourse = (Course) result.getData().getSerializableExtra("updated_course");

                    if (updatedCourse != null) {
                        updateCourseList(updatedCourse);
                    } else {
                        Toast.makeText(AdminActivity.this, "Course was deleted.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityAdminBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();

        if (currentUser != null) {
            userRef = FirebaseDatabase.getInstance().getReference().child("users").child(currentUser.getUid());
        } else {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
            finish();
        }

        addFaculty = binding.buttonCreateFaculty;

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        addFaculty.setOnClickListener(v -> {
            String facultyName = binding.editTextFacultyName.getText().toString().trim();
            String facultyEmail = binding.editTextFacultyEmail.getText().toString().trim();

            if (facultyName.isEmpty() || facultyEmail.isEmpty()) {
                Toast.makeText(AdminActivity.this, "Please enter all required fields", Toast.LENGTH_SHORT).show();
            } else {
                createFacultyAccount(facultyName, facultyEmail);
            }
        });

        Toolbar toolbar = binding.customToolbar;
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Admin Dashboard");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);  // Enable back button if needed
        }

        // Initialize RecyclerView and Firebase reference
        pendingCoursesRecyclerView = findViewById(R.id.pendingCourses);
        pendingCoursesRecyclerView.setHasFixedSize(true);
        pendingCoursesRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        coursesDatabase = FirebaseDatabase.getInstance().getReference("courses");
        pendingCourseList = new ArrayList<>();
        pendingCourseListAdapter = new PendingCourseListAdapter(this, pendingCourseList);

        // click listener for the RecyclerView items
        pendingCourseListAdapter.setOnItemClickListener(position -> {
            Course clickedCourse = pendingCourseList.get(position);
//             a new activity with course details
            Intent intent = new Intent(AdminActivity.this, CourseDetailsActivity.class);
            intent.putExtra("course", clickedCourse);
            startActivity(intent);
            courseDetailsLauncher.launch(intent);
        });

        // adapter to RecyclerView
        pendingCoursesRecyclerView.setAdapter(pendingCourseListAdapter);

        fetchPendingCourses();
    }

    private void createFacultyAccount(String facultyName, String facultyEmail) {
        String password = generatePassword(); // Generate a password for the new account

        firebaseAuth.createUserWithEmailAndPassword(facultyEmail, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Getting faculty UID
                        FirebaseUser newFacultyUser = task.getResult().getUser();
                        String facultyUID = newFacultyUser.getUid();

                        // Store the details and move to FacultyOverview activity
                        onAccountCreated(facultyName, facultyEmail, password, facultyUID);

                    } else {
                        Toast.makeText(AdminActivity.this, "Account creation failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void onAccountCreated(String username, String email, String password, String facultyUID) {
        if (currentUser != null) {
            Faculty faculty = new Faculty(username, email, password, "faculty");

            // Save faculty to database
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
            databaseReference.child("users").child(facultyUID).setValue(faculty)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(this, "Account created successfully", Toast.LENGTH_SHORT).show();

                            Faculty faculty1 = new Faculty(username, email, password, "faculty");
                            Intent intent = new Intent(AdminActivity.this, FacultyOverview.class);
                            intent.putExtra("faculty", faculty1);
                            startActivity(intent);
                        } else {
                            Toast.makeText(this, "Failed to save faculty to database", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(this, "Failed to create account", Toast.LENGTH_SHORT).show();
        }
    }

        private void fetchPendingCourses() {
            // Query to get courses where the status is "pending"
            Query pendingCoursesQuery = coursesDatabase.orderByChild("status").equalTo("pending");

            pendingCoursesQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    pendingCourseList.clear();  // Clear the list before adding new data
                    if (snapshot.exists()) {
                        for (DataSnapshot courseSnapshot : snapshot.getChildren()) {
                            Course course = courseSnapshot.getValue(Course.class);
                            if (course != null) {
                                pendingCourseList.add(course);
                            }
                        }
                        pendingCourseListAdapter.notifyDataSetChanged();  // Notify adapter of data change
                    } else {
                        Toast.makeText(AdminActivity.this, "No pending courses found.", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("AdminActivity", "Error fetching pending courses: " + error.getMessage());
                }
            });

    }
    private void updateCourseList(Course updatedCourse) {
        // Update the course list based on the returned result
        for (int i = 0; i < pendingCourseList.size(); i++) {
            if (pendingCourseList.get(i).getCourseId().equals(updatedCourse.getCourseId())) {
                if ("approved".equals(updatedCourse.getStatus())) {
                    pendingCourseList.remove(i); // Remove approved courses
                } else {
                    pendingCourseList.set(i, updatedCourse); // Update any other changes
                }
                break;
            }
        }
        pendingCourseListAdapter.notifyDataSetChanged(); // Notify adapter of data change
    }


    public static String generatePassword() {
        StringBuilder password = new StringBuilder(PASSWORD_LENGTH);
        for (int i = 0; i < PASSWORD_LENGTH; i++) {
            password.append(CHARACTERS.charAt(RANDOM.nextInt(CHARACTERS.length())));
        }
        return password.toString();
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

            Intent intent = new Intent(AdminActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
