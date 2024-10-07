package com.jude.educate.Assignment;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jude.educate.Model.Assignment;
import com.jude.educate.Model.Submission;
import com.jude.educate.R;

public class AssignmentSubmittedDetails extends AppCompatActivity {
    private TextView assignmentTitle, assignmentDescription, assignmentDate, maxMarksAssignment;
    private EditText gradeSubmission;
    private Button submitGradeButton;
    private LinearLayout fetchedSubmissionPdf;
    private String attachmentUrl;
    private int maxMarks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assignment_submitted_details);

        assignmentTitle = findViewById(R.id.assignmentTitle);
        assignmentDescription = findViewById(R.id.assignmentDescription);
        assignmentDate = findViewById(R.id.assignmentDate);
        maxMarksAssignment = findViewById(R.id.maxMarksAssignment);
        gradeSubmission = findViewById(R.id.gradeSubmission);
        submitGradeButton = findViewById(R.id.submitGradeButton);
        fetchedSubmissionPdf = findViewById(R.id.fetchedSubmissionPdf);

        // Load assignment details
        loadAssignmentDetails();

        Assignment assignment = (Assignment) getIntent().getSerializableExtra("assignment");
        String title = assignment.getTitle();
        String link = assignment.getPdfLink();

        addFetchedPdfCardView(title, link);

        // Set up grading submission
        setupGradingSubmission();
    }

    private void loadAssignmentDetails() {
        Assignment assignment = (Assignment) getIntent().getSerializableExtra("assignment");
        Submission submission = (Submission) getIntent().getSerializableExtra("submission");

        String assignmentId = assignment.getAssignmentID();
        String submissionId = submission.getStudentUid();

        DatabaseReference assignmentRef = FirebaseDatabase.getInstance().getReference("assignments").child(assignmentId);
        DatabaseReference submissionRef = assignmentRef.child("submissions").child(submissionId);

        assignmentRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    assignmentTitle.setText(snapshot.child("title").getValue(String.class));
                    assignmentDescription.setText(snapshot.child("description").getValue(String.class));
                    maxMarks = snapshot.child("maxMarks").getValue(Integer.class);
                    maxMarksAssignment.setText(String.valueOf(maxMarks));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AssignmentSubmittedDetails.this, "Error loading assignment details", Toast.LENGTH_SHORT).show();
            }
        });

        submissionRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    assignmentDate.setText(snapshot.child("submissionDate").getValue(String.class));
                    attachmentUrl = snapshot.child("attachmentUrl").getValue(String.class);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AssignmentSubmittedDetails.this, "Error loading submission details", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupPdfDownload() {
        fetchedSubmissionPdf.setOnClickListener(v -> {
            if (attachmentUrl != null && !attachmentUrl.isEmpty()) {
                // Start download
                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(attachmentUrl));
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "submission.pdf");

                DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                downloadManager.enqueue(request);

                Toast.makeText(this, "Downloading submission PDF", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "No PDF attached to this submission", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupGradingSubmission() {
        submitGradeButton.setOnClickListener(v -> {
            String gradeString = gradeSubmission.getText().toString().trim();

            if (!gradeString.isEmpty()) {
                int grade = Integer.parseInt(gradeString);

                // Validate if grade is within the range of 0 and maxMarks
                if (grade >= 0 && grade <= maxMarks) {
                    Assignment assignment = (Assignment) getIntent().getSerializableExtra("assignment");
                    Submission submission = (Submission) getIntent().getSerializableExtra("submission");

                    if (assignment != null && submission != null) {
                        String assignmentId = assignment.getAssignmentID();
                        String submissionId = submission.getStudentUid();

                        DatabaseReference submissionRef = FirebaseDatabase.getInstance().getReference("assignments")
                                .child(assignmentId).child("submissions").child(submissionId);

                        submissionRef.child("score").setValue(grade)
                                .addOnSuccessListener(aVoid -> {

                                    Toast.makeText(AssignmentSubmittedDetails.this, "Grade submitted successfully", Toast.LENGTH_SHORT).show();


                                    finish();
                                })
                                .addOnFailureListener(e -> {
                                    // Show a failure message
                                    Toast.makeText(AssignmentSubmittedDetails.this, "Failed to submit grade", Toast.LENGTH_SHORT).show();
                                });
                    } else {
                        Toast.makeText(this, "Error: Assignment or Submission is null", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "Grade must be between 0 and " + maxMarks, Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Please enter a grade", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void addFetchedPdfCardView(String pdfName, String pdfLink) {
            LinearLayout fetchedLayout = findViewById(R.id.fetchedSubmissionPdf);
            View pdfCard = getLayoutInflater().inflate(R.layout.pdf_card, null);

            TextView pdfNameTextView = pdfCard.findViewById(R.id.pdfName);
            pdfNameTextView.setText(pdfName);

            pdfCard.setOnClickListener(v -> {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(pdfLink));
                startActivity(browserIntent);
            });

            fetchedLayout.addView(pdfCard);
        }


}