package com.example.myapplication.viewholder;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;

public class TopicViewHolder extends RecyclerView.ViewHolder {
    public TextView titleText, descriptionText;
    public TopicViewHolder(@NonNull View itemView) {
        super(itemView);
        titleText = itemView.findViewById(R.id.topic_item_name);
        descriptionText = itemView.findViewById(R.id.topic_item_des);
    }
}
