package com.jude.educate;

import android.content.Intent;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.jude.educate.databinding.ActivityFacultyOrStudentBinding;

public class FacultyOrStudentActivity extends AppCompatActivity {

    ActivityFacultyOrStudentBinding binding;

    CheckBox facultyCheckbox;
    CheckBox studentCheckbox;
    AppCompatButton nextButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Inflate the binding
        binding = ActivityFacultyOrStudentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        // checkboxes
        facultyCheckbox = binding.FacultyCheckbox;
        studentCheckbox = binding.StudentCheckbox;
        nextButton = binding.NextButton;

        // making them group
        facultyCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                studentCheckbox.setChecked(false);
            }
        });



        studentCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                facultyCheckbox.setChecked(false);
            }
        });

        // Set up the Next button click listener
        nextButton.setOnClickListener(v -> {
            if (facultyCheckbox.isChecked()) {
                // Navigating TeacherSignUpActivity
                Intent intent = new Intent(FacultyOrStudentActivity.this, FacultySignUpActivity.class);
                startActivity(intent);
                finish();
            } else if (studentCheckbox.isChecked()) {
                // Navigating StudentSignUpActivity
                Intent intent = new Intent(FacultyOrStudentActivity.this, StudentSignUpActivity.class);
                startActivity(intent);
                finish();
            } else {
                // toast message for not choose
                Toast.makeText(FacultyOrStudentActivity.this, "Please select Teacher or Student", Toast.LENGTH_SHORT).show();
            }
        });











































    }
}