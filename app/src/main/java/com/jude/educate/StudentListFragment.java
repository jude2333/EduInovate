package com.jude.educate;

import static androidx.databinding.DataBindingUtil.setContentView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jude.educate.Adapter.StudentListAdapter;
import com.jude.educate.Model.Student;

import java.util.ArrayList;

public class StudentListFragment extends Fragment {

    private RecyclerView recyclerView;
    private DatabaseReference userDatabase, facultyDatabase;
    private StudentListAdapter studentListAdapter;
    private ArrayList<Student> studentList;

    public StudentListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_student_list, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Ensure user is authenticated
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            String facultyUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

            // Initialize views and Firebase references
            recyclerView = view.findViewById(R.id.studentsList);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

            userDatabase = FirebaseDatabase.getInstance().getReference("users");
            facultyDatabase = userDatabase.child(facultyUid); // Simplified reference

            studentList = new ArrayList<>();
            studentListAdapter = new StudentListAdapter(getActivity(), studentList);

            // for the recyclerView single view touch listener and details for the view(students details)
            studentListAdapter.setOnItemClickListener(position -> {
                Student clickedStudent = studentList.get(position);
                // Handle the click, e.g., start a new activity with student details
                Intent intent = new Intent(getActivity(), StudentDetailsActivity.class);
                intent.putExtra("student", clickedStudent);
                startActivity(intent);
            });
            recyclerView.setAdapter(studentListAdapter);

            // clearing the list before fetching new data
            studentList.clear();


            // Fetch student UIDs for the current faculty
            facultyDatabase.child("studentList").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        for (DataSnapshot uidSnapshot : snapshot.getChildren()) {
                            String studentUid = uidSnapshot.getValue(String.class);
                            if (studentUid != null) {
                                fetchStudentDetails(studentUid);
                            }
                        }
                    } else {
                        Log.d("StudentListFragment", "No students found.");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("StudentListFragment", "Failed to fetch student list: " + error.getMessage());
                }
            });
        } else {
            Log.e("StudentListFragment", "User not authenticated.");
        }

    }

    private void fetchStudentDetails(String studentUid) {
        userDatabase.child(studentUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Student student = snapshot.getValue(Student.class);
                if (student != null) {
                    studentList.add(student);
                    studentListAdapter.notifyDataSetChanged();
//                    Log.d("StudentListFragment", "Added student: " + student.getName() + ", " + student.getRegisterNumber());
                } else {
                    Log.e("StudentListFragment", "Failed to parse student data");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("StudentListFragment", "Failed to fetch student details: " + error.getMessage());
            }
        });
    }


}
