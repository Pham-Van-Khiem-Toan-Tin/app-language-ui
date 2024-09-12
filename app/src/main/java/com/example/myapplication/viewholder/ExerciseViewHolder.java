package com.example.myapplication.viewholder;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;

public class ExerciseViewHolder extends RecyclerView.ViewHolder {
    public TextView titleViewText;
    public ExerciseViewHolder(@NonNull View itemView) {
        super(itemView);
        titleViewText = itemView.findViewById(R.id.exercise_item_title);
    }
}
