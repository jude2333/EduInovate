package com.jude.educate.Adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jude.educate.Model.Assignment;
import com.jude.educate.Model.Material;
import com.jude.educate.R;

import java.util.List;

public class MaterialsAdapter extends RecyclerView.Adapter<MaterialsAdapter.MyViewHolder> {

    Context context;
    List<Material> list;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    private OnItemClickListener mListener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }

    public MaterialsAdapter(Context context, List<Material> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.assignments_card, parent, false);
        return new MyViewHolder(v, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Material material = list.get(position);

        // Set the data directly without null checks on the views
        holder.assignmentTitle.setText(material.getTitle());
        holder.assignmentDate.setText(material.getSubmittedTime()); // Ensure your getter matches your model class
        holder.assignmentDescription.setText(material.getDescription());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView assignmentTitle, assignmentDate, assignmentDescription;

        public MyViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);

            assignmentTitle = itemView.findViewById(R.id.AssignmentTitle);
            assignmentDate = itemView.findViewById(R.id.AssignmentDate);
            assignmentDescription = itemView.findViewById(R.id.AssignmentDescription);

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
