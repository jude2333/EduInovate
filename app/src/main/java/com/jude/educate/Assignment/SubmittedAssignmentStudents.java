package com.jude.educate.Assignment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

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
import com.jude.educate.Adapter.SubmissionAssignmentsAdapter;
import com.jude.educate.Model.Assignment;
import com.jude.educate.Model.Submission;
import com.jude.educate.R;
import com.jude.educate.databinding.ActivitySubmittedAssignmentStudentsBinding;

import java.util.ArrayList;
import java.util.List;
public class SubmittedAssignmentStudents extends AppCompatActivity {

    private RecyclerView recyclerView;
    private SubmissionAssignmentsAdapter submissionAssignmentsAdapter;
    private List<Submission> submissionList = new ArrayList<>();  // Initialize the list
    ActivitySubmittedAssignmentStudentsBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        binding = ActivitySubmittedAssignmentStudentsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });



        recyclerView = binding.submittedAssignmentStudentsRecyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        submissionAssignmentsAdapter = new SubmissionAssignmentsAdapter(this, submissionList);
        recyclerView.setAdapter(submissionAssignmentsAdapter);

        Assignment assignment = (Assignment) getIntent().getSerializableExtra("assignment");

        // Optionally set a click listener
        submissionAssignmentsAdapter.setOnItemClickListener(position -> {
            Submission clickedSubmission = submissionList.get(position);
            Intent intent = new Intent(SubmittedAssignmentStudents.this, AssignmentSubmittedDetails.class);
            intent.putExtra("assignment", assignment);
            intent.putExtra("submission", clickedSubmission);
            startActivity(intent);

        });


        loadSubmissionsForCourse();
    }

    private void loadSubmissionsForCourse() {

        Assignment assignment = (Assignment) getIntent().getSerializableExtra("assignment");
        String assignmentId = assignment.getAssignmentID();

        DatabaseReference submissionsRef = FirebaseDatabase.getInstance().getReference("assignments")
                .child(assignmentId)  // Pass the current assignment ID
                .child("submissions");

        submissionsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                submissionList.clear();  // Clear list before adding new data
                for (DataSnapshot submissionSnapshot : snapshot.getChildren()) {
                    Submission submission = submissionSnapshot.getValue(Submission.class);
                    if (submission != null) {
                        submissionList.add(submission);
                    }
                }
                submissionAssignmentsAdapter.notifyDataSetChanged();  // Notify adapter of data changes
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("SubmittedAssignmentStudents", "Error loading submissions", error.toException());
            }
        });
    }
}
