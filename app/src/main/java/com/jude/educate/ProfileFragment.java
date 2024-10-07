package com.jude.educate;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import de.hdodenhof.circleimageview.CircleImageView;
public class ProfileFragment extends Fragment {
    private static final int PICK_IMAGE_REQUEST = 1;
    private DatabaseReference usersDatabase;
    private StorageReference storageReference;

    private TextView userNameTextView, userEmailTextView, userPassword;
    private CircleImageView profileImageView;
    private ImageView editPen;

    private Uri imageUri;
    private String currentUserUid;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize Firebase references
        usersDatabase = FirebaseDatabase.getInstance().getReference("users");
        storageReference = FirebaseStorage.getInstance().getReference("profileImages");

        // Initialize Views
        userNameTextView = view.findViewById(R.id.profileName);
        userEmailTextView = view.findViewById(R.id.profileEmail);
        userPassword = view.findViewById(R.id.profilePassword);
        profileImageView = view.findViewById(R.id.profileImg);
        editPen = view.findViewById(R.id.editPen);

        // Get current user UID
        currentUserUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Fetch user details
        fetchUserDetails(currentUserUid);

        // Set OnClickListener for editPen
        editPen.setOnClickListener(v -> openGallery());
    }

    // Open gallery to select an image
    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            // Set the selected image to the profile ImageView
            profileImageView.setImageURI(imageUri);

            // Now upload the image to Firebase Storage
            uploadImageToFirebase();
        }
    }

    private void uploadImageToFirebase() {
        if (imageUri != null) {
            // Create a unique reference for the image
            StorageReference fileReference = storageReference.child(currentUserUid + ".jpg");

            fileReference.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        // Get the download URL and save it in the database
                        fileReference.getDownloadUrl().addOnSuccessListener(uri -> {
                            String imageUrl = uri.toString();
                            saveImageUrlToDatabase(imageUrl);
                        });
                    })
                    .addOnFailureListener(e -> {
                        if (getActivity() != null) {
                            Toast.makeText(getActivity(), "Image upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void saveImageUrlToDatabase(String imageUrl) {
        // Save the image URL in the user's profile in the Realtime Database
        usersDatabase.child(currentUserUid).child("profileImage").setValue(imageUrl)
                .addOnSuccessListener(aVoid -> {
                    if (getActivity() != null) {
                        Toast.makeText(getActivity(), "Profile image updated successfully", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    if (getActivity() != null) {
                        Toast.makeText(getActivity(), "Failed to update profile image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void fetchUserDetails(String uid) {
        usersDatabase.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && getActivity() != null && isAdded()) { // Ensure the fragment is attached
                    String name = snapshot.child("name").getValue(String.class);
                    String email = snapshot.child("email").getValue(String.class);
                    String password = snapshot.child("password").getValue(String.class);
                    String profileImageUrl = snapshot.child("profileImage").getValue(String.class);

                    // Set values to the TextViews
                    userNameTextView.setText(name);
                    userEmailTextView.setText(email);
                    userPassword.setText(password);

                    // Load the profile image using Glide if the fragment is still attached
                    if (profileImageUrl != null && getActivity() != null && isAdded()) {
                        Glide.with(ProfileFragment.this)
                                .load(profileImageUrl)
                                .into(profileImageView);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Profile", "Failed to fetch user details: " + error.getMessage());
                if (getActivity() != null) {
                    Toast.makeText(getActivity(), "Failed to fetch user details", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}
