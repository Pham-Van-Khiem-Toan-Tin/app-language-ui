package com.example.myapplication.viewholder;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;

public class GrammarViewHolder extends RecyclerView.ViewHolder {
    public TextView titleText;
    public GrammarViewHolder(@NonNull View itemView) {
        super(itemView);
        titleText = itemView.findViewById(R.id.grammar_item_title);
    }
}
