package com.jude.educate.Adapter;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.jude.educate.Model.Poll;
import com.jude.educate.Model.PollSubmission;
import com.jude.educate.R;

import java.util.HashMap;
import java.util.List;

public class PollSubmissionAdapter extends RecyclerView.Adapter<PollSubmissionAdapter.PollViewHolder> {

    private List<Poll> pollList;
    private Context context;
    String role;

    public PollSubmissionAdapter(List<Poll> pollList, Context context) {
        this.pollList = pollList;
        this.context = context;
    }

    @NonNull
    @Override
    public PollViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.poll_submission_card, parent, false);
        return new PollViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull PollViewHolder holder, int position) {
        Poll poll = pollList.get(position);

        holder.pollTitle.setText("Created Poll");
        holder.pollDate.setText(poll.getDate());
        holder.pollQuestion.setText(poll.getQuestion());

        // Set the text for the options
        holder.option1.setText(poll.getOptions().get("option1"));
        holder.option2.setText(poll.getOptions().get("option2"));

        // Calculate progress and display percentage for both options
        int totalVotes = poll.getTotalVotes();
        if (totalVotes > 0) {
            int option1Percentage = (poll.getOption1Votes() * 100) / totalVotes;
            int option2Percentage = (poll.getOption2Votes() * 100) / totalVotes;

            holder.progressOption1.setProgress(option1Percentage);
            holder.progressOption2.setProgress(option2Percentage);

            holder.option1Percentage.setText(option1Percentage + "%");
            holder.option2Percentage.setText(option2Percentage + "%");
        } else {
            holder.progressOption1.setProgress(0);
            holder.progressOption2.setProgress(0);

            holder.option1Percentage.setText("0%");
            holder.option2Percentage.setText("0%");
        }

        // get current student ID from Firebase Auth
        String currentStudentId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // refernce to the user's node in the Firebase Database
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(currentStudentId);

        // addd a Listener to get the user's role
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String role = dataSnapshot.child("role").getValue(String.class);

                    if (role != null && role.equals("faculty")) {
                        // hide views for faculty
                        holder.submitPollButton.setVisibility(View.GONE);
//                        holder.option1.setVisibility(View.GONE);
//                        holder.option2.setVisibility(View.GONE);
                    } else if (role != null && role.equals("student")) {
                        // Show views for students
//                        holder.submitPollButton.setVisibility(View.VISIBLE);
                        Log.d("UserRole", "User role: " + role);
                    } else {
                        Log.d("UserRole", "Role not found for user.");
                    }
                } else {
                    Log.d("UserRole", "User not found.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // handle possible errors
                Log.e("UserRole", "Error fetching user role", databaseError.toException());
            }
        });


        // Check if the student has already submitted the poll
        if (poll.getSubmissions() != null && poll.getSubmissions().containsKey(currentStudentId)) {
            holder.submitPollButton.setVisibility(View.GONE);
        } else {
            holder.submitPollButton.setVisibility(View.VISIBLE);

            // Submit Button click handler
            holder.submitPollButton.setOnClickListener(v -> {
                int selectedOptionId = holder.pollOptionsGroup.getCheckedRadioButtonId();
                if (selectedOptionId == -1) {
                    Toast.makeText(context, "Please select an option", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Identify the selected option
                String selectedOption = "";
                if (selectedOptionId == holder.option1.getId()) {
                    poll.addVote("option1");
                    selectedOption = "option1";
                } else if (selectedOptionId == holder.option2.getId()) {
                    poll.addVote("option2");
                    selectedOption = "option2";
                }

                // Create a PollSubmission object
                PollSubmission submission = new PollSubmission(currentStudentId, selectedOption);

                // Update the poll's submissions map
                if (poll.getSubmissions() == null) {
                    poll.setSubmissions(new HashMap<>());
                }
                poll.getSubmissions().put(currentStudentId, submission);

                // Update Firebase with the student's submission and vote counts
                DatabaseReference pollRef = FirebaseDatabase.getInstance().getReference("polls").child(poll.getPollId());
                pollRef.child("option1Votes").setValue(poll.getOption1Votes());
                pollRef.child("option2Votes").setValue(poll.getOption2Votes());
                pollRef.child("totalVotes").setValue(poll.getTotalVotes());
                pollRef.child("submissions").child(currentStudentId).setValue(submission);

                Toast.makeText(context, "Poll submitted successfully", Toast.LENGTH_SHORT).show();

                // Hide the submit button after vote
                holder.submitPollButton.setVisibility(View.GONE);

                // Notify adapter to refresh the data and update UI
                notifyItemChanged(position);

            });
        }
    }



    @Override
    public int getItemCount() {
        return pollList.size();
    }

    // ViewHolder class for holding each poll card
    public static class PollViewHolder extends RecyclerView.ViewHolder {

        TextView pollTitle, pollDate, pollQuestion, option1Percentage, option2Percentage;
        RadioGroup pollOptionsGroup;
        RadioButton option1, option2;
        ProgressBar progressOption1, progressOption2;
        Button submitPollButton;

        public PollViewHolder(@NonNull View itemView) {
            super(itemView);

            pollTitle = itemView.findViewById(R.id.pollTitle);
            pollDate = itemView.findViewById(R.id.pollDate);
            pollQuestion = itemView.findViewById(R.id.pollQuestionCard);
            pollOptionsGroup = itemView.findViewById(R.id.pollOptionsGroup);
            option1 = itemView.findViewById(R.id.option1Card);
            option2 = itemView.findViewById(R.id.option2Card);
            progressOption1 = itemView.findViewById(R.id.progressOption1Card);
            progressOption2 = itemView.findViewById(R.id.progressOption2Card);
            option1Percentage = itemView.findViewById(R.id.pollSubmittedPercentageOption1);
            option2Percentage = itemView.findViewById(R.id.pollSubmittedPercentageOption2);
            submitPollButton = itemView.findViewById(R.id.submitPollButton);
        }
    }
}
