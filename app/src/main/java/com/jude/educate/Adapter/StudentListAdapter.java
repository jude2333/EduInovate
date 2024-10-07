package com.jude.educate.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jude.educate.Model.Student;
import com.jude.educate.R;

import java.util.ArrayList;

public class StudentListAdapter extends RecyclerView.Adapter<StudentListAdapter.MyViewHolder> {

    Context context;
    ArrayList<Student> list;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    private OnItemClickListener mListener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }


    public StudentListAdapter(Context context, ArrayList<Student> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.student_card_view,parent,false);
        return  new MyViewHolder(v,mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Student student = list.get(position);

        if (holder.studentName != null && holder.registerNum != null) {
            holder.studentName.setText(student.getName());
            holder.registerNum.setText(student.getEmail());
//            Log.d("StudentListAdapter", "Binding data: " + student.getName() + ", " + student.getRegisterNumber());
        } else {
            Log.e("StudentListAdapter", "TextView is null");
        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView studentName, registerNum;

        public MyViewHolder(@NonNull View itemView,final OnItemClickListener listener) {
            super(itemView);

            studentName = itemView.findViewById(R.id.studentName);
            registerNum = itemView.findViewById(R.id.registerNum);

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
