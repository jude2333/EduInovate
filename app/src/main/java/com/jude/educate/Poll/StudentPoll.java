package com.jude.educate.Poll;

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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.jude.educate.Adapter.PollAdapter;
import com.jude.educate.Adapter.PollSubmissionAdapter;
import com.jude.educate.FacultyActivity;
import com.jude.educate.Model.Poll;
import com.jude.educate.R;
import com.jude.educate.StudentActivity;
import com.jude.educate.databinding.ActivityStudentPollBinding;

import java.util.ArrayList;
import java.util.List;

public class StudentPoll extends AppCompatActivity {

    ActivityStudentPollBinding binding;
    PollSubmissionAdapter pollSubmissionAdapter;
    RecyclerView recyclerView;

    List<Poll> pollList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityStudentPollBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        recyclerView = binding.pollStudentRecyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        pollList = new ArrayList<>();

        pollSubmissionAdapter = new PollSubmissionAdapter(pollList, this);
        recyclerView.setAdapter(pollSubmissionAdapter);

        if(StudentActivity.selectedCourse != null){
            fetchPollsForCourse(StudentActivity.selectedCourse);

        }
    }

    private void fetchPollsForCourse(String courseId) {
        DatabaseReference pollsRef = FirebaseDatabase.getInstance().getReference("polls");

        // Fetch all polls where courseId matches the selected course
        Query pollQuery = pollsRef.orderByChild("courseId").equalTo(courseId);

        pollQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                pollList.clear();

                for (DataSnapshot pollSnapshot : snapshot.getChildren()) {
                    Poll poll = pollSnapshot.getValue(Poll.class);

                    if (poll != null) {
                        pollList.add(poll);
                    }
                }


//                pollAdapter = new PollAdapter(pollList,StudentPoll.this);  // Assuming you have a PollAdapter
                pollSubmissionAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseError", "Error fetching polls", error.toException());
            }
        });
    }

}