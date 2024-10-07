package com.jude.educate.Assignment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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
import com.google.firebase.database.ValueEventListener;
import com.jude.educate.Adapter.AssignmentsAdapter;
import com.jude.educate.Model.Assignment;
import com.jude.educate.R;
import com.jude.educate.StudentActivity;

import java.util.ArrayList;
import java.util.List;
public class StudentAssignmentActivity extends AppCompatActivity {

    private List<Assignment> assignmentList = new ArrayList<>();
    private AssignmentsAdapter assignmentsAdapter;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_student_assignment);

        // Apply window insets to avoid UI overlap with system bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize RecyclerView and Adapter
        recyclerView = findViewById(R.id.AssignmentstudentList);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        assignmentsAdapter = new AssignmentsAdapter(this, assignmentList);
        recyclerView.setAdapter(assignmentsAdapter);

        // Click listener for RecyclerView items
        assignmentsAdapter.setOnItemClickListener(position -> {
            Assignment clickedAssignment = assignmentList.get(position);
            Intent intent = new Intent(StudentAssignmentActivity.this, StudentAssignmentSubmission.class);
            intent.putExtra("assignment", clickedAssignment);
            startActivity(intent);
            // No need to finish the activity unless you want to remove it from the backstack
        });

        // Fetch assignments for the current selected course
        fetchCurrentCourseAssignments();
    }

    private void fetchCurrentCourseAssignments() {
        DatabaseReference assignmentsRef = FirebaseDatabase.getInstance().getReference("assignments");

        assignmentsRef.orderByChild("courseId").equalTo(StudentActivity.selectedCourse)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        assignmentList.clear(); // Clear previous data

                        for (DataSnapshot assignmentSnapshot : snapshot.getChildren()) {
                            Assignment assignment = assignmentSnapshot.getValue(Assignment.class);
                            if (assignment != null && StudentActivity.selectedCourse != null && assignment.getCourseId().equals(StudentActivity.selectedCourse)) {
                                assignmentList.add(assignment);
                            }
                        }

                        // Notify the adapter to update the RecyclerView
                        assignmentsAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Log and handle errors here
                        Log.e("StudentAssignmentActivity", "Error fetching assignments", error.toException());
                    }
                });
    }
}
