package com.example.myapplication.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.model.ChatAvailable;
import com.example.myapplication.viewholder.ChatAvailableViewHolder;

import java.util.List;

public class ChatAvailableAdapter extends RecyclerView.Adapter<ChatAvailableViewHolder> {
    Context context;
    List<ChatAvailable> chatAvailables;
    ChatAvailableAdapter.OnItemClickListener onItemClickListener;
    public ChatAvailableAdapter(Context context, List<ChatAvailable> chatAvailables) {
        this.context = context;
        this.chatAvailables = chatAvailables;
    }
    public interface OnItemClickListener {
        void onItemClick(ChatAvailable chatAvailable, int position);
    }
    public void setOnItemClickListener(ChatAvailableAdapter.OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }
    @NonNull
    @Override
    public ChatAvailableViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ChatAvailableViewHolder(LayoutInflater.from(context).inflate(R.layout.chat_available_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ChatAvailableViewHolder holder, int position) {
        final ChatAvailable chatAvailable = chatAvailables.get(position);
        holder.studentTextView.setText(chatAvailable.getStudentName());
        holder.lastMessageTextView.setText(chatAvailable.getMessage());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int adapterPosition = holder.getAdapterPosition();
                onItemClickListener.onItemClick(chatAvailable, adapterPosition);
            }
        });
    }

    @Override
    public int getItemCount() {
        return chatAvailables.size();
    }
}
