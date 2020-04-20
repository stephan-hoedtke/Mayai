package com.stho.mayai.ui.alarms;

import android.content.ClipData;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.stho.mayai.Alarm;
import com.stho.mayai.IAlarms;
import com.stho.mayai.MayaiWorker;
import com.stho.mayai.R;

import org.jetbrains.annotations.NotNull;

public class AlarmsRecyclerViewAdapter extends RecyclerView.Adapter<AlarmsRecyclerViewAdapter.ViewHolder> {

    private final FragmentActivity activity;
    private final Context context;
    private final IAlarms alarms;

    AlarmsRecyclerViewAdapter(FragmentActivity activity, IAlarms alarms) {
        this.activity = activity;
        this.context = activity.getApplicationContext();
        this.alarms = alarms;
    }

    public Context getContext() {
        return context;
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
        holder.itemView.setOnLongClickListener(view -> {
            editItem(position);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return alarms.size();
    }

    void deleteItem(int position) {
        Alarm alarm = alarms.get(position);
        MayaiWorker.build(context).delete(alarm);
        // notify the item removed by position
        // to perform recycler view delete animations
        // NOTE: don't call notifyDataSetChanged()
        notifyItemRemoved(position);
        notifyDataSetChanged();
        showUndoSnackBar(position, alarm);
    }

    private void showUndoSnackBar(final int position, final Alarm alarm) {

        View container = activity.findViewById(R.id.container);
        Snackbar snackbar = Snackbar.make(container, "Alarm was deleted", Snackbar.LENGTH_LONG);
        snackbar.setAction("Undo", view -> undoDelete(position, alarm));
        snackbar.setActionTextColor(Color.YELLOW);
        snackbar.show();
    }

    private void undoDelete(final int position, final Alarm alarm) {
        MayaiWorker.build(context).undoDelete(position, alarm);
        notifyItemInserted(position);
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
        final ConstraintLayout viewBackground;
        final ConstraintLayout viewForeground;
        Alarm alarm;

        ViewHolder(View view) {
            super(view);
            this.view = view;
            this.imageView = view.findViewById(R.id.image);
            this.timeView = view.findViewById(R.id.time);
            this.statusView = view.findViewById(R.id.status);
            this.nameView = view.findViewById(R.id.name);
            this.viewBackground = view.findViewById(R.id.view_background);
            this.viewForeground = view.findViewById(R.id.view_foreground);
        }
    }
}


