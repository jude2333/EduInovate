package com.jude.educate.Assignment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jude.educate.Adapter.AssignmentsAdapter;
import com.jude.educate.FacultyActivity;
import com.jude.educate.Model.Assignment;
import com.jude.educate.R;
import com.jude.educate.databinding.ActivityAssignmentBinding;

import java.util.ArrayList;

public class AssignmentActivity extends AppCompatActivity {

    ActivityAssignmentBinding binding;
    private RecyclerView recyclerView;
    private AssignmentsAdapter assignmentsAdapter;
    private ArrayList<Assignment> assignmentList;

    String selectedCourse = FacultyActivity.selectedCourse;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_assignment);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        binding = ActivityAssignmentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        binding.assignmentCreateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AssignmentActivity.this, AssignmentCreationActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // Initialize the RecyclerView
        recyclerView = findViewById(R.id.assignmentsRecyclerView);

        // Set layout manager
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize the list and adapter
        assignmentList = new ArrayList<>();
        assignmentsAdapter = new AssignmentsAdapter(this, assignmentList);

        // Set the adapter to the RecyclerView
        recyclerView.setAdapter(assignmentsAdapter);

        // Optionally set a click listener
        assignmentsAdapter.setOnItemClickListener(position -> {
            Assignment clickedAssignment = assignmentList.get(position);
            Intent intent = new Intent(AssignmentActivity.this, SubmittedAssignmentStudents.class);
            intent.putExtra("assignment", clickedAssignment);
            startActivity(intent);

        });

        // Fetch data from Firebase (example method)
        fetchAssignmentsFromFirebase();
    }
    private void fetchAssignmentsFromFirebase() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("assignments");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                assignmentList.clear();  // Clear the list before adding new data
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Assignment assignment = dataSnapshot.getValue(Assignment.class);

                    // Check if assignment is not null and both assignment's courseId and selectedCourse are not null
                    if (assignment != null && assignment.getCourseId() != null && selectedCourse != null) {
                        if (assignment.getCourseId().equals(selectedCourse)) {
                            assignmentList.add(assignment); // Add the assignment that matches the selected course
                        }
                    }
                }
                assignmentsAdapter.notifyDataSetChanged(); // Notify adapter about data changes
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AssignmentActivity.this, "Failed to load assignments", Toast.LENGTH_SHORT).show();
            }
        });
    }


}