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
import com.jude.educate.Poll.PollCreationActivity;
import com.jude.educate.Progress.ProgressActivity;
import com.jude.educate.R;
import com.jude.educate.StudyMaterial.StudyMaterialCreation;

import java.util.ArrayList;
public class ViewPager2Adapter extends RecyclerView.Adapter<ViewPager2Adapter.ViewHolder> {
    private ArrayList<DataModel1> dataSet;
    private Context mContext;

    public ViewPager2Adapter(ArrayList<DataModel1> dataSet, Context mContext) {
        this.dataSet = dataSet;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cardview_main, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DataModel1 dataModel = dataSet.get(position);
        holder.textViewName.setText(dataModel.getHeading());
        holder.appCompatButton.setText(dataModel.getButtonText());
        Glide.with(mContext)
                .load(dataModel.getImage())
                .apply(RequestOptions.bitmapTransform(new RoundedCorners(20)))
                .into(holder.imageView);

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

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textViewName;
        AppCompatButton appCompatButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            textViewName = itemView.findViewById(R.id.textViewName);
            appCompatButton = itemView.findViewById(R.id.buttonEvent);
        }
    }
}
