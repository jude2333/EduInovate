package com.jude.educate.StudyMaterial;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.jude.educate.Adapter.AssignmentsAdapter;
import com.jude.educate.Adapter.MaterialsAdapter;
import com.jude.educate.Assignment.AssignmentActivity;
import com.jude.educate.Assignment.AssignmentCreationActivity;
import com.jude.educate.Assignment.SubmittedAssignmentStudents;
import com.jude.educate.FacultyActivity;
import com.jude.educate.Model.Assignment;
import com.jude.educate.Model.Material;
import com.jude.educate.R;
import com.jude.educate.databinding.ActivityAssignmentBinding;
import com.jude.educate.databinding.ActivityAssignmentCreationBinding;
import com.jude.educate.databinding.ActivityStudyMaterialBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class StudyMaterial extends AppCompatActivity {

    ActivityStudyMaterialBinding binding;

    private RecyclerView recyclerView;
    private MaterialsAdapter materialAdapter;
    private List<Material> materialList;

    String selectedCourse = FacultyActivity.selectedCourse;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityStudyMaterialBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        binding.materialCreateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StudyMaterial.this, StudyMaterialCreation.class);
                startActivity(intent);
                finish();
            }
        });

        recyclerView = binding.assignmentsRecyclerViewMaterial;

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        materialList = new ArrayList<>();
        materialAdapter = new MaterialsAdapter(this, materialList);

        recyclerView.setAdapter(materialAdapter);


        materialAdapter.setOnItemClickListener(position -> {
            Material clickedMaterial = materialList.get(position);
            String pdfLink = clickedMaterial.getPdfLink();

            // Open the PDF in the browser
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(pdfLink));
            startActivity(browserIntent);
        });


        fetchAssignmentsFromFirebase();
    }
    private void fetchAssignmentsFromFirebase() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("materials");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                materialList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Material material = dataSnapshot.getValue(Material.class);

                    if (material != null && material.getCourseId() != null && selectedCourse != null) {
                        if (material.getCourseId().equals(selectedCourse)) {
                            materialList.add(material);
                        }
                    }
                }
                materialAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(StudyMaterial.this, "Failed to load assignments", Toast.LENGTH_SHORT).show();
            }
        });
    }
}