package com.jude.educate.Cource;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.jude.educate.Model.Course;
import com.jude.educate.R;

public class CourseDetailsActivity extends AppCompatActivity {

    private DatabaseReference courseDatabaseRef;
    private Course course;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_course_details);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainCourseDetailActivity), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Fetch course data from the intent
        course = (Course) getIntent().getSerializableExtra("course");

        // Initialize Firebase reference
        courseDatabaseRef = FirebaseDatabase.getInstance().getReference("courses");

        // Views
        TextView nameTextView = findViewById(R.id.CourseDetailname);
        TextView codeTextView = findViewById(R.id.CourseDetailcode);
        TextView statusTextView = findViewById(R.id.CourseDetailstatus);
        Button approveButton = findViewById(R.id.approveButton);
        Button rejectButton = findViewById(R.id.rejectButton);

        // Set course data to views
        if (course != null) {
            nameTextView.setText("Name: " + course.getCourseName());
            codeTextView.setText("Code: " + course.getCourseId());
            statusTextView.setText("Status: " + course.getStatus());
        }

        // Approve button click listener
        approveButton.setOnClickListener(v -> approveCourse());

        // Reject button click listener (for deletion)
        rejectButton.setOnClickListener(v -> deleteCourse());
    }

    // Method to approve the course
    private void approveCourse() {
        if (course != null) {
            courseDatabaseRef.child(course.getCourseId()).child("status").setValue("approved")
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            course.setStatus("approved"); // Update the course object locally
                            Toast.makeText(CourseDetailsActivity.this, "Course approved successfully", Toast.LENGTH_SHORT).show();

                            // **Return the updated course object back to AdminActivity**
                            Intent resultIntent = new Intent();
                            resultIntent.putExtra("updated_course", course);
                            setResult(RESULT_OK, resultIntent);
                            finish(); // Close the activity
                        } else {
                            Toast.makeText(CourseDetailsActivity.this, "Failed to approve course", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    // Method to delete the course
    private void deleteCourse() {
        if (course != null) {
            courseDatabaseRef.child(course.getCourseId()).removeValue()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(CourseDetailsActivity.this, "Course deleted successfully", Toast.LENGTH_SHORT).show();

                            // **Return null to indicate the course was deleted**
                            Intent resultIntent = new Intent();
                            resultIntent.putExtra("updated_course", (Course) null);
                            setResult(RESULT_OK, resultIntent);
                            finish(); // Close the activity
                        } else {
                            Toast.makeText(CourseDetailsActivity.this, "Failed to delete course", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
}
