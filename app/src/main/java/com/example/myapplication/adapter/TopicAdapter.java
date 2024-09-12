package com.example.myapplication.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.model.Topic;
import com.example.myapplication.viewholder.TopicViewHolder;

import java.util.List;

public class TopicAdapter extends RecyclerView.Adapter<TopicViewHolder> {
    Context context;
    List<Topic> topics;
    TopicAdapter.OnItemClickListener onItemClickListener;

    public TopicAdapter(Context context, List<Topic> topics) {
        this.context = context;
        this.topics = topics;
    }

    public interface OnItemClickListener {
        void onItemClick(Topic topic, int position);
    }
    public void setOnItemClickListener(TopicAdapter.OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    @NonNull
    @Override
    public TopicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new TopicViewHolder(LayoutInflater.from(context).inflate(R.layout.topic_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull TopicViewHolder holder, int position) {
        final Topic topic = topics.get(position);
        holder.titleText.setText(topic.getName());
        holder.descriptionText.setText(topic.getDescription());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int adapterPosition = holder.getAdapterPosition();
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(topic, adapterPosition);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return topics.size();
    }
}
