package com.jude.educate.StudyMaterial;

import android.content.Intent;
import android.net.Uri;
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
import com.jude.educate.Adapter.AssignmentsAdapter;
import com.jude.educate.Adapter.MaterialsAdapter;
import com.jude.educate.Assignment.StudentAssignmentSubmission;
import com.jude.educate.Model.Assignment;
import com.jude.educate.Model.Material;
import com.jude.educate.R;
import com.jude.educate.StudentActivity;
import com.jude.educate.databinding.ActivityStudentMaterialBinding;
import com.jude.educate.databinding.ActivityStudyMaterialBinding;

import java.util.ArrayList;
import java.util.List;

public class StudentMaterialActivity extends AppCompatActivity {

    private List<Material> materialtList = new ArrayList<>();
    private MaterialsAdapter materialAdapter;
    private RecyclerView recyclerView;

    ActivityStudentMaterialBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityStudentMaterialBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        // Apply window insets to avoid UI overlap with system bars

        // Initialize RecyclerView and Adapter
        recyclerView = binding.materialStudentList;
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        materialAdapter = new MaterialsAdapter(this, materialtList);
        recyclerView.setAdapter(materialAdapter);

        // Click listener for RecyclerView items
        materialAdapter.setOnItemClickListener(position -> {
            Material clickedMaterial = materialtList.get(position);
            String pdfLink = clickedMaterial.getPdfLink();

            // Open the PDF in the browser
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(pdfLink));
            startActivity(browserIntent);
        });
        if(StudentActivity.selectedCourse != null){
            fetchCurrentCourseAssignments(StudentActivity.selectedCourse);
        }
        // Fetch assignments for the current selected course

    }

    private void fetchCurrentCourseAssignments(String courseId) {
        DatabaseReference assignmentsRef = FirebaseDatabase.getInstance().getReference("materials");

        assignmentsRef.orderByChild("courseId").equalTo(courseId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        materialtList.clear(); // Clear previous data

                        for (DataSnapshot materialSnapshot : snapshot.getChildren()) {
                            Material material = materialSnapshot.getValue(Material.class);
                            if (material != null && StudentActivity.selectedCourse != null && material.getCourseId().equals(StudentActivity.selectedCourse)) {
                                materialtList.add(material);
                            }
                        }

                        // Notify the adapter to update the RecyclerView
                        materialAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Log and handle errors here
                        Log.e("StudentMaterialActivity", "Error fetching materials", error.toException());
                    }
                });
    }
}
