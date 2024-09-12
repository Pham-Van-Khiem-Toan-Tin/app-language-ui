package com.example.myapplication.adapter;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.model.Grammar;
import com.example.myapplication.viewholder.GrammarViewHolder;

import java.util.List;

public class GrammarAdapter extends RecyclerView.Adapter<GrammarViewHolder> {
    Context context;
    List<Grammar> grammars;
    OnItemClickListener onItemClickListener;

    public GrammarAdapter(Context context, List<Grammar> grammars) {
        this.context = context;
        this.grammars = grammars;
    }
    public interface OnItemClickListener {
        void onItemClick(Grammar grammar, int position);
    }
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }
    @NonNull
    @Override
    public GrammarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new GrammarViewHolder(LayoutInflater.from(context).inflate(R.layout.grammar_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull GrammarViewHolder holder, int position) {
        final Grammar grammar = grammars.get(position);
        holder.titleText.setText(grammar.getTitle());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int adapterPosition = holder.getAdapterPosition();
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(grammar, adapterPosition);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return grammars.size();
    }
}
