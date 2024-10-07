package com.jude.educate.Poll;

//import static com.jude.educate.MainActivityFragment.recyclerView;

import android.content.Intent;
import android.os.Bundle;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jude.educate.Adapter.PollAdapter;
import com.jude.educate.Adapter.PollSubmissionAdapter;
import com.jude.educate.Assignment.AssignmentSubmittedDetails;
import com.jude.educate.Assignment.SubmittedAssignmentStudents;
import com.jude.educate.FacultyActivity;
import com.jude.educate.Model.Poll;
import com.jude.educate.Model.Submission;
import com.jude.educate.R;
import com.jude.educate.databinding.ActivityPollBinding;

import java.util.ArrayList;
import java.util.List;

public class PollActivity extends AppCompatActivity {

    ActivityPollBinding binding;
    RecyclerView recyclerView;
    List<Poll> pollList;

    PollSubmissionAdapter pollSubmissionAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityPollBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        recyclerView = binding.previousPollsRecyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        pollList = new ArrayList<>();

        pollSubmissionAdapter = new PollSubmissionAdapter(pollList, this);
        recyclerView.setAdapter(pollSubmissionAdapter);

        // Get the current faculty ID and selected course ID
        String currentFacultyId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String selectedCourseId = FacultyActivity.selectedCourse; // Replace with the actual course ID for the current course
        // null check
        if (selectedCourseId != null) {

            // Fetch the polls from Firebase
            DatabaseReference pollsRef = FirebaseDatabase.getInstance().getReference("polls");
            pollsRef.orderByChild("facultyId").equalTo(currentFacultyId)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            pollList.clear(); // Clear the list to avoid duplicates
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                Poll poll = snapshot.getValue(Poll.class);

                                // filter the polls based on the selected course ID
                                if (poll != null && poll.getCourseId().equals(selectedCourseId)) {
                                    pollList.add(poll);
                                }
                            }
                            // Notify the adapter that the data has changed
                            pollSubmissionAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Toast.makeText(PollActivity.this, "Failed to load polls.", Toast.LENGTH_SHORT).show();
                        }
                    });

            binding.createPollButton.setOnClickListener(v -> {
                if(selectedCourseId != null){
                    Intent intent = new Intent(this, PollCreationActivity.class);
                    startActivity(intent);
                }else{
                    Toast.makeText(this, "Please select a course first", Toast.LENGTH_SHORT).show();
                }

            });

//            pollSubmissionAdapter.setOnItemClickListener(position -> {
//                Poll clickedPoll = pollList.get(position);
//                Intent intent = new Intent(PollActivity.this, PollsSubmittedDetails.class);
//                intent.putExtra("poll", clickedPoll);
//                startActivity(intent);
//
//            });
        }

 }
}