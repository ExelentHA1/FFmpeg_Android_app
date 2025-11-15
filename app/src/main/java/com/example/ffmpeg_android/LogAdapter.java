package com.example.ffmpeg_android;

import android.view.ViewGroup;
import android.widget.TextView;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class LogAdapter extends RecyclerView.Adapter<LogAdapter.LogViewHolder> {

    private final List<String> logs = new ArrayList<>();

    public static class LogViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        public LogViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = (TextView) itemView;
        }
    }

    @NonNull
    @Override
    public LogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        TextView tv = new TextView(parent.getContext());
        tv.setPadding(8, 4, 8, 4);
        tv.setTextSize(12);
        return new LogViewHolder(tv);
    }

    @Override
    public void onBindViewHolder(@NonNull LogViewHolder holder, int position) {
        holder.textView.setText(logs.get(position));
    }

    @Override
    public int getItemCount() {
        return logs.size();
    }

    // Add line to console
    public void addLog(String line) {
        logs.add(line);
        notifyItemInserted(logs.size() - 1);
    }
}

