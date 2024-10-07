package com.jude.educate.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jude.educate.Model.Assignment;
import com.jude.educate.Model.Student;
import com.jude.educate.Model.Submission;
import com.jude.educate.R;

import java.util.ArrayList;
import java.util.List;

public class SubmissionAssignmentsAdapter extends RecyclerView.Adapter<SubmissionAssignmentsAdapter.MyViewHolder> {

    Context context;
    List<Submission> list;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    private OnItemClickListener mListener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }

    public SubmissionAssignmentsAdapter(Context context, List<Submission> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.assignments_submitted_students_card, parent, false);
        return new MyViewHolder(v, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Submission submission = list.get(position);

        // Set the data directly without null checks on the views
        holder.assignmentStudName.setText(submission.getStudentName());
        holder.assignmentDate.setText(submission.getSubmissionDate()); // Ensure your getter matches your model class
//        holder.assignmentDescription.setText(assignment.getDescription());

        if (submission.getScore() != null) {
            holder.assignmentScore.setText(String.valueOf("Score: " + submission.getScore()));
        } else {
            holder.assignmentScore.setText("Not Graded");
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView assignmentStudName, assignmentDate,assignmentScore;

        public MyViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);

            assignmentStudName = itemView.findViewById(R.id.studentNameAssignment);
            assignmentDate = itemView.findViewById(R.id.studentDateAssignment);
            assignmentScore = itemView.findViewById(R.id.markAssigned);
//            assignmentDescription = itemView.findViewById(R.id.AssignmentDescription);

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(position);
                    }
                }
            });
        }
    }
}
