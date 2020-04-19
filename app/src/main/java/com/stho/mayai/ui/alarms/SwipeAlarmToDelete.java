package com.stho.mayai.ui.alarms;

import android.text.Layout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import static androidx.recyclerview.widget.ItemTouchHelper.LEFT;
import static androidx.recyclerview.widget.ItemTouchHelper.RIGHT;

/*
    See: https://medium.com/@zackcosborn/step-by-step-recyclerview-swipe-to-delete-and-undo-7bbae1fce27e
 */
public class SwipeAlarmToDelete extends ItemTouchHelper.SimpleCallback {

    private AlarmsRecyclerViewAdapter adapter;

    public SwipeAlarmToDelete(AlarmsRecyclerViewAdapter adapter) {
        super(0,ItemTouchHelper.LEFT | RIGHT);
        this.adapter = adapter;
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        int position = viewHolder.getAdapterPosition();
        switch (direction) {
            case RIGHT:
                adapter.editItem(position);
                break;

            case LEFT:
                adapter.deleteItem(position);
                break;
        }
    }
}

