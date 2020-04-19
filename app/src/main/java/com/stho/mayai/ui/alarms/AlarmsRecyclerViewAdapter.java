package com.stho.mayai.ui.alarms;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.stho.mayai.Alarm;
import com.stho.mayai.IAlarms;
import com.stho.mayai.MayaiWorker;
import com.stho.mayai.R;

import org.jetbrains.annotations.NotNull;

public class AlarmsRecyclerViewAdapter extends RecyclerView.Adapter<AlarmsRecyclerViewAdapter.ViewHolder> {

    private final IAlarms alarms;
    private final Context context;

    AlarmsRecyclerViewAdapter(Context context, IAlarms alarms) {
        this.context = context;
        this.alarms = alarms;
    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_alarms_entry, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.alarm = alarms.get(position);
        holder.imageView.setImageResource(holder.alarm.getNotificationIconId());
        holder.timeView.setText(holder.alarm.getTriggerTimeAsString());
        holder.statusView.setText(holder.alarm.getStatusName());
        holder.nameView.setText(holder.alarm.getName());
    }

    @Override
    public int getItemCount() {
        return alarms.size();
    }

    void deleteItem(int position) {
        Alarm alarm = alarms.get(position);
        MayaiWorker.build(context).delete(alarm);
        notifyItemRemoved(position);
        notifyDataSetChanged();
    }

    void editItem(int position) {
        Alarm alarm = alarms.get(position);
        MayaiWorker.build(context).open(alarm);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final View view;
        final ImageView imageView;
        final TextView timeView;
        final TextView statusView;
        final TextView nameView;
        Alarm alarm;

        ViewHolder(View view) {
            super(view);
            this.view = view;
            this.imageView = view.findViewById(R.id.image);
            this.timeView = view.findViewById(R.id.time);
            this.statusView = view.findViewById(R.id.status);
            this.nameView = view.findViewById(R.id.name);
        }
    }
}


