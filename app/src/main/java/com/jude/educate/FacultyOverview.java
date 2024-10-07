package com.jude.educate;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.jude.educate.Model.Faculty;
import com.jude.educate.Model.Student;
import com.jude.educate.databinding.ActivityFacultyOverviewBinding;

public class FacultyOverview extends AppCompatActivity {

//    private ActivityFacultyOverviewBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

//        binding = ActivityFacultyOverviewBinding.inflate(getLayoutInflater());
        setContentView(R.layout.activity_faculty_overview);


        // Get student data from intent
        Faculty student = (Faculty) getIntent().getSerializableExtra("faculty");

        // Find views
        TextView nameTextView = findViewById(R.id.nameFaculty);
        TextView emailTextView = findViewById(R.id.emailFaculty);
        TextView passwordTextView = findViewById(R.id.passwordFaculty);
//        TextView ageTextView = findViewById(R.id.detailAge);
//        TextView majorTextView = findViewById(R.id.detailMajor);
//        TextView registerNumberTextView = findViewById(R.id.detailRegisterNumber);
//        TextView courseIdTextView = findViewById(R.id.detailCourseId);

        // Set student data to views
        if (student != null) {
            nameTextView.setText("Name: " + student.getName());
            emailTextView.setText("Email: " + student.getEmail());
            passwordTextView.setText("Password: " + student.getPassword());
//            ageTextView.setText("Age: " + student.getAge());
//            majorTextView.setText("Major: " + student.getMajor());
//            registerNumberTextView.setText("Register Number: " + student.getRegisterNumber());
//            courseIdTextView.setText("Course ID: " + student.getCourseId());
        }

//        // Retrieve data from Intent
//        Intent intent = getIntent();
//        if (intent != null && intent.getExtras() != null) {
//            Bundle bundle = intent.getExtras();
//            String facultyName = bundle.getString("facultyName");
//            String facultyEmail = bundle.getString("facultyEmail");
//            String facultyPassword = bundle.getString("facultyPassword");
//            String facultyUID = bundle.getString("facultyUID");
//
//            binding.nameFaculty.setText(facultyName);
//            binding.emailFaculty.setText(facultyEmail);
//            binding.passwordFaculty.setText(facultyPassword);
//            // binding.facultyUID.setText(facultyUID); // Uncomment if you need to display facultyUID
//        }
    }


}
