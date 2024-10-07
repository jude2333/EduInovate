package com.jude.educate.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jude.educate.Model.StudentsProgress;
import com.jude.educate.R;

import java.util.List;

public class ProgressAdapter extends RecyclerView.Adapter<ProgressAdapter.ProgressViewHolder> {
    private List<StudentsProgress> studentProgressList;

    public ProgressAdapter(List<StudentsProgress> studentProgressList) {
        this.studentProgressList = studentProgressList;
    }

    @NonNull
    @Override
    public ProgressViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.student_progress_card, parent, false);
        return new ProgressViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProgressViewHolder holder, int position) {
        StudentsProgress studentProgress = studentProgressList.get(position);

        // Set the student name and progress
        holder.studentNameTextView.setText(studentProgress.getStudentName());
        holder.progressBar.setProgress(studentProgress.getProgress());
        holder.percentageTextView.setText(studentProgress.getProgress() + "%");
    }

    @Override
    public int getItemCount() {
        return studentProgressList.size();
    }

    public static class ProgressViewHolder extends RecyclerView.ViewHolder {
        TextView studentNameTextView;
        ProgressBar progressBar;
        TextView percentageTextView;

        public ProgressViewHolder(@NonNull View itemView) {
            super(itemView);
            studentNameTextView = itemView.findViewById(R.id.textViewStudentName);
            progressBar = itemView.findViewById(R.id.progressStudent);
            percentageTextView = itemView.findViewById(R.id.percentageStudentProgress);
        }
    }
}
