package com.jude.educate.Progress;

import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.jude.educate.R;
import com.jude.educate.StudentActivity;
import com.jude.educate.databinding.ActivityStudentProgressBinding;

public class StudentProgress extends AppCompatActivity {

    ActivityStudentProgressBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityStudentProgressBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        if(StudentActivity.selectedCourse != null){
            String courseId = StudentActivity.selectedCourse;
            String studentId = FirebaseAuth.getInstance().getCurrentUser().getUid();

            calculateStudentProgress(studentId,courseId);

            loadStudentProgress(studentId,courseId);

        }
    }

    // Load student progress for the course
    private void loadStudentProgress(String studentId,String courseId) {
        DatabaseReference studentRef = FirebaseDatabase.getInstance().getReference("users").child(studentId).child("progress").child(courseId);
        studentRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    int progress = dataSnapshot.getValue(Integer.class);
                    updateProgressBar(progress);
                } else {
                    Log.d("Progress", "No progress data found for student.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("DatabaseError", "Failed to load progress", databaseError.toException());
            }
        });
    }

    // Update the progress bar and text
    private void updateProgressBar(int progress) {
        binding.progressStudent.setProgress(progress);
        binding.percentageStudentProgress.setText(progress + "%");
    }

    // Calculate the student's progress based on submissions
    private void calculateStudentProgress(String studentId,String courseId) {
        DatabaseReference assignmentRef = FirebaseDatabase.getInstance().getReference("assignments");
        Query courseAssignmentsQuery = assignmentRef.orderByChild("courseId").equalTo(courseId);

        courseAssignmentsQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int totalAssignments = (int) dataSnapshot.getChildrenCount();
                int submittedAssignments = 0;

                // Loop through each assignment in the course
                for (DataSnapshot assignmentSnapshot : dataSnapshot.getChildren()) {
                    // Check if student has submitted this assignment
                    if (assignmentSnapshot.child("submissions").hasChild(studentId)) {
                        submittedAssignments++;
                    }
                }

                // Check if totalAssignments is greater than 0 to avoid division by zero
                if (totalAssignments > 0) {
                    // Calculate progress as a percentage
                    int progress = (submittedAssignments * 100) / totalAssignments;

                    // Update progress in the database
                    DatabaseReference studentRef = FirebaseDatabase.getInstance().getReference("users").child(studentId);
                    studentRef.child("progress").child(courseId).setValue(progress);

                    // Update the progress bar in UI
                    updateProgressBar(progress);
                } else {
                    Log.d("Progress", "No assignments found for this course.");
                    // Optionally, set progress to 0 if no assignments
                    updateProgressBar(0);
                };
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("DatabaseError", "Failed to calculate progress", databaseError.toException());
            }
        });
    }
}