package com.stho.mayai.ui.alarms;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.stho.mayai.Alarms;
import com.stho.mayai.R;
import com.stho.mayai.Summary;
import com.stho.mayai.ViewAnimation;
import com.stho.mayai.databinding.FragmentAlarmsBinding;

import java.util.Objects;


public class AlarmsFragment extends Fragment {

    private ViewAnimation animation;
    private AlarmsViewModel viewModel;
    private FragmentAlarmsBinding binding;
    private final Handler handler = new Handler(Looper.getMainLooper());

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = AlarmsViewModel.build(this);
        setHasOptionsMenu(true);
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAlarmsBinding.inflate(inflater, container, false);
        binding.list.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.headlineFrame.setOnClickListener(view -> animation.hide());

        final AlarmsRecyclerViewAdapter adapter = new AlarmsRecyclerViewAdapter(requireActivity(), viewModel.getAlarms());
        binding.list.setAdapter(adapter);

        final ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SwipeAlarmToDelete(adapter));
        itemTouchHelper.attachToRecyclerView(binding.list);

        final DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL);
        binding.list.addItemDecoration(dividerItemDecoration);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel.getAlarmsLD().observe(getViewLifecycleOwner(), this::updateAlarms);
        viewModel.getSummaryLD().observe(getViewLifecycleOwner(), this::updateSummary);
        animation = ViewAnimation.build(binding.headlineFrame);
    }

    @Override
    public void onResume() {
        super.onResume();
        prepareUpdateHandler();
    }

    private void prepareUpdateHandler() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                update();
                handler.postDelayed(this, DELAY_MILLIS);
            }
        }, INITIAL_DELAY_MILLIS);
    }

    @Override
    public void onPause() {
        super.onPause();
        handler.removeCallbacksAndMessages(null);
        animation.removeCallbacks();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_hint) {
            animation.toggle();
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateAlarms(final @NonNull Alarms alarms) {
        update();
    }

    private void updateSummary(final @NonNull Summary summary) {
        updateActionBar(summary);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void update() {
        viewModel.updateSummary();
        Objects.requireNonNull(binding.list.getAdapter()).notifyDataSetChanged();
    }

    private void updateActionBar(final @NonNull Summary summary) {
        final ActionBar actionBar = ((AppCompatActivity) requireActivity()).getSupportActionBar();
        if (actionBar != null) {
            final int counter = summary.getPendingAlarmsCounter();
            actionBar.setTitle(getTitleString(counter));
            actionBar.setSubtitle(null);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }
    }

    private @NonNull String getTitleString(final int counter) {
        if (counter == 0) {
            return getString(R.string.title_alarms);
        } else {
            return getString(R.string.title_alarms_param, counter);
        }
    }

    private static final int DELAY_MILLIS = 1000;
    private static final int INITIAL_DELAY_MILLIS = 100;
}

