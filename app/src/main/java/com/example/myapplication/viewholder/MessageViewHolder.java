package com.example.myapplication.viewholder;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;

public class MessageViewHolder extends RecyclerView.ViewHolder {
    public LinearLayout sentMessageContainer;
    public LinearLayout receivedMessageContainer;
    public TextView sentMessage;
    public TextView receivedMessage;
    public MessageViewHolder(@NonNull View itemView) {
        super(itemView);
        sentMessageContainer = itemView.findViewById(com.example.myapplication.R.id.sent_message_container);
        receivedMessageContainer = itemView.findViewById(R.id.received_message_container);
        sentMessage = itemView.findViewById(R.id.sent_message);
        receivedMessage = itemView.findViewById(R.id.received_message);
    }
}
