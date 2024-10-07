package com.jude.educate.Assignment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.jude.educate.FacultyActivity;
import com.jude.educate.Model.Assignment;
import com.jude.educate.Model.Student;
import com.jude.educate.Model.Submission;
import com.jude.educate.R;
import com.jude.educate.StudentActivity;
import com.jude.educate.StudentActivityFragment;
import com.jude.educate.databinding.ActivityStudentAssignmentSubmissionBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class StudentAssignmentSubmission extends AppCompatActivity {

    ActivityStudentAssignmentSubmissionBinding binding;
    String userName;
//    private Uri selectedPdfUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Properly inflate the ViewBinding
        binding = ActivityStudentAssignmentSubmissionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());  // Use the binding to set the content view

        // Any other UI setup logic comes after binding initialization
        EdgeToEdge.enable(this);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Assignment assignment = (Assignment) getIntent().getSerializableExtra("assignment");
        String assignmentId = assignment.getAssignmentID();

        DatabaseReference assignmentRef = FirebaseDatabase.getInstance().getReference("assignments").child(assignmentId);
        assignmentRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String pdfLink = snapshot.child("pdfLink").getValue(String.class);
                    String title = snapshot.child("title").getValue(String.class);
                    String description = snapshot.child("description").getValue(String.class);
                    String submittedTime = snapshot.child("submittedTime").getValue(String.class);

                    binding.assignmentFetchedTitle.setText(title);
                    binding.assignmentFetchedDescription.setText(description);
                    binding.assignmentFetchedDate.setText(submittedTime);



                    addFetchedPdfCardView(title, pdfLink);

                    Assignment assignment = (Assignment) getIntent().getSerializableExtra("assignment");
                    String assignmentId = assignment.getAssignmentID();
                    String currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();

                    DatabaseReference assignmentRef = FirebaseDatabase.getInstance().getReference("assignments").child(assignmentId);
                    DatabaseReference submissionRef = assignmentRef.child("submissions").child(currentUser);

                    submissionRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                binding.submitAssignmentButton.setVisibility(View.GONE);
                                binding.linearAssignment.setVisibility(View.GONE);

                                // Check for assigned score
                                Integer score = snapshot.child("score").getValue(Integer.class);
                                if (score != null) {
                                    // Score assigned, show it
                                    binding.assignedScore.setVisibility(View.VISIBLE);
                                    binding.assignedScore.setText("Score: " + score);
                                } else {
                                    // No score assigned yet
                                    binding.assignedScore.setVisibility(View.VISIBLE);
                                    binding.assignedScore.setText("Marks not assigned");
                                }
                            } else {
                                // Assignment not submitted, show the submit button
                                binding.submitAssignmentButton.setVisibility(View.VISIBLE);
                                //setupSubmitButton();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.e("FirebaseError", "Error fetching submission data", error.toException());
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("AssignmentFetch", "Error fetching data", error.toException());
            }
        });

        LinearLayout linearAssignment = binding.linearAssignment;
        linearAssignment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFilePicker();
            }
        });

//      submit button
        Button submitButton = binding.submitAssignmentButton;
        submitButton.setOnClickListener(v -> handleSubmitAssignment());
    }

    private void addFetchedPdfCardView(String pdfName, String pdfLink) {
        LinearLayout fetchedLayout = findViewById(R.id.fetchedAssignmentPdf);
        View pdfCard = getLayoutInflater().inflate(R.layout.pdf_card, null);

        TextView pdfNameTextView = pdfCard.findViewById(R.id.pdfName);
        pdfNameTextView.setText(pdfName);

        pdfCard.setOnClickListener(v -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(pdfLink));
            startActivity(browserIntent);
        });

        fetchedLayout.addView(pdfCard);
    }

    private static final int PICK_PDF_FILE = 2;

    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("application/pdf");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, PICK_PDF_FILE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_PDF_FILE && resultCode == RESULT_OK) {
            if (data != null) {
                Uri pdfUri = data.getData();
                String pdfName = getFileName(pdfUri);
                // After selecting the file, add the file to the view
                displayPdfCard(pdfName, pdfUri);
            }
        }
    }

    // Helper method to get the file name
    private String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);

                    if (nameIndex >= 0) { // Ensure the column index is valid
                        result = cursor.getString(nameIndex);
                    } else {
                        // Fallback if column index is not found
                        Log.e("getFileName", "DISPLAY_NAME column not found");
                        result = "unknown_file";
                    }
                }
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    private void displayPdfCard(String pdfName, Uri pdfUri) {
        try {
            // Inflateing the pdf_card layout
            View pdfView = getLayoutInflater().inflate(R.layout.pdf_card, null);

            if (pdfView == null) {
                Log.e("AssignmentCreationActivity", "Inflation of pdf_card failed");
                Toast.makeText(this, "Failed to inflate pdf_card layout", Toast.LENGTH_SHORT).show();
                return;
            }


            TextView pdfNameTextView = pdfView.findViewById(R.id.pdfName);

            if (pdfNameTextView != null) {
                pdfNameTextView.setText(pdfName); // Set the PDF name to the TextView
            } else {
                Log.e("AssignmentCreationActivity", "pdfNameTextView is null, check if the ID is correct in pdf_card layout");
                Toast.makeText(getApplicationContext(), "pdfNameTextView is null", Toast.LENGTH_SHORT).show();
                return;
            }

            if (binding.browsedPdfForAssignment == null) {
                Log.e("AssignmentCreationActivity", "assignmentListView is null");
                Toast.makeText(this, "assignmentListView is not found", Toast.LENGTH_SHORT).show();
                return;
            }

            binding.browsedPdfForAssignment.addView(pdfView);
            Log.i("AssignmentCreationActivity", "PDF card added successfully");


            this.selectedPdfUri = pdfUri;

        } catch (Exception e) {
            Log.e("AssignmentCreationActivity", "Error adding PDF card", e);
            Toast.makeText(this, "Failed to add PDF card", Toast.LENGTH_SHORT).show();
        }
    }


    private Uri selectedPdfUri;  // Store the selected file's URI
    private ProgressDialog progressDialog;

    private void handleSubmitAssignment() {
        if (selectedPdfUri != null) {
            // Step 1: Show progress dialog
            progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading Assignment");
            progressDialog.setMessage("Please wait while the file is being uploaded...");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setIndeterminate(false);
            progressDialog.setMax(100);
            progressDialog.setProgress(0);
            progressDialog.setCancelable(false);
            progressDialog.show();

            // Step 2: Upload the selected PDF to Firebase Storage
            StorageReference storageRef = FirebaseStorage.getInstance().getReference();
            String currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
            Assignment assignment = (Assignment) getIntent().getSerializableExtra("assignment");
            String assignmentId = assignment.getAssignmentID();

            // Create a unique file path for this user's assignment submission
            StorageReference pdfRef = storageRef.child("assignments").child(assignmentId).child(currentUser + "_submission.pdf");

            pdfRef.putFile(selectedPdfUri)
                    .addOnProgressListener(snapshot -> {
                        // Update progress bar
                        double progress = (100.0 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
                        progressDialog.setProgress((int) progress);
                    })
                    .addOnSuccessListener(taskSnapshot -> {
                        // Step 3: Retrieve the download URL of the uploaded file
                        pdfRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            String attachmentUrl = uri.toString(); // This is the URL of the uploaded PDF

                            // Hide the progress dialog when upload is complete
                            progressDialog.dismiss();

                            submitAssignmentToDatabase(attachmentUrl);  // Submit the assignment with the PDF URL

                        }).addOnFailureListener(e -> {
                            // Hide progress dialog and handle any errors in getting the download URL
                            progressDialog.dismiss();
                            Toast.makeText(StudentAssignmentSubmission.this, "Failed to get download URL", Toast.LENGTH_SHORT).show();
                        });

                    }).addOnFailureListener(e -> {
                        // Handle any errors in uploading the file
                        progressDialog.dismiss();
                        Toast.makeText(StudentAssignmentSubmission.this, "File upload failed", Toast.LENGTH_SHORT).show();

                    });
        } else {
            Toast.makeText(this, "Please select a file to submit", Toast.LENGTH_SHORT).show();
        }
    }

    // Submit assignment to the database after successful upload
    private void submitAssignmentToDatabase(String attachmentUrl) {
        // Assuming you have collected the submission data from user input
        String submissionDate = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(new Date()); // Get the current date
        String currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Getting userName from Firebase
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(currentUser);
        userRef.child("name").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String userName = snapshot.getValue(String.class);
                Log.d("UserName", "User Name: " + userName);

                // Create a new Submission object
                Submission submission = new Submission(submissionDate, null, attachmentUrl, userName,currentUser);

                Assignment assignment = (Assignment) getIntent().getSerializableExtra("assignment");
                String assignmentId = assignment.getAssignmentID();
                DatabaseReference assignmentRef = FirebaseDatabase.getInstance().getReference("assignments").child(assignmentId);

                // Push the submission under the specific assignment's submissions node
                assignmentRef.child("submissions").child(currentUser).setValue(submission)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(StudentAssignmentSubmission.this, "Submission successful", Toast.LENGTH_SHORT).show();
                                finish();  // Optionally, navigate back to the previous activity
                            } else {
                                Toast.makeText(StudentAssignmentSubmission.this, "Submission failed", Toast.LENGTH_SHORT).show();
                            }
                        });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseError", "Error fetching user name", error.toException());
            }
        });
    }


}
