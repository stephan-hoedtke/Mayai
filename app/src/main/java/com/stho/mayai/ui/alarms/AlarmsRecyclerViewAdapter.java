package com.stho.mayai.ui.alarms;

import android.content.Context;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.stho.mayai.Alarm;
import com.stho.mayai.Helpers;
import com.stho.mayai.IAlarms;
import com.stho.mayai.MayaiWorker;
import com.stho.mayai.R;
import com.stho.mayai.databinding.FragmentAlarmsEntryBinding;


import java.util.Objects;

public class AlarmsRecyclerViewAdapter extends RecyclerView.Adapter<AlarmsRecyclerViewAdapter.ViewHolder> {

    private final FragmentActivity activity;
    private final Context context;
    private final IAlarms alarms;

    AlarmsRecyclerViewAdapter(FragmentActivity activity, IAlarms alarms) {
        this.activity = activity;
        this.context = activity.getApplicationContext();
        this.alarms = alarms;
        this.setHasStableIds(true);
    }

    @Override
    public long getItemId(int position) {
        final Alarm alarm = getAlarm(position);
        return alarm.getId();
    }

    private @NonNull Alarm getAlarm(final int position) {
        return Objects.requireNonNull(alarms.get(position));
    }

    @Override
    public @NonNull ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final Context context = parent.getContext();
        final LayoutInflater inflater = LayoutInflater.from(context);
        final FragmentAlarmsEntryBinding binding = FragmentAlarmsEntryBinding.inflate(inflater, parent, false);
        return new ViewHolder(context, binding);
    }

    @Override
    public void onBindViewHolder(final @NonNull ViewHolder holder, final int position) {
        final Alarm alarm = getAlarm(position);
        holder.binding.image.setAlarm(alarm);
        holder.binding.time.setText(alarm.getTriggerTimeAsString());
        holder.binding.status.setText(alarm.getStatusName());
        holder.binding.name.setText(alarm.getName());
        if (alarm.isPending()) {
            holder.binding.textViewRemainingTime.setText(Helpers.getSecondsAsString(alarm.getRemainingSeconds()));
            holder.binding.textViewRemainingTime.setTextColor(ContextCompat.getColor(context, alarm.isHot() ? R.color.primaryAccentTextColor : R.color.primaryTextColor));
        } else {
            holder.binding.textViewRemainingTime.setText("");
        }
        holder.itemView.setOnLongClickListener(view -> {
            editItem(holder.getAdapterPosition());
            return true;
        });
        holder.itemView.setOnTouchListener((view, motionEvent) -> {
            holder.gestureDetector.onTouchEvent(motionEvent);
            view.performClick();
            return false;
        });
        holder.gestureDetector.setIsLongpressEnabled(true);
        holder.gestureDetector.setOnDoubleTapListener(new GestureDetector.OnDoubleTapListener() {
            @Override
            public boolean onSingleTapConfirmed(MotionEvent motionEvent) {
                return false;
            }

            @Override
            public boolean onDoubleTap(MotionEvent motionEvent) {
                final int currentPosition = holder.getAdapterPosition();
                editItem(currentPosition);
                return false;
            }

            @Override
            public boolean onDoubleTapEvent(MotionEvent motionEvent) {
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return alarms.size();
    }

    void deleteItem(int position) {
        final Alarm alarm = getAlarm(position);
        MayaiWorker.build(context).delete(alarm);
        notifyItemRemoved(position);
        showUndoSnackBar(position, alarm);
    }

    private void showUndoSnackBar(final int position, final Alarm alarm) {
        final View container = activity.findViewById(R.id.container);
        final String text = activity.getString(R.string.text_alarm_deleted);
        final Snackbar snackbar = Snackbar.make(container, text, Snackbar.LENGTH_LONG);
        final String labelUndo = activity.getString(R.string.label_undo);
        snackbar.setAction(labelUndo, view -> undoDelete(position, alarm));
        snackbar.setActionTextColor(ContextCompat.getColor(context, R.color.secondaryAccentTextColor));
        snackbar.setBackgroundTint(ContextCompat.getColor(context, R.color.secondaryDarkColor));
        snackbar.setTextColor(ContextCompat.getColor(context, R.color.secondaryTextColor));
        snackbar.show();
    }

    private void undoDelete(final int position, final Alarm alarm) {
        MayaiWorker.build(context).undoDelete(position, alarm);
        notifyItemInserted(position);
    }

    private void editItem(int position) {
        final Alarm alarm = getAlarm(position);
        MayaiWorker.build(context).open(alarm);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final FragmentAlarmsEntryBinding binding;
        final GestureDetector gestureDetector;

        ViewHolder(Context context, FragmentAlarmsEntryBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            this.gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener());
        }
    }
}


