package com.example.myapplication.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.model.Message;
import com.example.myapplication.viewholder.MessageViewHolder;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageViewHolder> {
    private List<Message> messages;
    private String currentUserId;
    Context context;
    String role;

    public MessageAdapter( Context context,List<Message> messages, String currentUserId, String role) {
        this.messages = messages;
        this.currentUserId = currentUserId;
        this.context = context;
        this.role = role;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MessageViewHolder(LayoutInflater.from(context).inflate(R.layout.message_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Message message = messages.get(position);
        String idCompare = message.getSendId();
        if (idCompare.equals(currentUserId)) {
            // Người gửi
            holder.sentMessageContainer.setVisibility(View.VISIBLE);
            holder.receivedMessageContainer.setVisibility(View.GONE);
            holder.sentMessage.setText(message.getMessage());
        } else {
            // Người nhận
            holder.sentMessageContainer.setVisibility(View.GONE);
            holder.receivedMessageContainer.setVisibility(View.VISIBLE);
            holder.receivedMessage.setText(message.getMessage());
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }
}
