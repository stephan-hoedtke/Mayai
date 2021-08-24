package com.stho.mayai.ui.showlog;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.stho.mayai.LogEntry;
import com.stho.mayai.R;

import java.util.List;

public class ShowLogRecyclerViewAdapter extends RecyclerView.Adapter<ShowLogRecyclerViewAdapter.ViewHolder> {

    private final List<LogEntry> values;

    public ShowLogRecyclerViewAdapter(List<LogEntry> items) {
        values = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_show_log_entry, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.item = values.get(position);
        holder.timeView.setText(holder.item.getTimeAsString());
        holder.messageView.setText(holder.item.getMessage());
    }

    @Override
    public int getItemCount() {
        return values.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView timeView;
        public final TextView messageView;
        public LogEntry item;

        ViewHolder(View view) {
            super(view);
            this.timeView = view.findViewById(R.id.time);
            this.messageView = view.findViewById(R.id.message);
        }
    }
}


