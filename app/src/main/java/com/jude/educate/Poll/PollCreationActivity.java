package com.jude.educate.Poll;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.jude.educate.FacultyActivity;
import com.jude.educate.Model.Poll;
import com.jude.educate.R;
import com.jude.educate.databinding.ActivityPollCreationBinding;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
public class PollCreationActivity extends AppCompatActivity {

    ActivityPollCreationBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityPollCreationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();

        if (FacultyActivity.selectedCourse != null) {
            binding.createPollButton.setOnClickListener(v -> {
                String question = binding.pollQuestionEditText.getText().toString().trim();
                String option1 = binding.option1EditText.getText().toString().trim();
                String option2 = binding.option2EditText.getText().toString().trim();

                if (question.isEmpty() || option1.isEmpty() || option2.isEmpty()) {
                    Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                // get the current date
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                String currentDate = sdf.format(new Date());

                createPoll(currentUser, FacultyActivity.selectedCourse, question, option1, option2, currentDate);
            });
        }
    }

    private void createPoll(String facultyId, String courseId, String question, String option1, String option2,String currDate) {
        DatabaseReference pollRef = FirebaseDatabase.getInstance().getReference().child("polls").push();

        String pollId = pollRef.getKey();

        Map<String, String> options = new HashMap<>();
        options.put("option1", option1);
        options.put("option2", option2);

        Poll poll = new Poll(pollId, question, options, facultyId, courseId,currDate);

        pollRef.setValue(poll)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Poll created successfully!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to create poll.", Toast.LENGTH_SHORT).show();
                });
    }
}
