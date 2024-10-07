package com.jude.educate.Adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.jude.educate.Assignment.AssignmentActivity;
import com.jude.educate.Assignment.AssignmentCreationActivity;
import com.jude.educate.DataModel.DataModel1;
import com.jude.educate.DataModel.DataModel2;
import com.jude.educate.Poll.PollActivity;
import com.jude.educate.Poll.PollCreationActivity;
import com.jude.educate.Progress.ProgressActivity;
import com.jude.educate.R;
import com.jude.educate.StudyMaterial.StudyMaterial;
import com.jude.educate.StudyMaterial.StudyMaterialCreation;

import java.util.ArrayList;

public class CustomAdapter1 extends RecyclerView.Adapter<CustomAdapter1.MyViewHolder> {

    // Variables
    private ArrayList<DataModel1> dataSet;
    private Context mContext;


    // Constructor:


    public CustomAdapter1(ArrayList<DataModel1> dataSet, Context mContext) {
        this.dataSet = dataSet;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cardview_main, parent, false);

        // We Will Create the Item_cardview layout
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;

    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        DataModel1 dataModel = dataSet.get(position);
        TextView textViewName = holder.heading;
        AppCompatButton compatButton = holder.appCompatButton;
        ImageView imageView = holder.imageView;

        // Adding the data for textViews
        textViewName.setText(dataSet.get(position).getHeading());
        compatButton.setText(dataSet.get(position).getButtonText());

        // Load the image using Glide with rounded corners
        Glide.with(mContext)
                .load(dataSet.get(position).getImage())
                .apply(RequestOptions.bitmapTransform(new RoundedCorners(20)))
                .into(imageView);

        // Prevent multiple rapid clicks using debounce mechanism
        holder.itemView.setOnClickListener(v -> {
            // Disable multiple clicks rapidly
            holder.itemView.setEnabled(false);

            // Use a handler to re-enable clicks after 1 second
            new Handler(Looper.getMainLooper()).postDelayed(() -> holder.itemView.setEnabled(true), 1000);

            // Handle item click for navigating to specific activities
            switch (dataModel.getId()) {
                case 1:
                    mContext.startActivity(new Intent(mContext, AssignmentCreationActivity.class));
                    break;
                case 2:
                    mContext.startActivity(new Intent(mContext, PollCreationActivity.class));
                    break;
                case 3:
                    mContext.startActivity(new Intent(mContext, StudyMaterialCreation.class));
                    break;
                case 4:
                    mContext.startActivity(new Intent(mContext, ProgressActivity.class));
                    break;
                default:
                    // Removed Toast for unknown actions to reduce the toast queue overflow
                    Log.w("ActivitySwitch", "Unknown action for item ID: " + dataModel.getId());
                    break;
            }
        });
    }


    @Override
    public int getItemCount() {
        return dataSet.size();
    }


    // ViewHolder Class
    public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView imageView;
        TextView heading;
        AppCompatButton appCompatButton;

        //ItemClickListener itemClickListener;  saving it for later use
        public MyViewHolder(View itemView) {
            super(itemView);
            this.imageView = itemView.findViewById(R.id.imageView);
            this.heading = itemView.findViewById(R.id.textViewName);
            this.appCompatButton = itemView.findViewById(R.id.buttonEvent);
            //itemView.setOnClickListener((View.OnClickListener) this);
        }

        @Override
        public void onClick(View view) {

        }
    }

}
