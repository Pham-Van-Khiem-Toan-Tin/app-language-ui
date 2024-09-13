package com.example.myapplication.viewholder;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;

public class ChatAvailableViewHolder extends RecyclerView.ViewHolder {
    public TextView studentTextView, lastMessageTextView;
    public ChatAvailableViewHolder(@NonNull View itemView) {
        super(itemView);
        studentTextView = itemView.findViewById(R.id.chat_item_student);
        lastMessageTextView = itemView.findViewById(R.id.chat_item_last_message);
    }
}
