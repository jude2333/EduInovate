package com.jude.educate.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jude.educate.Model.Poll;
import com.jude.educate.R;

import java.io.Serializable;
import java.util.List;

public class PollAdapter extends RecyclerView.Adapter<PollAdapter.PollViewHolder> {

    private List<Poll> pollList;
    private Context context;

    private PollAdapter.OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }



    public void setOnItemClickListener(PollAdapter.OnItemClickListener listener) {
        this.mListener = listener;
    }



    // Constructor
    public PollAdapter(List<Poll> pollList, Context context) {
        this.pollList = pollList;
        this.context = context;
    }

    @NonNull
    @Override
    public PollViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.poll_card, parent, false);
        return new PollViewHolder(view,mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull PollViewHolder holder, int position) {
        Poll poll = pollList.get(position);

        // Bind the poll data to the views
        holder.pollCodeTextView.setText(poll.getCourseId());
        holder.totalSubmittedTextView.setText("Total Votes: " + String.valueOf(poll.getTotalVotes()));
        holder.pollDateTextView.setText(poll.getDate());

        // Handle click event if necessary
        holder.itemView.setOnClickListener(v -> {
            // Optionally handle poll click
        });
    }

    @Override
    public int getItemCount() {
        return pollList.size();
    }

    // ViewHolder class to hold the card views
    public static class PollViewHolder extends RecyclerView.ViewHolder {
        TextView pollCodeTextView, totalSubmittedTextView, pollDateTextView;

        public PollViewHolder(@NonNull View itemView,final OnItemClickListener listener) {
            super(itemView);
            pollCodeTextView = itemView.findViewById(R.id.pollcode);
            totalSubmittedTextView = itemView.findViewById(R.id.totalSubmitted);
            pollDateTextView = itemView.findViewById(R.id.PollDate);

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
