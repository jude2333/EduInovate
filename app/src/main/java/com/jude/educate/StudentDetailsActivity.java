package com.jude.educate;

import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.jude.educate.Model.Student;

public class StudentDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_student_details);

//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainStudentDetails), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });

        // Get student data from intent
        Student student = (Student) getIntent().getSerializableExtra("student");

        // Find views
        TextView nameTextView = findViewById(R.id.detailName);
        TextView emailTextView = findViewById(R.id.detailEmail);
        TextView passwordTextview = findViewById(R.id.detailPassword);
//        TextView ageTextView = findViewById(R.id.detailAge);
//        TextView majorTextView = findViewById(R.id.detailMajor);
//        TextView registerNumberTextView = findViewById(R.id.detailRegisterNumber);
//        TextView courseIdTextView = findViewById(R.id.detailCourseId);

        // Set student data to views
        if (student != null) {
            nameTextView.setText("Name: " + student.getName());
            emailTextView.setText("Email: " + student.getEmail());
            passwordTextview.setText("Password: " + student.getPassword());
//            ageTextView.setText("Age: " + student.getAge());
//            majorTextView.setText("Major: " + student.getMajor());
//            registerNumberTextView.setText("Register Number: " + student.getRegisterNumber());
//            courseIdTextView.setText("Course ID: " + student.getCourseId());
        }
    }
}