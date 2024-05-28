package com.jude.educate.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.jude.educate.DataModel.DataModel1;
import com.jude.educate.DataModel.DataModel2;
import com.jude.educate.R;

import java.util.ArrayList;

public class CustomAdapter2 extends RecyclerView.Adapter<CustomAdapter2.MyViewHolder> {

    // Variables
    private ArrayList<DataModel2> dataSet;
    private Context mContext;


    // Constructor:


    public CustomAdapter2(ArrayList<DataModel2> dataSet, Context mContext) {
        this.dataSet = dataSet;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cardview_main_secondry, parent, false);

        // We Will Create the Item_cardview layout
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;

    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        TextView textViewName = holder.heading;
        ImageView imageView = holder.imageView;

        // Adding the data for textViews
        textViewName.setText(dataSet.get(position).getHeading());

        // Adding the data for imageView
        // Adding Glide Library

        Glide.with(mContext)
                .load(dataSet.get(position).getImage())
                .apply(RequestOptions.bitmapTransform
                        (new RoundedCorners(20)))
                .into(imageView);

    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }


    // ViewHolder Class
    public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView imageView;
        TextView heading;

        //ItemClickListener itemClickListener;  saving it for later use
        public MyViewHolder(View itemView) {
            super(itemView);
            this.imageView = itemView.findViewById(R.id.imageView);
            this.heading = itemView.findViewById(R.id.textViewName);
            //itemView.setOnClickListener((View.OnClickListener) this);
        }

        @Override
        public void onClick(View view) {

        }
    }

}