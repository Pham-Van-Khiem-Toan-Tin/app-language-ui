package com.example.myapplication.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.model.Exercise;
import com.example.myapplication.viewholder.ExerciseViewHolder;

import java.util.List;

public class ExerciserApdater extends RecyclerView.Adapter<ExerciseViewHolder> {
    Context context;
    List<Exercise> exercises;
    ExerciserApdater.OnItemClickListener onItemClickListener;
    public ExerciserApdater(Context context, List<Exercise> exercises) {
        this.context = context;
        this.exercises = exercises;
    }
    public interface OnItemClickListener {
        void onItemClick(Exercise exercise, int position);
    }
    public void setOnItemClickListener(ExerciserApdater.OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    @NonNull
    @Override
    public ExerciseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ExerciseViewHolder(LayoutInflater.from(context).inflate(R.layout.exercise_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ExerciseViewHolder holder, int position) {
        final Exercise exercise = exercises.get(position);
        holder.titleViewText.setText(exercise.getTitle());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int adapterPosition = holder.getAdapterPosition();
                if (onItemClickListener !=null) {
                    onItemClickListener.onItemClick(exercise, adapterPosition);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return exercises.size();
    }



}
