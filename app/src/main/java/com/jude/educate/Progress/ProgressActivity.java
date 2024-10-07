package com.jude.educate.Progress;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.*;
import com.jude.educate.Adapter.ProgressAdapter;
import com.jude.educate.FacultyActivity;
import com.jude.educate.Model.StudentsProgress;
import com.jude.educate.R;
import java.util.ArrayList;
import java.util.List;

public class ProgressActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ProgressAdapter progressAdapter;
    private List<StudentsProgress> studentProgressList = new ArrayList<>();
    private final String courseId = FacultyActivity.selectedCourse; // Example course ID, replace with actual logic to get selected course

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress);

        recyclerView = findViewById(R.id.recyclerViewProgress);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        fetchStudentsProgress();
    }

    private void fetchStudentsProgress() {
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");

        usersRef.orderByChild("role").equalTo("student").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                studentProgressList.clear();
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    if (isEnrolledInCourse(userSnapshot, courseId)) {
                        String studentName = userSnapshot.child("name").getValue(String.class);
                        int progress = getStudentProgress(userSnapshot, courseId);
                        studentProgressList.add(new StudentsProgress(studentName, progress));
                    }
                }
                updateRecyclerView();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
            }
        });
    }

    private boolean isEnrolledInCourse(DataSnapshot userSnapshot, String courseId) {
        DataSnapshot coursesEnrolled = userSnapshot.child("coursesEnrolled");
        for (DataSnapshot course : coursesEnrolled.getChildren()) {
            if (courseId.equals(course.getValue(String.class))) {
                return true;
            }
        }
        return false;
    }

    private int getStudentProgress(DataSnapshot userSnapshot, String courseId) {
        DataSnapshot progressSnapshot = userSnapshot.child("progress").child(courseId);
        if (progressSnapshot.exists()) {
            Long progress = progressSnapshot.getValue(Long.class);
            return progress != null ? progress.intValue() : 0;
        }
        return 0;
    }

    private void updateRecyclerView() {
        if (progressAdapter == null) {
            progressAdapter = new ProgressAdapter(studentProgressList);
            recyclerView.setAdapter(progressAdapter);
        } else {
            progressAdapter.notifyDataSetChanged();
        }
    }
}