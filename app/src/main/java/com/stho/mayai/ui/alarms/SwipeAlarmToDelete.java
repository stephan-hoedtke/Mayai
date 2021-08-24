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
@SuppressWarnings("SpellCheckingInspection")
public class SwipeAlarmToDelete extends ItemTouchHelper.SimpleCallback {

    private final AlarmsRecyclerViewAdapter adapter;

    public SwipeAlarmToDelete(AlarmsRecyclerViewAdapter adapter) {
        super(0, LEFT | RIGHT);
        this.adapter = adapter;
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        return true;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        int position = viewHolder.getAdapterPosition();
        if (direction == LEFT || direction == RIGHT) {
            adapter.deleteItem(position);
        }
    }

    @Override
    public void onSelectedChanged(@Nullable RecyclerView.ViewHolder viewHolder, int actionState) {
        if (viewHolder != null) {
            final View foreground = ((AlarmsRecyclerViewAdapter.ViewHolder) viewHolder).binding.foreground;
            getDefaultUIUtil().onSelected(foreground);
        }
    }

    @Override
    public void onChildDrawOver(@NonNull Canvas c, @NonNull RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        final View foreground = ((AlarmsRecyclerViewAdapter.ViewHolder) viewHolder).binding.foreground;
        foreground.setBackgroundColor(ContextCompat.getColor(foreground.getContext(), isCurrentlyActive ? R.color.colorSelection : R.color.colorBackground));
        getDefaultUIUtil().onDrawOver(c, recyclerView, foreground, dX, dY, actionState, isCurrentlyActive);
    }

    @Override
    public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        final View foreground = ((AlarmsRecyclerViewAdapter.ViewHolder) viewHolder).binding.foreground;
        getDefaultUIUtil().clearView(foreground);
    }

    @Override
    public void onChildDraw(@NotNull Canvas c, @NotNull RecyclerView recyclerView, @NotNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        final View foreground = ((AlarmsRecyclerViewAdapter.ViewHolder) viewHolder).binding.foreground;
        getDefaultUIUtil().onDraw(c, recyclerView, foreground, dX, dY, actionState, isCurrentlyActive);
    }
}

