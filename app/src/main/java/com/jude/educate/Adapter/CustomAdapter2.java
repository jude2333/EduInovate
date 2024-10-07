package com.jude.educate.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.jude.educate.Assignment.AssignmentActivity;
import com.jude.educate.DataModel.DataModel2;
import com.jude.educate.Poll.PollActivity;
import com.jude.educate.Progress.ProgressActivity;
import com.jude.educate.R;
import com.jude.educate.StudyMaterial.StudyMaterial;

import java.util.ArrayList;

public class CustomAdapter2 extends RecyclerView.Adapter<CustomAdapter2.ViewHolder> {
    private ArrayList<DataModel2> dataSet;
    private Context mContext;

    public CustomAdapter2(ArrayList<DataModel2> dataSet, Context mContext) {
        this.dataSet = dataSet;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cardview_main_secondry, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DataModel2 dataModel = dataSet.get(position);
        holder.textViewName.setText(dataModel.getHeading());
        Glide.with(mContext)
                .load(dataModel.getImage())
                .apply(RequestOptions.bitmapTransform(new RoundedCorners(20)))
                .into(holder.imageView);

        // Handle item click for navigating to specific activities
        holder.itemView.setOnClickListener(v -> {
            switch (dataModel.getId()) {
                case 1:
                    mContext.startActivity(new Intent(mContext, AssignmentActivity.class));
                    break;
                case 2:
                   mContext.startActivity(new Intent(mContext, PollActivity.class));
                    break;
                case 3:
                    mContext.startActivity(new Intent(mContext,  StudyMaterial.class));
                    break;
                case 4:
                    mContext.startActivity(new Intent(mContext, ProgressActivity.class));
                    break;

                default:
                    Toast.makeText(mContext, "Unknown action", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textViewName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            textViewName = itemView.findViewById(R.id.textViewName);
        }
    }
}
