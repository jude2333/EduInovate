package com.jude.educate.Assignment;

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
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.jude.educate.FacultyActivity;
import com.jude.educate.Model.Assignment;
import com.jude.educate.R;
import com.jude.educate.databinding.ActivityAssignmentCreationBinding;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AssignmentCreationActivity extends AppCompatActivity {

    ActivityAssignmentCreationBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
//        setContentView(R.layout.activity_assignment_creation);
        binding = ActivityAssignmentCreationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        LinearLayout linearAssignment = binding.linearAssignment;
        linearAssignment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFilePicker();
            }
        });

        binding.assignmentSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitAssignment();
            }
        });




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

            // Find the TextView within the inflated pdfView
            TextView pdfNameTextView = pdfView.findViewById(R.id.pdfName);

            if (pdfNameTextView != null) {
                pdfNameTextView.setText(pdfName); // Set the PDF name to the TextView
            } else {
                Log.e("AssignmentCreationActivity", "pdfNameTextView is null, check if the ID is correct in pdf_card layout");
                Toast.makeText(getApplicationContext(), "pdfNameTextView is null", Toast.LENGTH_SHORT).show();
                return;
            }

            if (binding.assignmentListView == null) {
                Log.e("AssignmentCreationActivity", "assignmentListView is null");
                Toast.makeText(this, "assignmentListView is not found", Toast.LENGTH_SHORT).show();
                return;
            }

            binding.assignmentListView.addView(pdfView);
            Log.i("AssignmentCreationActivity", "PDF card added successfully");

            // Optionally store the URI for uploading to Firebase later
            this.selectedPdfUri = pdfUri;  // Save the selected file URI for further use

        } catch (Exception e) {
            Log.e("AssignmentCreationActivity", "Error adding PDF card", e);
            Toast.makeText(this, "Failed to add PDF card", Toast.LENGTH_SHORT).show();
        }
    }


    private Uri selectedPdfUri;  // Store the selected file's URI

    private void submitAssignment() {
        String title = binding.enterTitleAssignment.getText().toString();
        String description = binding.enterTitleDescription.getText().toString();
        String marks = binding.enterMaxMarks.getText().toString();

        int mark = Integer.parseInt(marks);

        // geting current user UID
        String facultyUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Validate title and selected PDF
        if (title.isEmpty() || selectedPdfUri == null) {
            Toast.makeText(this, "Please enter title and select a PDF", Toast.LENGTH_SHORT).show();
            return;
        }

        // get the current date
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String currentDate = sdf.format(new Date());

        // get a reference to Firebase Realtime Database for generating assignmentID
        DatabaseReference assignmentRef = FirebaseDatabase.getInstance().getReference("assignments");
        String assignmentID = assignmentRef.push().getKey();

        // upld the PDF file first
        uploadPdfToFirebase(selectedPdfUri, assignmentID, title, description, mark, facultyUid, currentDate);
    }


    private void uploadPdfToFirebase(Uri fileUri, String assignmentID, String title, String description, int mark, String facultyUid, String currentDate) {
        // Initialize ProgressDialog
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading PDF...");
        progressDialog.setMessage("Please wait while the PDF is being uploaded.");
        progressDialog.setCancelable(false); // Prevent dismissing by tapping outside the dialog
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setProgress(0);
        progressDialog.show();

        StorageReference storageRef = FirebaseStorage.getInstance().getReference("assignments/" + assignmentID + "/" + getFileName(fileUri));

        storageRef.putFile(fileUri)
                .addOnSuccessListener(taskSnapshot -> {
                    Log.d("AssignmentCreation", "PDF uploaded successfully");

                    // Get the download URL
                    storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        Log.d("AssignmentCreation", "PDF Download URL: " + uri.toString());

                        String courseId = FacultyActivity.selectedCourse;

                        String pdfLink = uri.toString();
                        Assignment assignment = new Assignment(title, description, mark, facultyUid, currentDate, pdfLink,courseId,assignmentID);

                        DatabaseReference assignmentRef = FirebaseDatabase.getInstance().getReference("assignments").child(assignmentID);
                        assignmentRef.setValue(assignment)
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(AssignmentCreationActivity.this, "Assignment uploaded successfully!", Toast.LENGTH_SHORT).show();
                                    progressDialog.dismiss();  // Close the progress dialog
                                    finish();  // Close the activity
                                })
                                .addOnFailureListener(e -> {
                                    Log.e("AssignmentCreation", "Failed to save assignment to the database", e);
                                    Toast.makeText(AssignmentCreationActivity.this, "Failed to save assignment to database", Toast.LENGTH_SHORT).show();
                                    progressDialog.dismiss();  // Close the progress dialog
                                });

                    }).addOnFailureListener(e -> {
                        Log.e("AssignmentCreation", "Failed to retrieve download URL", e);
                        Toast.makeText(AssignmentCreationActivity.this, "Failed to retrieve download URL", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();  // Close the progress dialog
                    });
                })
                .addOnFailureListener(e -> {
                    Log.e("AssignmentCreation", "Failed to upload PDF", e);
                    Toast.makeText(AssignmentCreationActivity.this, "Failed to upload PDF", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();  // Close the progress dialog
                })
                .addOnProgressListener(snapshot -> {
                    // Update progress dialog during upload
                    double progress = (100.0 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
                    progressDialog.setProgress((int) progress);
                });
    }








}