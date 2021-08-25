package com.stho.mayai.ui.alarms;

import static androidx.recyclerview.widget.ItemTouchHelper.LEFT;
import static androidx.recyclerview.widget.ItemTouchHelper.RIGHT;

import android.graphics.Canvas;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.stho.mayai.R;

import org.jetbrains.annotations.NotNull;

/*
    See:
        https://medium.com/@zackcosborn/step-by-step-recyclerview-swipe-to-delete-and-undo-7bbae1fce27e
        https://www.androidhive.info/2017/09/android-recyclerview-swipe-delete-undo-using-itemtouchhelper/
 */
public class SwipeAlarmToDelete extends ItemTouchHelper.SimpleCallback {

    private final AlarmsRecyclerViewAdapter adapter;

    public SwipeAlarmToDelete(final @NonNull AlarmsRecyclerViewAdapter adapter) {
        super(0, LEFT | RIGHT);
        this.adapter = adapter;
    }

    @Override
    public boolean onMove(final @NonNull RecyclerView recyclerView, final @NonNull RecyclerView.ViewHolder viewHolder, final @NonNull RecyclerView.ViewHolder target) {
        return true;
    }

    @Override
    public void onSwiped(final @NonNull RecyclerView.ViewHolder viewHolder, final int direction) {
        final int position = viewHolder.getAdapterPosition();
        if (direction == LEFT || direction == RIGHT) {
            adapter.deleteItem(position);
        }
    }

    @Override
    public void onSelectedChanged(final @Nullable RecyclerView.ViewHolder viewHolder, final int actionState) {
        if (viewHolder != null) {
            final View foreground = ((AlarmsRecyclerViewAdapter.ViewHolder) viewHolder).binding.foreground;
            getDefaultUIUtil().onSelected(foreground);
        }
    }

    @Override
    public void onChildDrawOver(final @NonNull Canvas c, final @NonNull RecyclerView recyclerView, final @NonNull RecyclerView.ViewHolder viewHolder, final float dX, final float dY, int actionState, final boolean isCurrentlyActive) {
        final View foreground = ((AlarmsRecyclerViewAdapter.ViewHolder) viewHolder).binding.foreground;
        foreground.setBackgroundColor(ContextCompat.getColor(foreground.getContext(), isCurrentlyActive ? R.color.colorSelection : R.color.colorBackground));
        getDefaultUIUtil().onDrawOver(c, recyclerView, foreground, dX, dY, actionState, isCurrentlyActive);
    }

    @Override
    public void clearView(final @NonNull RecyclerView recyclerView, final @NonNull RecyclerView.ViewHolder viewHolder) {
        final View foreground = ((AlarmsRecyclerViewAdapter.ViewHolder) viewHolder).binding.foreground;
        getDefaultUIUtil().clearView(foreground);
    }

    @Override
    public void onChildDraw(final @NotNull Canvas c, final @NotNull RecyclerView recyclerView, final @NotNull RecyclerView.ViewHolder viewHolder, final float dX, final float dY, final int actionState, final boolean isCurrentlyActive) {
        final View foreground = ((AlarmsRecyclerViewAdapter.ViewHolder) viewHolder).binding.foreground;
        getDefaultUIUtil().onDraw(c, recyclerView, foreground, dX, dY, actionState, isCurrentlyActive);
    }
}

