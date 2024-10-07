package com.jude.educate.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jude.educate.Model.Course;
import com.jude.educate.Model.Student;
import com.jude.educate.R;

import java.util.ArrayList;

public class PendingCourseListAdapter extends RecyclerView.Adapter<PendingCourseListAdapter.MyViewHolder> {


    Context context;
    ArrayList<Course> list;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    private StudentListAdapter.OnItemClickListener mListener;

    public void setOnItemClickListener(StudentListAdapter.OnItemClickListener listener) {
        this.mListener = listener;
    }


    public PendingCourseListAdapter(Context context, ArrayList<Course> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public PendingCourseListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.pending_course_card,parent,false);
        return  new PendingCourseListAdapter.MyViewHolder(v,mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull PendingCourseListAdapter.MyViewHolder holder, int position) {

//        Student student = list.get(position);
        Course course = list.get(position);

        if (holder.courseName != null && holder.courseCode != null) {
            holder.courseName.setText(course.getCourseName());
            holder.courseCode.setText(course.getCourseId());
        } else {
            Log.e("PendingCourseListAdapter", "TextView is null");
        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView courseName, courseCode;

        public MyViewHolder(@NonNull View itemView,final StudentListAdapter.OnItemClickListener listener) {
            super(itemView);

            courseName = itemView.findViewById(R.id.pendingCourseName);
            courseCode = itemView.findViewById(R.id.pendingCourseCode);

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
